package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.KavachStaticData
import com.example.model.SlideThemeType
import com.example.ui.theme.Amber100
import com.example.ui.theme.Amber200
import com.example.ui.theme.Amber300
import com.example.ui.theme.Amber400
import com.example.ui.theme.Amber50
import com.example.ui.theme.Amber500
import com.example.ui.theme.Amber800
import com.example.ui.theme.Amber900
import com.example.ui.theme.Amber950
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Orange50
import com.example.ui.theme.PlusJakartaSansFamily
import com.example.ui.theme.Rose300
import com.example.ui.theme.Rose500
import com.example.ui.theme.Rose600
import com.example.ui.theme.Rose800
import com.example.ui.theme.Rose900
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.Teal200
import com.example.ui.theme.Teal300
import com.example.ui.theme.Teal400
import com.example.ui.theme.Teal500
import com.example.ui.theme.Teal600
import com.example.ui.theme.Teal700
import com.example.ui.theme.Teal800
import com.example.ui.theme.Teal900
import com.example.ui.theme.Teal950
import com.example.ui.theme.Zinc100
import com.example.ui.theme.Zinc200
import com.example.ui.theme.Zinc300
import com.example.ui.theme.Zinc700
import com.example.ui.theme.Zinc800
import com.example.ui.theme.Zinc900
import com.example.ui.theme.Zinc950

@Composable
fun KavachTopBar(
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Surface(
            color = if (isDark) Zinc950.copy(alpha = 0.95f) else Color.White.copy(alpha = 0.95f),
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                // Top Brand Accent Line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Teal700, Emerald600, Amber500)
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = if (isDark) {
                                            listOf(Teal500, Teal700)
                                        } else {
                                            listOf(Teal600, Teal800)
                                        }
                                    )
                                )
                                .border(
                                    width = 1.dp,
                                    color = Teal600.copy(alpha = 0.35f),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = stringResource(R.string.app_name),
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Text(
                                text = stringResource(R.string.app_name),
                                fontFamily = PlusJakartaSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = if (isDark) Zinc100 else Slate900
                            )
                            Text(
                                text = stringResource(R.string.app_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = if (isDark) Teal400 else Teal700
                            )
                        }
                    }

                    Surface(
                        onClick = onToggleTheme,
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDark) Zinc900 else Slate50,
                        border = BorderStroke(1.dp, if (isDark) Zinc800 else Slate200),
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("theme_toggle_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = stringResource(R.string.toggle_theme_desc),
                                tint = if (isDark) Amber400 else Slate600,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = if (isDark) Zinc800 else Slate200.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
fun GoldenUpiRuleCarousel(
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val slides = KavachStaticData.GOLDEN_RULE_SLIDES
    var currentSlide by remember { mutableIntStateOf(0) }
    val progress = remember { Animatable(0f) }
    val context = LocalContext.current

    LaunchedEffect(currentSlide) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 4200, easing = LinearEasing)
        )
        currentSlide = (currentSlide + 1) % slides.size
    }

    val activeSlide = slides[currentSlide]

    val iconBgColor by animateColorAsState(
        targetValue = when (activeSlide.themeType) {
            SlideThemeType.AMBER -> Amber500
            SlideThemeType.TEAL -> Teal700
            SlideThemeType.ROSE -> Rose600
        },
        animationSpec = tween(300),
        label = "slideIconColor"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "helplinePulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isDark) Amber800.copy(alpha = 0.5f) else Amber200.copy(alpha = 0.9f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("golden_rule_carousel")
            .pointerInput(Unit) {
                var dragDistance = 0f
                detectHorizontalDragGestures(
                    onDragStart = { dragDistance = 0f },
                    onHorizontalDrag = { _, dragAmount -> dragDistance += dragAmount },
                    onDragEnd = {
                        if (dragDistance < -40f) {
                            currentSlide = (currentSlide + 1) % slides.size
                        } else if (dragDistance > 40f) {
                            currentSlide = (currentSlide - 1 + slides.size) % slides.size
                        }
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = if (isDark) {
                            listOf(
                                Amber950.copy(alpha = 0.45f),
                                Amber900.copy(alpha = 0.2f),
                                Zinc900
                            )
                        } else {
                            listOf(Amber50, Orange50, Amber50)
                        }
                    )
                )
        ) {
            // Top Animated Sweep Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(if (isDark) Amber950.copy(alpha = 0.6f) else Amber200.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress.value)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Amber500, Teal600, Emerald500)
                            )
                        )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(iconBgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    AnimatedContent(
                        targetState = activeSlide,
                        transitionSpec = {
                            fadeIn(tween(220)) togetherWith fadeOut(tween(180))
                        },
                        modifier = Modifier.weight(1f),
                        label = "cyberSlideContent"
                    ) { slide ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            val badgeBg = when (slide.themeType) {
                                SlideThemeType.AMBER -> Amber500.copy(alpha = 0.15f)
                                SlideThemeType.TEAL -> Teal600.copy(alpha = 0.15f)
                                SlideThemeType.ROSE -> Rose500.copy(alpha = 0.15f)
                            }
                            val badgeBorder = when (slide.themeType) {
                                SlideThemeType.AMBER -> Amber400.copy(alpha = 0.35f)
                                SlideThemeType.TEAL -> Teal500.copy(alpha = 0.35f)
                                SlideThemeType.ROSE -> Rose500.copy(alpha = 0.35f)
                            }
                            val badgeTextCol = when (slide.themeType) {
                                SlideThemeType.AMBER -> if (isDark) Amber300 else Amber900
                                SlideThemeType.TEAL -> if (isDark) Teal300 else Teal900
                                SlideThemeType.ROSE -> if (isDark) Rose300 else Rose900
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(badgeBg)
                                    .border(1.dp, badgeBorder, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = slide.badgeText.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeTextCol
                                )
                            }

                            val highlightCol = when (slide.themeType) {
                                SlideThemeType.AMBER -> if (isDark) Amber200 else Amber950
                                SlideThemeType.TEAL -> if (isDark) Teal200 else Teal950
                                SlideThemeType.ROSE -> if (isDark) Rose300 else Rose800
                            }

                            Text(
                                text = buildAnnotatedString {
                                    append(slide.bodyPrefix)
                                    withStyle(
                                        SpanStyle(
                                            fontWeight = FontWeight.Bold,
                                            color = highlightCol
                                        )
                                    ) {
                                        append(slide.boldHighlight)
                                    }
                                    append(slide.bodySuffix)
                                    if (slide.secondaryHighlight.isNotEmpty()) {
                                        withStyle(
                                            SpanStyle(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isDark) Teal300 else Teal800
                                            )
                                        ) {
                                            append(slide.secondaryHighlight)
                                        }
                                        append(slide.tailText)
                                    }
                                },
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = if (isDark) Zinc200 else Slate700
                            )
                        }
                    }
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = if (isDark) Amber800.copy(alpha = 0.35f) else Amber200.copy(alpha = 0.65f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Slide Controls Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDark) Zinc900.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.88f),
                        border = BorderStroke(
                            1.dp,
                            if (isDark) Amber800.copy(alpha = 0.5f) else Amber200.copy(alpha = 0.8f)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    currentSlide = (currentSlide - 1 + slides.size) % slides.size
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("prev_slide_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = stringResource(R.string.prev_slide_desc),
                                    tint = if (isDark) Zinc300 else Slate600,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                slides.forEachIndexed { idx, _ ->
                                    val dotWidth by animateDpAsState(
                                        targetValue = if (idx == currentSlide) 20.dp else 8.dp,
                                        animationSpec = tween(250),
                                        label = "dotWidth"
                                    )
                                    val dotColor = if (idx == currentSlide) {
                                        if (isDark) Teal400 else Teal700
                                    } else {
                                        if (isDark) Zinc700 else Amber300
                                    }
                                    Box(
                                        modifier = Modifier
                                            .height(8.dp)
                                            .width(dotWidth)
                                            .clip(CircleShape)
                                            .background(dotColor)
                                            .clickable { currentSlide = idx }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "${currentSlide + 1}/${slides.size}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Zinc300 else Slate600
                            )

                            IconButton(
                                onClick = {
                                    currentSlide = (currentSlide + 1) % slides.size
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("next_slide_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = stringResource(R.string.next_slide_desc),
                                    tint = if (isDark) Zinc300 else Slate600,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Helpline 1930 Badge (Tap to Dial 1930)
                    Surface(
                        onClick = {
                            try {
                                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:1930"))
                                context.startActivity(dialIntent)
                            } catch (_: Exception) {
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDark) Zinc900.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.92f),
                        border = BorderStroke(
                            1.dp,
                            if (isDark) Amber800.copy(alpha = 0.5f) else Amber200.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier.testTag("helpline_1930_badge")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .alpha(pulseAlpha)
                                    .clip(CircleShape)
                                    .background(Emerald500)
                            )
                            Text(
                                text = buildAnnotatedString {
                                    append("Helpline: ")
                                    withStyle(
                                        SpanStyle(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Teal400 else Teal700
                                        )
                                    ) {
                                        append("1930")
                                    }
                                },
                                style = MaterialTheme.typography.labelLarge,
                                fontSize = 12.sp,
                                color = if (isDark) Zinc200 else Slate700
                            )
                        }
                    }
                }
            }
        }
    }
}
