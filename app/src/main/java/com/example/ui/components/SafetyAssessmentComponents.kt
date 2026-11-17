package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.EmergencyStep
import com.example.model.FraudAssessmentResult
import com.example.model.KavachStaticData
import com.example.ui.AiAnalysisState
import com.example.ui.theme.Amber200
import com.example.ui.theme.Amber300
import com.example.ui.theme.Amber400
import com.example.ui.theme.Amber50
import com.example.ui.theme.Amber500
import com.example.ui.theme.Amber600
import com.example.ui.theme.Amber700
import com.example.ui.theme.Amber800
import com.example.ui.theme.Amber900
import com.example.ui.theme.Amber950
import com.example.ui.theme.Emerald200
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald950
import com.example.ui.theme.Orange50
import com.example.ui.theme.PlusJakartaSansFamily
import com.example.ui.theme.Rose100
import com.example.ui.theme.Rose200
import com.example.ui.theme.Rose300
import com.example.ui.theme.Rose400
import com.example.ui.theme.Rose50
import com.example.ui.theme.Rose500
import com.example.ui.theme.Rose600
import com.example.ui.theme.Rose700
import com.example.ui.theme.Rose800
import com.example.ui.theme.Rose900
import com.example.ui.theme.Rose950
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Teal100
import com.example.ui.theme.Teal200
import com.example.ui.theme.Teal300
import com.example.ui.theme.Teal400
import com.example.ui.theme.Teal50
import com.example.ui.theme.Teal600
import com.example.ui.theme.Teal700
import com.example.ui.theme.Teal800
import com.example.ui.theme.Teal900
import com.example.ui.theme.Teal950
import com.example.ui.theme.Zinc100
import com.example.ui.theme.Zinc200
import com.example.ui.theme.Zinc300
import com.example.ui.theme.Zinc400
import com.example.ui.theme.Zinc500
import com.example.ui.theme.Zinc700
import com.example.ui.theme.Zinc800
import com.example.ui.theme.Zinc900
import com.example.ui.theme.Zinc950

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SafetyAssessmentCard(
    hasScanned: Boolean,
    assessment: FraudAssessmentResult?,
    aiAnalysisState: AiAnalysisState,
    activeSpeechLang: String?,
    onToggleSpeech: (String) -> Unit,
    copiedFeedback: Boolean,
    onShareAdvice: () -> Unit,
    isEmergencyExpanded: Boolean,
    onToggleEmergencyDrawer: () -> Unit,
    emergencyHighlightTrigger: Int,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val score = assessment?.riskScore ?: 0
    val level = assessment?.threatLevel ?: stringResource(R.string.ready_to_scan)
    val isHighRisk = hasScanned && (level == "High Risk / Scam" || score >= 70)
    val isSuspicious = hasScanned && !isHighRisk && (level == "Suspicious" || score >= 35)
    val isSafe = hasScanned && !isHighRisk && !isSuspicious

    val headerShieldColor by animateColorAsState(
        targetValue = when {
            isHighRisk -> Rose600
            isSuspicious -> Amber500
            isSafe -> Emerald600
            else -> Teal700
        },
        animationSpec = tween(300),
        label = "headerShieldColor"
    )

    val statusDotColor by animateColorAsState(
        targetValue = when {
            isHighRisk -> Rose600
            isSuspicious -> Amber500
            isSafe -> Emerald500
            else -> Teal600
        },
        animationSpec = tween(300),
        label = "statusDotColor"
    )

    val headingTextColor = when {
        isHighRisk -> if (isDark) Rose400 else Rose700
        isSuspicious -> if (isDark) Amber400 else Amber700
        isSafe -> if (isDark) Emerald400 else Emerald700
        else -> if (isDark) Zinc100 else Slate800
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (!hasScanned) 0f else (score.coerceIn(5, 100) / 100f),
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "riskProgressBar"
    )

    val explanationEn = assessment?.explanation
        ?: "Paste any suspicious SMS, WhatsApp forward, or UPI collect request above and tap \"Check for Scam\" to see a full safety breakdown."
    val explanationHi = assessment?.explanationHi
        ?: "बाईं ओर कोई भी संदिग्ध SMS, WhatsApp संदेश या UPI रिक्वेस्ट पेस्ट करें और पूरी सुरक्षा जानकारी देखने के लिए \"Check for Scam\" पर क्लिक करें।"

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isDark) Zinc900 else Color.White,
        border = BorderStroke(1.dp, if (isDark) Zinc800 else Slate200),
        shadowElevation = 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("safety_assessment_card")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Card Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = when {
                                isHighRisk -> if (isDark) listOf(Rose950.copy(alpha = 0.45f), Zinc900) else listOf(Rose50, Color.White)
                                isSuspicious -> if (isDark) listOf(Amber950.copy(alpha = 0.45f), Zinc900) else listOf(Amber50, Color.White)
                                isSafe -> if (isDark) listOf(Emerald950.copy(alpha = 0.45f), Zinc900) else listOf(Emerald50, Color.White)
                                else -> if (isDark) listOf(Teal950.copy(alpha = 0.45f), Zinc900) else listOf(Teal50, Color.White)
                            }
                        )
                    )
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(headerShieldColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    Text(
                        text = stringResource(R.string.safety_assessment_title),
                        fontFamily = PlusJakartaSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (isDark) Zinc100 else Slate900
                    )
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = when {
                    isHighRisk -> if (isDark) Rose900.copy(alpha = 0.5f) else Rose200
                    isSuspicious -> if (isDark) Amber900.copy(alpha = 0.5f) else Amber200
                    isSafe -> if (isDark) Emerald600.copy(alpha = 0.4f) else Emerald200
                    else -> if (isDark) Teal900.copy(alpha = 0.5f) else Teal200
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 1 & 2. THREAT LEVEL & RISK SCORE BANNER
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.dp,
                        when {
                            isHighRisk -> if (isDark) Rose900.copy(alpha = 0.6f) else Rose200
                            isSuspicious -> if (isDark) Amber900.copy(alpha = 0.6f) else Amber200
                            isSafe -> if (isDark) Emerald600.copy(alpha = 0.4f) else Emerald200
                            else -> if (isDark) Zinc800 else Slate200
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = when {
                                        isHighRisk -> if (isDark) listOf(Rose950.copy(alpha = 0.4f), Zinc900) else listOf(Rose50, Amber50.copy(alpha = 0.35f))
                                        isSuspicious -> if (isDark) listOf(Amber950.copy(alpha = 0.4f), Zinc900) else listOf(Amber50, Color.White)
                                        isSafe -> if (isDark) listOf(Emerald950.copy(alpha = 0.4f), Zinc900) else listOf(Emerald50, Color.White)
                                        else -> if (isDark) listOf(Zinc900, Teal950.copy(alpha = 0.2f)) else listOf(Slate50, Color.White)
                                    }
                                )
                            )
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = stringResource(R.string.threat_level_label).uppercase(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isDark) Zinc400 else Slate500
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(statusDotColor)
                                            .border(2.dp, statusDotColor.copy(alpha = 0.25f), CircleShape)
                                    )
                                    Text(
                                        text = level,
                                        fontFamily = PlusJakartaSansFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        maxLines = 1,
                                        softWrap = false,
                                        color = headingTextColor,
                                        modifier = Modifier.testTag("threat_level_heading")
                                    )
                                }
                            }

                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.risk_score_label).uppercase(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isDark) Zinc400 else Slate500
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text(
                                        text = if (hasScanned) score.toString() else "--",
                                        fontFamily = PlusJakartaSansFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 26.sp,
                                        maxLines = 1,
                                        softWrap = false,
                                        color = if (isDark) Color.White else Slate900,
                                        modifier = Modifier.testTag("risk_score_value")
                                    )
                                    Text(
                                        text = "/ 100",
                                        style = MaterialTheme.typography.labelMedium,
                                        maxLines = 1,
                                        softWrap = false,
                                        color = if (isDark) Zinc400 else Slate400
                                    )
                                }
                            }
                        }

                        // Segmented Risk Progress Bar
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(if (isDark) Zinc800 else Slate200)
                                    .padding(2.dp)
                            ) {
                                // Background 3-Zone Guide
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(50)),
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(0.34f)
                                            .fillMaxHeight()
                                            .background(Emerald500.copy(alpha = 0.28f))
                                    )
                                    Box(
                                        modifier = Modifier
                                            .weight(0.35f)
                                            .fillMaxHeight()
                                            .background(Amber500.copy(alpha = 0.28f))
                                    )
                                    Box(
                                        modifier = Modifier
                                            .weight(0.31f)
                                            .fillMaxHeight()
                                            .background(Rose600.copy(alpha = 0.28f))
                                    )
                                }

                                // Foreground Animated Progress Fill
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(animatedProgress)
                                        .clip(RoundedCornerShape(50))
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = when {
                                                    isHighRisk -> listOf(Amber500, Rose600)
                                                    isSuspicious -> listOf(Emerald500, Amber500)
                                                    isSafe -> listOf(Emerald400, Emerald600)
                                                    else -> listOf(Teal600, Teal600)
                                                }
                                            )
                                        )
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stringResource(R.string.safe_range_label),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isDark) Emerald400 else Emerald700
                                )
                                Text(
                                    text = stringResource(R.string.suspicious_range_label),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isDark) Amber400 else Amber600
                                )
                                Text(
                                    text = stringResource(R.string.high_risk_range_label),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isDark) Rose400 else Rose600
                                )
                            }
                        }
                    }
                }

                // Transparent AI Notice if AI Cloud check encountered an error
                if (aiAnalysisState is AiAnalysisState.Error) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isDark) Amber950.copy(alpha = 0.4f) else Amber50,
                        border = BorderStroke(1.dp, if (isDark) Amber800 else Amber200),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = aiAnalysisState.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDark) Amber200 else Amber900,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }

                // 3. RED FLAGS
                HorizontalDivider(thickness = 1.dp, color = if (isDark) Zinc800 else Slate200)

                val validFlags = assessment?.redFlags ?: emptyList()

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.red_flags_label).uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isDark) Zinc400 else Slate600
                        )

                        val countText = when {
                            !hasScanned -> "None yet"
                            validFlags.isEmpty() -> "None"
                            else -> "${validFlags.size} found"
                        }

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = when {
                                isHighRisk -> if (isDark) Rose950.copy(alpha = 0.55f) else Rose50
                                isSuspicious -> if (isDark) Amber950.copy(alpha = 0.55f) else Amber50
                                isSafe -> if (isDark) Emerald950.copy(alpha = 0.55f) else Emerald50
                                else -> if (isDark) Zinc800 else Slate100
                            },
                            border = BorderStroke(
                                1.dp,
                                when {
                                    isHighRisk -> if (isDark) Rose900.copy(alpha = 0.6f) else Rose200
                                    isSuspicious -> if (isDark) Amber900.copy(alpha = 0.6f) else Amber200
                                    isSafe -> if (isDark) Emerald600.copy(alpha = 0.45f) else Emerald200
                                    else -> if (isDark) Zinc700 else Slate200
                                }
                            )
                        ) {
                            Text(
                                text = countText,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = when {
                                    isHighRisk -> if (isDark) Rose300 else Rose700
                                    isSuspicious -> if (isDark) Amber300 else Amber700
                                    isSafe -> if (isDark) Emerald400 else Emerald700
                                    else -> if (isDark) Zinc300 else Slate600
                                },
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (!hasScanned) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isDark) Zinc950 else Slate50,
                                border = BorderStroke(1.dp, if (isDark) Zinc800 else Slate200)
                            ) {
                                Text(
                                    text = "Waiting for message input...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDark) Zinc400 else Slate500,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                )
                            }
                        } else if (validFlags.isEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isDark) Emerald950.copy(alpha = 0.45f) else Emerald50,
                                border = BorderStroke(
                                    1.dp,
                                    if (isDark) Emerald600.copy(alpha = 0.45f) else Emerald200
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = if (isDark) Emerald400 else Emerald600,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = "No red flags detected",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontSize = 12.sp,
                                        color = if (isDark) Emerald200 else Emerald700
                                    )
                                }
                            }
                        } else {
                            validFlags.forEach { flag ->
                                val pillBg = when {
                                    isSafe -> if (isDark) Emerald950.copy(alpha = 0.45f) else Emerald50
                                    isSuspicious -> if (isDark) Amber950.copy(alpha = 0.45f) else Amber50
                                    else -> if (isDark) Rose950.copy(alpha = 0.45f) else Rose50
                                }
                                val pillBorder = when {
                                    isSafe -> if (isDark) Emerald600.copy(alpha = 0.45f) else Emerald200
                                    isSuspicious -> if (isDark) Amber800 else Amber300
                                    else -> if (isDark) Rose900 else Rose200
                                }
                                val pillText = when {
                                    isSafe -> if (isDark) Emerald200 else Emerald800
                                    isSuspicious -> if (isDark) Amber300 else Amber900
                                    else -> if (isDark) Rose300 else Rose800
                                }
                                val dotCol = when {
                                    isSafe -> Emerald500
                                    isSuspicious -> Amber500
                                    else -> Rose500
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = pillBg,
                                    border = BorderStroke(1.dp, pillBorder)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(dotCol)
                                        )
                                        Text(
                                            text = flag,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontSize = 12.sp,
                                            color = pillText
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. BILINGUAL EXPLANATION (English + Hindi with Voice Read-Out)
                HorizontalDivider(thickness = 1.dp, color = if (isDark) Zinc800 else Slate200)

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.explanation_label).uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isDark) Zinc400 else Slate600
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isDark) Teal950.copy(alpha = 0.6f) else Teal50,
                                border = BorderStroke(
                                    1.dp,
                                    if (isDark) Teal800.copy(alpha = 0.5f) else Teal200.copy(alpha = 0.7f)
                                )
                            ) {
                                Text(
                                    text = stringResource(R.string.bilingual_badge),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDark) Teal300 else Teal700,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Share / Copy Advice Button
                        Surface(
                            onClick = {
                                val summary = buildString {
                                    appendLine("KavachPay Safety Check:")
                                    appendLine("Threat Level: $level")
                                    appendLine("Risk Score: ${if (hasScanned) "$score/100" else "--/100"}")
                                    appendLine("Advice (EN): $explanationEn")
                                    append("सलाह (HI): $explanationHi")
                                }
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                clipboard?.setPrimaryClip(ClipData.newPlainText("KavachPay Safety Check", summary))
                                onShareAdvice()
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Transparent,
                            modifier = Modifier.testTag("copy_advice_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = if (copiedFeedback) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    tint = if (isDark) Teal400 else Teal700,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (copiedFeedback) {
                                        stringResource(R.string.copied_advice)
                                    } else {
                                        stringResource(R.string.share_advice)
                                    },
                                    style = MaterialTheme.typography.labelLarge,
                                    fontSize = 12.sp,
                                    color = if (isDark) Teal400 else Teal700
                                )
                            }
                        }
                    }

                    val accentStripeColor = when {
                        isHighRisk -> Rose600
                        isSuspicious -> Amber500
                        isSafe -> Emerald600
                        else -> Teal600
                    }

                    val cardBg = when {
                        isHighRisk -> if (isDark) Rose950.copy(alpha = 0.18f) else Rose50.copy(alpha = 0.45f)
                        isSuspicious -> if (isDark) Amber950.copy(alpha = 0.18f) else Amber50.copy(alpha = 0.45f)
                        isSafe -> if (isDark) Emerald950.copy(alpha = 0.18f) else Emerald50.copy(alpha = 0.45f)
                        else -> if (isDark) Teal950.copy(alpha = 0.22f) else Teal50.copy(alpha = 0.5f)
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = cardBg,
                        border = BorderStroke(
                            1.dp,
                            when {
                                isHighRisk -> if (isDark) Zinc800 else Rose200.copy(alpha = 0.6f)
                                isSuspicious -> if (isDark) Zinc800 else Amber200.copy(alpha = 0.6f)
                                isSafe -> if (isDark) Zinc800 else Emerald200.copy(alpha = 0.6f)
                                else -> if (isDark) Zinc800 else Teal100
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            // Left 4dp Accent Border
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .background(accentStripeColor)
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // English Explanation Block
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "ENGLISH",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (isDark) Zinc500 else Slate400
                                        )

                                        VoiceListenButton(
                                            isPlaying = activeSpeechLang == "en",
                                            label = if (activeSpeechLang == "en") {
                                                stringResource(R.string.stop_en)
                                            } else {
                                                stringResource(R.string.listen_en)
                                            },
                                            isDark = isDark,
                                            isHindi = false,
                                            onClick = { onToggleSpeech("en") },
                                            modifier = Modifier.testTag("speak_en_button")
                                        )
                                    }

                                    Text(
                                        text = explanationEn,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isDark) Zinc300 else Slate700,
                                        modifier = Modifier.testTag("explanation_en_text")
                                    )
                                }

                                HorizontalDivider(
                                    thickness = 1.dp,
                                    color = if (isDark) Zinc800 else Slate200.copy(alpha = 0.7f)
                                )

                                // Hindi Explanation Block
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = stringResource(R.string.hindi_advice_label),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (isDark) Teal400 else Teal700
                                        )

                                        VoiceListenButton(
                                            isPlaying = activeSpeechLang == "hi",
                                            label = if (activeSpeechLang == "hi") {
                                                stringResource(R.string.stop_hi)
                                            } else {
                                                stringResource(R.string.listen_hi)
                                            },
                                            isDark = isDark,
                                            isHindi = true,
                                            onClick = { onToggleSpeech("hi") },
                                            modifier = Modifier.testTag("speak_hi_button")
                                        )
                                    }

                                    Text(
                                        text = explanationHi,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isDark) Zinc200 else Slate800,
                                        modifier = Modifier.testTag("explanation_hi_text")
                                    )
                                }
                            }
                        }
                    }
                }

                // 5. EMERGENCY GOLDEN-HOUR CHECKLIST (Shown when Suspicious or High Risk)
                AnimatedVisibility(visible = isSuspicious || isHighRisk) {
                    EmergencyGoldenHourChecklist(
                        isExpanded = isEmergencyExpanded,
                        onToggleExpand = onToggleEmergencyDrawer,
                        highlightTrigger = emergencyHighlightTrigger,
                        isDark = isDark
                    )
                }
            }
        }
    }
}

@Composable
private fun VoiceListenButton(
    isPlaying: Boolean,
    label: String,
    isDark: Boolean,
    isHindi: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "voiceWave")
    val bar1Scale by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(350),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar1"
    )
    val bar2Scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(350),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar2"
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isDark) Zinc900 else Color.White.copy(alpha = 0.92f),
        border = BorderStroke(
            1.dp,
            if (isHindi) {
                if (isDark) Teal800.copy(alpha = 0.7f) else Teal200
            } else {
                if (isDark) Zinc700 else Slate200
            }
        ),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            if (isPlaying) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.height(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(11.dp)
                            .scale(scaleX = 1f, scaleY = bar1Scale)
                            .clip(RoundedCornerShape(50))
                            .background(if (isDark) Teal400 else Teal600)
                    )
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(11.dp)
                            .scale(scaleX = 1f, scaleY = bar2Scale)
                            .clip(RoundedCornerShape(50))
                            .background(if (isDark) Teal400 else Teal600)
                    )
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(11.dp)
                            .scale(scaleX = 1f, scaleY = bar1Scale)
                            .clip(RoundedCornerShape(50))
                            .background(if (isDark) Teal400 else Teal600)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = null,
                    tint = if (isDark) Teal400 else Teal600,
                    modifier = Modifier.size(14.dp)
                )
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (isHindi) {
                    if (isDark) Teal300 else Teal800
                } else {
                    if (isDark) Zinc200 else Slate700
                }
            )
        }
    }
}

@Composable
private fun EmergencyGoldenHourChecklist(
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    highlightTrigger: Int,
    isDark: Boolean
) {
    val context = LocalContext.current
    val pulseAnim = remember { Animatable(0f) }

    LaunchedEffect(highlightTrigger) {
        if (highlightTrigger > 0) {
            pulseAnim.snapTo(1f)
            pulseAnim.animateTo(0f, tween(1300))
        }
    }

    val borderWidth = if (pulseAnim.value > 0.1f) 2.dp else 1.dp
    val borderColor = if (pulseAnim.value > 0.1f) {
        Rose600
    } else {
        if (isDark) Amber800.copy(alpha = 0.7f) else Amber300
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("emergency_checklist_section"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HorizontalDivider(thickness = 1.dp, color = if (isDark) Zinc800 else Slate200)

        Surface(
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(borderWidth, borderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = if (isDark) {
                                listOf(
                                    Amber950.copy(alpha = 0.38f),
                                    Zinc900,
                                    Rose950.copy(alpha = 0.25f)
                                )
                            } else {
                                listOf(Amber50, Orange50, Rose50.copy(alpha = 0.5f))
                            }
                        )
                    )
            ) {
                // Collapsible Header Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onToggleExpand)
                        .padding(14.dp)
                        .testTag("emergency_toggle_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Rose600),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = stringResource(R.string.emergency_checklist_badge).uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isDark) Rose400 else Rose700
                            )
                            Text(
                                text = stringResource(R.string.emergency_checklist_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Zinc100 else Slate900
                            )
                        }
                    }

                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = if (isDark) Zinc400 else Slate500
                    )
                }

                // Collapsible Drawer Body
                AnimatedVisibility(visible = isExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = if (isDark) Zinc800 else Amber200.copy(alpha = 0.65f)
                        )

                        // Direct Action Buttons: Call 1930 & cybercrime.gov.in
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:1930"))
                                        context.startActivity(intent)
                                    } catch (_: Exception) {
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Rose600,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 48.dp)
                                    .testTag("call_1930_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.call_1930_btn),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://cybercrime.gov.in"))
                                        context.startActivity(intent)
                                    } catch (_: Exception) {
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, if (isDark) Teal800 else Teal300),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isDark) Zinc900 else Color.White,
                                    contentColor = if (isDark) Teal300 else Teal800
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 48.dp)
                                    .testTag("report_cybercrime_button")
                            ) {
                                Text(
                                    text = stringResource(R.string.report_cybercrime_btn),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }

                        // 4-Step Bilingual Action Checklist
                        KavachStaticData.EMERGENCY_STEPS.forEach { step ->
                            EmergencyChecklistStepItem(step = step, isDark = isDark)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmergencyChecklistStepItem(
    step: EmergencyStep,
    isDark: Boolean
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isDark) Zinc900.copy(alpha = 0.88f) else Color.White.copy(alpha = 0.88f),
        border = BorderStroke(
            1.dp,
            if (isDark) Zinc800 else Amber200.copy(alpha = 0.75f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Rose950 else Rose100),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = step.number.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDark) Rose300 else Rose700
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = step.titleEn,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Zinc100 else Slate800
                )
                Text(
                    text = step.bodyEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDark) Zinc400 else Slate600
                )
                Text(
                    text = step.bodyHi,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = if (isDark) Teal300 else Teal800
                )
            }
        }
    }
}
