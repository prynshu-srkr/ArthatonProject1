package com.example.data

import com.example.model.FraudAssessmentResult
import kotlinx.coroutines.flow.Flow

class ScanRepository(private val dao: ScanHistoryDao) {
    val allScans: Flow<List<ScanHistoryEntity>> = dao.getAllScans()

    suspend fun saveScanResult(
        text: String,
        sender: String,
        channel: String,
        result: FraudAssessmentResult
    ) {
        val latest = dao.getLatestScan()
        val encodedFlags = ScanHistoryEntity.encodeRedFlags(result.redFlags)
        if (latest != null && latest.text == text && latest.sender == sender) {
            dao.insertScan(
                latest.copy(
                    channel = channel,
                    timestamp = System.currentTimeMillis(),
                    riskScore = result.riskScore,
                    threatLevel = result.threatLevel,
                    redFlagsJson = encodedFlags,
                    explanation = result.explanation,
                    explanationHi = result.explanationHi
                )
            )
        } else {
            dao.insertScan(
                ScanHistoryEntity(
                    text = text,
                    sender = sender,
                    channel = channel,
                    timestamp = System.currentTimeMillis(),
                    riskScore = result.riskScore,
                    threatLevel = result.threatLevel,
                    redFlagsJson = encodedFlags,
                    explanation = result.explanation,
                    explanationHi = result.explanationHi
                )
            )
        }
    }

    suspend fun clearHistory() {
        dao.clearAllScans()
    }
}
