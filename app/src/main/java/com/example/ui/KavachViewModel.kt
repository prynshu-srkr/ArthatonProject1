package com.example.ui

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ScanHistoryEntity
import com.example.data.ScanRepository
import com.example.domain.LocalFraudHeuristics
import com.example.model.FraudAssessmentResult
import com.example.model.KavachStaticData
import com.example.model.SampleScenario
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

private const val TAG = "KavachViewModel"

sealed interface AiAnalysisState {
    data object Idle : AiAnalysisState
    data object Loading : AiAnalysisState
    data class Success(val result: FraudAssessmentResult) : AiAnalysisState
    data class Error(val message: String, val fallbackResult: FraudAssessmentResult) : AiAnalysisState
}

data class KavachUiState(
    val messageInput: String = "",
    val senderInput: String = "",
    val selectedChannel: String = KavachStaticData.CHANNELS.first(),
    val activeSampleKey: String? = null,
    val isScanning: Boolean = false,
    val hasScannedAtLeastOnce: Boolean = false,
    val currentAssessment: FraudAssessmentResult? = null,
    val aiAnalysisState: AiAnalysisState = AiAnalysisState.Idle,
    val isEmergencyExpanded: Boolean = true,
    val emergencyHighlightTrigger: Int = 0,
    val activeSpeechLang: String? = null, // "en" or "hi" or null
    val isDarkMode: Boolean = false,
    val copiedFeedback: Boolean = false
)

class KavachViewModel(
    private val repository: ScanRepository,
    appContext: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(KavachUiState())
    val uiState: StateFlow<KavachUiState> = _uiState.asStateFlow()

    val scanHistory: StateFlow<List<ScanHistoryEntity>> = repository.allScans
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var activeScanJob: Job? = null

    init {
        initTextToSpeech(appContext.applicationContext)
    }

    private val fraudSchema = Schema.obj(
        properties = mapOf(
            "risk_score" to Schema.integer(description = "Fraud risk score from 0 to 100"),
            "threat_level" to Schema.enumeration(
                values = listOf("Safe", "Suspicious", "High Risk / Scam"),
                description = "Overall threat classification"
            ),
            "red_flags" to Schema.array(
                items = Schema.string(),
                description = "Detected manipulation or scam indicators"
            ),
            "explanation" to Schema.string(description = "Actionable safety explanation in English"),
            "explanation_hi" to Schema.string(description = "Actionable safety explanation in Hindi")
        )
    )

    // Use ultra-fast gemini-3.5-flash-lite for low-latency fraud classification
    private val generativeModel by lazy {
        val todayFormatted = getLiveDateContextString()
        val systemPrompt = """
            You are an expert financial fraud, cybercrime, and phishing detection AI specializing in Indian digital payments, UPI scams, SMS spam, and honeytrap/friendship club scams. Today's date is $todayFormatted.
            IMPORTANT SAFE BANK ALERT RULE: Standard bank debit/credit transaction alerts containing masked account numbers (like A/c XX4821), UPI Ref numbers, available balance (Avl Bal), and official 1800/1860 bank toll-free numbers—with NO external phishing links and NO request for UPI PIN/OTP—are legitimate and MUST be classified as 'Safe' with a risk_score between 5 and 15 and an empty red_flags array. Never flag past/present transaction dates (such as 07-Nov-26) or official 1800 toll-free dispute numbers as red flags.
            Return a concise JSON response with risk_score (0-100), threat_level ('Safe', 'Suspicious', or 'High Risk / Scam'), red_flags (array of strings), explanation (English advice), and explanation_hi (Hindi advice).
        """.trimIndent()

        Firebase.ai.generativeModel(
            modelName = "gemini-3.5-flash-lite",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = fraudSchema
            },
            systemInstruction = content { text(systemPrompt) }
        )
    }

    private fun getLiveDateContextString(): String {
        val formatter = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.forLanguageTag("en-IN"))
        return formatter.format(Date())
    }

    private fun initTextToSpeech(context: Context) {
        try {
            tts = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    isTtsReady = true
                    tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {}

                        override fun onDone(utteranceId: String?) {
                            _uiState.update { it.copy(activeSpeechLang = null) }
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {
                            _uiState.update { it.copy(activeSpeechLang = null) }
                        }

                        override fun onError(utteranceId: String?, errorCode: Int) {
                            _uiState.update { it.copy(activeSpeechLang = null) }
                        }
                    })
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "TextToSpeech initialization unavailable", e)
        }
    }

    fun onMessageInputChanged(newText: String) {
        _uiState.update {
            it.copy(
                messageInput = newText,
                activeSampleKey = null
            )
        }
    }

    fun onSenderInputChanged(newSender: String) {
        _uiState.update { it.copy(senderInput = newSender) }
    }

    fun onChannelSelected(newChannel: String) {
        _uiState.update { it.copy(selectedChannel = newChannel) }
    }

    fun clearInput() {
        stopSpeechPlayback()
        activeScanJob?.cancel()
        _uiState.update {
            it.copy(
                messageInput = "",
                senderInput = "",
                activeSampleKey = null,
                isScanning = false
            )
        }
    }

    fun toggleDarkMode() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    fun toggleEmergencyDrawer() {
        _uiState.update { it.copy(isEmergencyExpanded = !it.isEmergencyExpanded) }
    }

    fun selectSampleScenario(sample: SampleScenario) {
        stopSpeechPlayback()
        _uiState.update {
            it.copy(
                messageInput = sample.text,
                senderInput = sample.sender,
                selectedChannel = sample.channel,
                activeSampleKey = sample.key
            )
        }
        runFullInspection()
    }

    fun loadHistoryEntry(entry: ScanHistoryEntity) {
        stopSpeechPlayback()
        activeScanJob?.cancel()
        val result = entry.toAssessmentResult()
        val isHighOrSuspicious = result.riskScore >= 35 ||
            result.threatLevel == "Suspicious" ||
            result.threatLevel == "High Risk / Scam"

        _uiState.update { state ->
            state.copy(
                messageInput = entry.text,
                senderInput = entry.sender,
                selectedChannel = entry.channel.ifBlank { KavachStaticData.CHANNELS.first() },
                activeSampleKey = null,
                isScanning = false,
                hasScannedAtLeastOnce = true,
                currentAssessment = result,
                aiAnalysisState = AiAnalysisState.Success(result),
                isEmergencyExpanded = if (isHighOrSuspicious) true else state.isEmergencyExpanded,
                emergencyHighlightTrigger = if (isHighOrSuspicious) state.emergencyHighlightTrigger + 1 else state.emergencyHighlightTrigger
            )
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun triggerCopiedFeedback() {
        viewModelScope.launch {
            _uiState.update { it.copy(copiedFeedback = true) }
            kotlinx.coroutines.delay(1800)
            _uiState.update { it.copy(copiedFeedback = false) }
        }
    }

    fun toggleSpeechPlayback(langCode: String) {
        val currentState = _uiState.value
        if (currentState.activeSpeechLang == langCode) {
            stopSpeechPlayback()
            return
        }

        val engine = tts
        if (engine == null || !isTtsReady) {
            _uiState.update { it.copy(activeSpeechLang = null) }
            return
        }

        engine.stop()

        val textToRead = if (langCode == "hi") {
            currentState.currentAssessment?.explanationHi
                ?: "बाईं ओर कोई भी संदिग्ध SMS, WhatsApp संदेश या UPI रिक्वेस्ट पेस्ट करें और पूरी सुरक्षा जानकारी देखने के लिए Check for Scam पर क्लिक करें।"
        } else {
            currentState.currentAssessment?.explanation
                ?: "Paste any suspicious SMS, WhatsApp forward, or UPI collect request above and tap Check for Scam to see a full safety breakdown."
        }

        if (textToRead.isBlank()) return

        val locale = if (langCode == "hi") {
            Locale.forLanguageTag("hi-IN")
        } else {
            Locale.forLanguageTag("en-IN")
        }

        engine.language = locale
        engine.setSpeechRate(if (langCode == "hi") 0.95f else 0.98f)
        engine.setPitch(1.0f)

        _uiState.update { it.copy(activeSpeechLang = langCode) }
        val result = engine.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, "kavach_tts_$langCode")
        if (result == TextToSpeech.ERROR) {
            _uiState.update { it.copy(activeSpeechLang = null) }
        }
    }

    fun stopSpeechPlayback() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping TTS", e)
        }
        _uiState.update { it.copy(activeSpeechLang = null) }
    }

    fun runFullInspection() {
        val current = _uiState.value
        val text = current.messageInput.trim()
        val sender = current.senderInput.trim()
        val channel = current.selectedChannel

        if (text.isEmpty()) return

        stopSpeechPlayback()
        activeScanJob?.cancel()

        // 1. Instant local heuristic evaluation (< 5ms) so UI updates immediately without delay
        val localResult = LocalFraudHeuristics.evaluate(text, sender)
        val isLocalHighOrSuspicious = localResult.riskScore >= 35 ||
            localResult.threatLevel == "Suspicious" ||
            localResult.threatLevel == "High Risk / Scam"

        _uiState.update { state ->
            state.copy(
                isScanning = false,
                hasScannedAtLeastOnce = true,
                currentAssessment = localResult,
                aiAnalysisState = AiAnalysisState.Success(localResult),
                isEmergencyExpanded = if (isLocalHighOrSuspicious) true else state.isEmergencyExpanded,
                emergencyHighlightTrigger = if (isLocalHighOrSuspicious) state.emergencyHighlightTrigger + 1 else state.emergencyHighlightTrigger
            )
        }

        activeScanJob = viewModelScope.launch {
            // Persist the instant result immediately
            repository.saveScanResult(text, sender, channel, localResult)

            // 2. Skip background AI override when local shield has already definitively classified
            // a high-confidence scam (>= 70), a verified safe bank alert, or a built-in sample scenario
            val isVerifiedSafeBank = LocalFraudHeuristics.isVerifiedSafeBankAlert(text, sender)
            if (localResult.riskScore >= 70 || isVerifiedSafeBank || current.activeSampleKey != null) {
                return@launch
            }

            try {
                val todayFormatted = getLiveDateContextString()
                val fullPrompt = """
                    Current Live Date: $todayFormatted
                    Channel: ${channel.ifBlank { "SMS / UPI" }}
                    Sender ID: ${sender.ifBlank { "Unknown" }}
                    Message:
                    ""${'"'}
                    $text
                    ""${'"'}
                """.trimIndent()

                val response = withTimeout(60_000L) {
                    generativeModel.generateContent(fullPrompt)
                }

                val jsonString = response.text ?: throw IllegalStateException("Model returned empty response.")
                val json = JSONObject(jsonString)

                val aiScore = json.optInt("risk_score", localResult.riskScore).coerceIn(0, 100)
                val aiLevel = json.optString("threat_level", localResult.threatLevel)
                val flagsJsonArray = json.optJSONArray("red_flags")
                val aiFlags = mutableListOf<String>()
                if (flagsJsonArray != null) {
                    for (i in 0 until flagsJsonArray.length()) {
                        val f = flagsJsonArray.optString(i)
                        if (f.isNotBlank() &&
                            !f.lowercase().contains("no manipulation") &&
                            !f.lowercase().contains("none")
                        ) {
                            aiFlags.add(f)
                        }
                    }
                }
                val aiExplanation = json.optString("explanation", localResult.explanation)
                val aiExplanationHi = json.optString("explanation_hi", localResult.explanationHi)

                val mergedReport = if (localResult.riskScore > aiScore) {
                    FraudAssessmentResult(
                        riskScore = max(localResult.riskScore, aiScore),
                        threatLevel = localResult.threatLevel,
                        redFlags = (aiFlags + localResult.redFlags).distinct(),
                        explanation = if (aiExplanation.isNotBlank() && localResult.riskScore < 70) aiExplanation else localResult.explanation,
                        explanationHi = if (aiExplanationHi.isNotBlank() && localResult.riskScore < 70) aiExplanationHi else localResult.explanationHi
                    )
                } else {
                    FraudAssessmentResult(
                        riskScore = aiScore,
                        threatLevel = aiLevel,
                        redFlags = aiFlags,
                        explanation = aiExplanation.ifBlank { localResult.explanation },
                        explanationHi = aiExplanationHi.ifBlank { localResult.explanationHi }
                    )
                }

                repository.saveScanResult(text, sender, channel, mergedReport)

                val isHighOrSuspicious = mergedReport.riskScore >= 35 ||
                    mergedReport.threatLevel == "Suspicious" ||
                    mergedReport.threatLevel == "High Risk / Scam"

                _uiState.update { state ->
                    state.copy(
                        isScanning = false,
                        hasScannedAtLeastOnce = true,
                        currentAssessment = mergedReport,
                        aiAnalysisState = AiAnalysisState.Success(mergedReport),
                        isEmergencyExpanded = if (isHighOrSuspicious) true else state.isEmergencyExpanded,
                        emergencyHighlightTrigger = if (isHighOrSuspicious && !isLocalHighOrSuspicious) {
                            state.emergencyHighlightTrigger + 1
                        } else {
                            state.emergencyHighlightTrigger
                        }
                    )
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Gemini fraud inspection timed out", e)
                _uiState.update { state ->
                    state.copy(
                        isScanning = false,
                        aiAnalysisState = AiAnalysisState.Error(
                            message = "AI cloud check timed out after 60s. Showing instant KavachPay local shield result.",
                            fallbackResult = localResult
                        )
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Firebase AI fraud check failed", e)
                _uiState.update { state ->
                    state.copy(
                        isScanning = false,
                        aiAnalysisState = AiAnalysisState.Error(
                            message = e.localizedMessage ?: "AI cloud check unavailable. Showing instant KavachPay local shield result.",
                            fallbackResult = localResult
                        )
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            Log.w(TAG, "Error shutting down TTS", e)
        }
    }

    class Factory(
        private val repository: ScanRepository,
        private val appContext: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return KavachViewModel(repository, appContext) as T
        }
    }
}
