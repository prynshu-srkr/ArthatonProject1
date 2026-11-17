package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.FraudScannerInputCard
import com.example.ui.components.GoldenUpiRuleCarousel
import com.example.ui.components.KavachTopBar
import com.example.ui.components.RecentScannedStatementsCard
import com.example.ui.components.SafetyAssessmentCard
import com.example.ui.theme.DarkBg
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.SlateBgLight
import com.example.ui.theme.Teal600
import com.example.ui.theme.Zinc500
import com.example.ui.theme.Zinc800
import com.example.ui.theme.Zinc950

@Composable
fun KavachPayScreen(
    viewModel: KavachViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scanHistory by viewModel.scanHistory.collectAsStateWithLifecycle()
    val isDark = uiState.isDarkMode
    val listState = rememberLazyListState()

    // Scroll to the Safety Assessment / Emergency Checklist card when a high-risk or suspicious scan finishes
    LaunchedEffect(uiState.emergencyHighlightTrigger) {
        if (uiState.emergencyHighlightTrigger > 0) {
            listState.animateScrollToItem(index = 2)
        }
    }

    val baseBgColor = if (isDark) DarkBg else SlateBgLight

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            KavachTopBar(
                isDark = isDark,
                onToggleTheme = viewModel::toggleDarkMode
            )
        },
        containerColor = baseBgColor,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .drawBehind {
                    // Subtle radial ambient glow matching the web design's radial-gradient background
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Teal600.copy(alpha = if (isDark) 0.12f else 0.06f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.12f, 0f),
                            radius = size.width * 0.7f
                        )
                    )
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Emerald500.copy(alpha = if (isDark) 0.08f else 0.055f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.88f, size.height * 0.06f),
                            radius = size.width * 0.65f
                        )
                    )
                },
            contentAlignment = Alignment.TopCenter
        ) {
            val isExpandedScreen = maxWidth >= 840.dp

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 1152.dp)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item(key = "top_spacer_carousel") {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        GoldenUpiRuleCarousel(isDark = isDark)
                    }
                }

                if (isExpandedScreen) {
                    // Canonical 2-Column Expanded Layout (7 cols left, 5 cols right)
                    item(key = "expanded_two_pane") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(
                                modifier = Modifier.weight(0.58f),
                                verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                FraudScannerInputCard(
                                    messageInput = uiState.messageInput,
                                    onMessageChange = viewModel::onMessageInputChanged,
                                    senderInput = uiState.senderInput,
                                    onSenderChange = viewModel::onSenderInputChanged,
                                    selectedChannel = uiState.selectedChannel,
                                    onChannelSelect = viewModel::onChannelSelected,
                                    activeSampleKey = uiState.activeSampleKey,
                                    onSelectSample = viewModel::selectSampleScenario,
                                    onClearText = viewModel::clearInput,
                                    onAnalyzeClick = viewModel::runFullInspection,
                                    isScanning = uiState.isScanning,
                                    isDark = isDark
                                )

                                RecentScannedStatementsCard(
                                    scanHistory = scanHistory,
                                    onSelectHistoryItem = viewModel::loadHistoryEntry,
                                    onClearHistory = viewModel::clearAllHistory,
                                    isDark = isDark
                                )
                            }

                            Column(modifier = Modifier.weight(0.42f)) {
                                SafetyAssessmentCard(
                                    hasScanned = uiState.hasScannedAtLeastOnce,
                                    assessment = uiState.currentAssessment,
                                    aiAnalysisState = uiState.aiAnalysisState,
                                    activeSpeechLang = uiState.activeSpeechLang,
                                    onToggleSpeech = viewModel::toggleSpeechPlayback,
                                    copiedFeedback = uiState.copiedFeedback,
                                    onShareAdvice = viewModel::triggerCopiedFeedback,
                                    isEmergencyExpanded = uiState.isEmergencyExpanded,
                                    onToggleEmergencyDrawer = viewModel::toggleEmergencyDrawer,
                                    emergencyHighlightTrigger = uiState.emergencyHighlightTrigger,
                                    isDark = isDark
                                )
                            }
                        }
                    }
                } else {
                    // Handheld Single-Column Layout: Input -> Safety Assessment -> Recent Scanned Statements
                    item(key = "mobile_input_card") {
                        FraudScannerInputCard(
                            messageInput = uiState.messageInput,
                            onMessageChange = viewModel::onMessageInputChanged,
                            senderInput = uiState.senderInput,
                            onSenderChange = viewModel::onSenderInputChanged,
                            selectedChannel = uiState.selectedChannel,
                            onChannelSelect = viewModel::onChannelSelected,
                            activeSampleKey = uiState.activeSampleKey,
                            onSelectSample = viewModel::selectSampleScenario,
                            onClearText = viewModel::clearInput,
                            onAnalyzeClick = viewModel::runFullInspection,
                            isScanning = uiState.isScanning,
                            isDark = isDark
                        )
                    }

                    item(key = "mobile_safety_assessment_card") {
                        SafetyAssessmentCard(
                            hasScanned = uiState.hasScannedAtLeastOnce,
                            assessment = uiState.currentAssessment,
                            aiAnalysisState = uiState.aiAnalysisState,
                            activeSpeechLang = uiState.activeSpeechLang,
                            onToggleSpeech = viewModel::toggleSpeechPlayback,
                            copiedFeedback = uiState.copiedFeedback,
                            onShareAdvice = viewModel::triggerCopiedFeedback,
                            isEmergencyExpanded = uiState.isEmergencyExpanded,
                            onToggleEmergencyDrawer = viewModel::toggleEmergencyDrawer,
                            emergencyHighlightTrigger = uiState.emergencyHighlightTrigger,
                            isDark = isDark
                        )
                    }

                    item(key = "mobile_recent_history_card") {
                        RecentScannedStatementsCard(
                            scanHistory = scanHistory,
                            onSelectHistoryItem = viewModel::loadHistoryEntry,
                            onClearHistory = viewModel::clearAllHistory,
                            isDark = isDark
                        )
                    }
                }

                // Footer
                item(key = "footer") {
                    KavachFooter(isDark = isDark)
                }
            }
        }
    }
}

@Composable
private fun KavachFooter(isDark: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 24.dp)
    ) {
        Surface(
            color = if (isDark) Zinc950 else Color.White,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = if (isDark) Zinc800 else Slate200
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.footer_tagline),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Zinc500 else Slate500,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.footer_subtext),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDark) Zinc500 else Slate500,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
