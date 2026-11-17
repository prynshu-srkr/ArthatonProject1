package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ScanHistoryEntity
import com.example.model.KavachStaticData
import com.example.model.SampleScenario
import com.example.ui.theme.Amber200
import com.example.ui.theme.Amber300
import com.example.ui.theme.Amber50
import com.example.ui.theme.Amber700
import com.example.ui.theme.Amber900
import com.example.ui.theme.Amber950
import com.example.ui.theme.Emerald200
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald950
import com.example.ui.theme.Rose200
import com.example.ui.theme.Rose300
import com.example.ui.theme.Rose400
import com.example.ui.theme.Rose50
import com.example.ui.theme.Rose600
import com.example.ui.theme.Rose700
import com.example.ui.theme.Rose900
import com.example.ui.theme.Rose950
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Teal200
import com.example.ui.theme.Teal300
import com.example.ui.theme.Teal400
import com.example.ui.theme.Teal50
import com.example.ui.theme.Teal600
import com.example.ui.theme.Teal700
import com.example.ui.theme.Teal800
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FraudScannerInputCard(
    messageInput: String,
    onMessageChange: (String) -> Unit,
    senderInput: String,
    onSenderChange: (String) -> Unit,
    selectedChannel: String,
    onChannelSelect: (String) -> Unit,
    activeSampleKey: String?,
    onSelectSample: (SampleScenario) -> Unit,
    onClearText: () -> Unit,
    onAnalyzeClick: () -> Unit,
    isScanning: Boolean,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    var channelDropdownExpanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isDark) Zinc900 else Color.White,
        border = BorderStroke(1.dp, if (isDark) Zinc800 else Slate200),
        shadowElevation = 1.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Row: Label + Clear Text
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.input_label),
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isDark) Zinc100 else Slate800,
                        modifier = Modifier.weight(1f)
                    )

                    TextButton(
                        onClick = onClearText,
                        modifier = Modifier.testTag("clear_input_button")
                    ) {
                        Text(
                            text = stringResource(R.string.clear_text),
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isDark) Teal400 else Teal700
                        )
                    }
                }

                // Message Input Textarea
                OutlinedTextField(
                    value = messageInput,
                    onValueChange = onMessageChange,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.input_placeholder),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDark) Zinc500 else Slate400
                        )
                    },
                    minLines = 4,
                    maxLines = 7,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = if (isDark) Zinc950 else Color.White,
                        unfocusedContainerColor = if (isDark) Zinc950 else Slate50.copy(alpha = 0.6f),
                        focusedBorderColor = if (isDark) Teal400 else Teal600,
                        unfocusedBorderColor = if (isDark) Zinc700 else Slate300,
                        focusedTextColor = if (isDark) Zinc100 else Slate900,
                        unfocusedTextColor = if (isDark) Zinc100 else Slate900
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("message_input")
                )

                // Try Sample Messages Section
                HorizontalDivider(
                    thickness = 1.dp,
                    color = if (isDark) Zinc800 else Slate200.copy(alpha = 0.7f)
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = if (isDark) Emerald400 else Emerald600,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(R.string.try_sample_messages).uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isDark) Zinc400 else Slate500
                        )
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        KavachStaticData.SAMPLE_SCENARIOS.forEach { sample ->
                            val isSelected = activeSampleKey == sample.key
                            SamplePillButton(
                                sample = sample,
                                isSelected = isSelected,
                                isDark = isDark,
                                onClick = { onSelectSample(sample) }
                            )
                        }
                    }
                }

                // Sender Name / UPI ID + Channel Row
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = stringResource(R.string.sender_label),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Zinc400 else Slate600
                        )
                        OutlinedTextField(
                            value = senderInput,
                            onValueChange = onSenderChange,
                            singleLine = true,
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.sender_placeholder),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDark) Zinc500 else Slate400
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = if (isDark) Zinc950 else Color.White,
                                unfocusedContainerColor = if (isDark) Zinc950 else Slate50.copy(alpha = 0.7f),
                                focusedBorderColor = if (isDark) Teal400 else Teal600,
                                unfocusedBorderColor = if (isDark) Zinc800 else Slate200,
                                focusedTextColor = if (isDark) Zinc200 else Slate800,
                                unfocusedTextColor = if (isDark) Zinc200 else Slate800
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("sender_input")
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = stringResource(R.string.channel_label),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Zinc400 else Slate600
                        )

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                onClick = { channelDropdownExpanded = true },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isDark) Zinc950 else Slate50.copy(alpha = 0.7f),
                                border = BorderStroke(1.dp, if (isDark) Zinc800 else Slate200),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 48.dp)
                                    .testTag("channel_dropdown")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = selectedChannel,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isDark) Zinc200 else Slate800
                                    )
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = if (isDark) Zinc400 else Slate500
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = channelDropdownExpanded,
                                onDismissRequest = { channelDropdownExpanded = false },
                                modifier = Modifier.background(if (isDark) Zinc900 else Color.White)
                            ) {
                                KavachStaticData.CHANNELS.forEach { channel ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = channel,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = if (isDark) Zinc100 else Slate800
                                            )
                                        },
                                        onClick = {
                                            onChannelSelect(channel)
                                            channelDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Action Bar
            HorizontalDivider(
                thickness = 1.dp,
                color = if (isDark) Zinc800 else Slate200.copy(alpha = 0.9f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDark) Zinc950 else Slate50.copy(alpha = 0.9f))
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Emerald500)
                    )
                    Text(
                        text = stringResource(R.string.instant_protection_badge),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDark) Zinc400 else Slate500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = onAnalyzeClick,
                    enabled = !isScanning,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Teal600 else Teal700,
                        contentColor = Color.White,
                        disabledContainerColor = if (isDark) Teal800 else Teal600.copy(alpha = 0.6f),
                        disabledContentColor = Color.White
                    ),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("analyze_button")
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.checking_scam),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.check_for_scam),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SamplePillButton(
    sample: SampleScenario,
    isSelected: Boolean,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = when {
            isSelected && isDark -> Emerald950.copy(alpha = 0.75f)
            isSelected && !isDark -> Emerald50
            isDark -> Zinc950
            else -> Slate50
        },
        animationSpec = tween(200),
        label = "pillBg"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isSelected && isDark -> Emerald400
            isSelected && !isDark -> Emerald500
            isDark -> Zinc800
            else -> Slate200
        },
        animationSpec = tween(200),
        label = "pillBorder"
    )

    val textColor = when {
        isSelected && isDark -> Emerald200
        isSelected && !isDark -> Emerald700
        isDark -> Zinc300
        else -> Slate700
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor),
        modifier = Modifier
            .then(
                if (isSelected) {
                    Modifier.shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(12.dp),
                        ambientColor = Emerald500,
                        spotColor = Emerald500
                    )
                } else {
                    Modifier
                }
            )
            .testTag("sample_pill_${sample.key}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(Color(sample.dotColorHex))
            )
            Text(
                text = sample.label,
                style = MaterialTheme.typography.labelLarge,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
        }
    }
}

@Composable
fun RecentScannedStatementsCard(
    scanHistory: List<ScanHistoryEntity>,
    onSelectHistoryItem: (ScanHistoryEntity) -> Unit,
    onClearHistory: () -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isDark) Zinc900 else Color.White,
        border = BorderStroke(1.dp, if (isDark) Zinc800 else Slate200),
        shadowElevation = 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("recent_scanned_card")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Card Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDark) Zinc950.copy(alpha = 0.5f) else Slate50.copy(alpha = 0.6f))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Teal600)
                    )
                    Text(
                        text = stringResource(R.string.recent_scanned_title),
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if (isDark) Zinc200 else Slate800,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (isDark) Teal950.copy(alpha = 0.6f) else Teal50,
                        border = BorderStroke(
                            1.dp,
                            if (isDark) Teal800.copy(alpha = 0.5f) else Teal200.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("history_count_badge")
                    ) {
                        Text(
                            text = "${scanHistory.size} saved",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            softWrap = false,
                            color = if (isDark) Teal300 else Teal700,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    onClick = onClearHistory,
                    enabled = scanHistory.isNotEmpty(),
                    shape = RoundedCornerShape(10.dp),
                    color = if (isDark) Rose950.copy(alpha = 0.35f) else Rose50.copy(alpha = 0.7f),
                    border = BorderStroke(
                        1.dp,
                        if (isDark) Rose900.copy(alpha = 0.5f) else Rose200.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.testTag("clear_history_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = null,
                            tint = if (scanHistory.isEmpty()) {
                                if (isDark) Zinc500 else Slate400
                            } else {
                                if (isDark) Rose400 else Rose600
                            },
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = stringResource(R.string.clear_history),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            softWrap = false,
                            color = if (scanHistory.isEmpty()) {
                                if (isDark) Zinc500 else Slate400
                            } else {
                                if (isDark) Rose400 else Rose600
                            }
                        )
                    }
                }
            }

            HorizontalDivider(thickness = 1.dp, color = if (isDark) Zinc800 else Slate200)

            // Table Column Headers (Matched widths with HistoryStatementRow)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDark) Zinc950.copy(alpha = 0.4f) else Slate50.copy(alpha = 0.5f))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Saved Statement",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    color = if (isDark) Zinc400 else Slate500,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Risk Score",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    softWrap = false,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = if (isDark) Zinc400 else Slate500,
                    modifier = Modifier.width(68.dp)
                )
                Text(
                    text = "Threat Level",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    softWrap = false,
                    textAlign = androidx.compose.ui.text.style.TextAlign.End,
                    color = if (isDark) Zinc400 else Slate500,
                    modifier = Modifier.width(114.dp)
                )
            }

            HorizontalDivider(thickness = 1.dp, color = if (isDark) Zinc800 else Slate200)

            if (scanHistory.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.empty_history_message),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDark) Zinc500 else Slate400
                    )
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    scanHistory.forEachIndexed { idx, item ->
                        HistoryStatementRow(
                            item = item,
                            isDark = isDark,
                            onClick = { onSelectHistoryItem(item) }
                        )
                        if (idx < scanHistory.lastIndex) {
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = if (isDark) Zinc800 else Slate200.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryStatementRow(
    item: ScanHistoryEntity,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val snippet = remember(item.text) {
        if (item.text.length > 80) item.text.take(80) + "..." else item.text
    }
    val formattedTime = remember(item.timestamp) {
        try {
            val date = Date(item.timestamp)
            val timeFmt = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)
            timeFmt.format(date)
        } catch (e: Exception) {
            ""
        }
    }

    val isHighRisk = item.threatLevel == "High Risk / Scam" || item.riskScore >= 70
    val isSuspicious = !isHighRisk && (item.threatLevel == "Suspicious" || item.riskScore >= 35)

    val badgeBg = when {
        isHighRisk -> if (isDark) Rose950.copy(alpha = 0.55f) else Rose50
        isSuspicious -> if (isDark) Amber950.copy(alpha = 0.55f) else Amber50
        else -> if (isDark) Emerald950.copy(alpha = 0.55f) else Emerald50
    }
    val badgeBorder = when {
        isHighRisk -> if (isDark) Rose900.copy(alpha = 0.65f) else Rose200
        isSuspicious -> if (isDark) Amber900.copy(alpha = 0.65f) else Amber200
        else -> if (isDark) Emerald600.copy(alpha = 0.45f) else Emerald200
    }
    val badgeTextCol = when {
        isHighRisk -> if (isDark) Rose300 else Rose700
        isSuspicious -> if (isDark) Amber300 else Amber700
        else -> if (isDark) Emerald400 else Emerald700
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Top Row: 3 Aligned Columns (Saved Statement | Risk Score | Threat Level)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = snippet,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = if (isDark) Zinc200 else Slate800,
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier.width(68.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${item.riskScore} / 100",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = if (isDark) Zinc100 else Slate900
                )
            }

            Box(
                modifier = Modifier.width(114.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = badgeBg,
                    border = BorderStroke(1.dp, badgeBorder)
                ) {
                    Text(
                        text = item.threatLevel,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        softWrap = false,
                        color = badgeTextCol,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Bottom Row: Full-width Sender Badge + Normal Horizontal Date & Time
        if (item.sender.isNotBlank() || formattedTime.isNotBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (item.sender.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isDark) Zinc800 else Slate100,
                        border = BorderStroke(1.dp, if (isDark) Zinc700 else Slate200)
                    ) {
                        Text(
                            text = item.sender,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            softWrap = false,
                            color = if (isDark) Zinc300 else Slate600,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }

                if (formattedTime.isNotBlank()) {
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        softWrap = false,
                        color = if (isDark) Zinc400 else Slate400
                    )
                }
            }
        }
    }
}
