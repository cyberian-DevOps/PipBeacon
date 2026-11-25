package com.pipbeacon.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pipbeacon.app.data.lessons.AcademyTrack
import com.pipbeacon.app.data.lessons.LessonItem
import com.pipbeacon.app.ui.components.AcademyHeroGraphic
import com.pipbeacon.app.ui.components.ShowcaseBackground
import com.pipbeacon.app.ui.components.ShowcaseCandlestickStrip
import com.pipbeacon.app.ui.components.ShowcaseGoldButton
import com.pipbeacon.app.ui.theme.CyanAccent
import com.pipbeacon.app.ui.theme.GoldAccent
import com.pipbeacon.app.ui.theme.GoldAccentBright
import com.pipbeacon.app.ui.theme.IndicatorBuy
import com.pipbeacon.app.ui.theme.IndicatorNeutralBackground
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.NavyCard
import com.pipbeacon.app.ui.theme.NavyCardElevated
import com.pipbeacon.app.ui.theme.NavySurface
import com.pipbeacon.app.ui.theme.NavySurfaceVariant
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary
import com.pipbeacon.app.ui.theme.TextSecondary
import com.pipbeacon.app.viewmodel.AcademyUiState

@Composable
fun ForexAcademyScreen(
    uiState: AcademyUiState,
    onSelectLevel: (String) -> Unit,
    onToggleCompletion: (String) -> Unit,
    onSwitchToCryptoAcademy: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    AcademyTrackScreen(
        track = AcademyTrack.FOREX,
        lessons = uiState.forexLessons,
        completedCount = uiState.forexCompletedCount,
        totalCount = uiState.forexTotalCount,
        selectedLevel = uiState.selectedLevel,
        onSelectLevel = onSelectLevel,
        onToggleCompletion = onToggleCompletion,
        onSwitchTrack = onSwitchToCryptoAcademy,
        switchTrackLabel = "Crypto Academy",
        onBack = onBack,
        modifier = modifier.testTag("forex_academy_screen")
    )
}

@Composable
fun CryptoAcademyScreen(
    uiState: AcademyUiState,
    onSelectLevel: (String) -> Unit,
    onToggleCompletion: (String) -> Unit,
    onSwitchToForexAcademy: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    AcademyTrackScreen(
        track = AcademyTrack.CRYPTO,
        lessons = uiState.cryptoLessons,
        completedCount = uiState.cryptoCompletedCount,
        totalCount = uiState.cryptoTotalCount,
        selectedLevel = uiState.selectedLevel,
        onSelectLevel = onSelectLevel,
        onToggleCompletion = onToggleCompletion,
        onSwitchTrack = onSwitchToForexAcademy,
        switchTrackLabel = "Forex Academy",
        onBack = onBack,
        modifier = modifier.testTag("crypto_academy_screen")
    )
}

@Composable
fun AcademyTrackScreen(
    track: AcademyTrack,
    lessons: List<LessonItem>,
    completedCount: Int,
    totalCount: Int,
    selectedLevel: String,
    onSelectLevel: (String) -> Unit,
    onToggleCompletion: (String) -> Unit,
    onSwitchTrack: () -> Unit,
    switchTrackLabel: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLessonIndex by remember { mutableStateOf<Int?>(null) }
    var searchVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val isForex = track == AcademyTrack.FOREX
    val accent = if (isForex) CyanAccent else GoldAccentBright
    val progressPercent = if (totalCount > 0) ((completedCount.toFloat() / totalCount) * 100).toInt() else 0

    val filteredLessons = remember(lessons, searchQuery) {
        if (searchQuery.isBlank()) lessons
        else lessons.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                it.summary.contains(searchQuery, ignoreCase = true)
        }
    }

    ShowcaseBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Showcase Top Header (Back, Centered Title, Search & Track Switch)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("academy_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Text(
                    text = track.title,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { searchVisible = !searchVisible },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = if (searchVisible) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Search lessons",
                            tint = if (searchVisible) GoldAccentBright else TextPrimary
                        )
                    }
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(onClick = onSwitchTrack)
                            .testTag("academy_switch_track_button"),
                        color = NavyCard,
                        border = BorderStroke(1.dp, NavyBorder),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = if (isForex) "Crypto" else "Forex",
                            color = accent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(visible = searchVisible) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search ${track.title}...", color = TextMuted, fontSize = 13.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accent,
                        unfocusedBorderColor = NavyBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = NavyCard,
                        unfocusedContainerColor = NavyCard
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Showcase Hero Illustration Banner Card
            Surface(
                color = NavyCard,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, NavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(118.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    ) {
                        AcademyHeroGraphic(
                            isForex = isForex,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Subtle currency/crypto emblem label overlay
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 18.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isForex) {
                                Text(
                                    text = "€",
                                    color = CyanAccent,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.width(34.dp))
                                Text(
                                    text = "$",
                                    color = Color(0xFF060D1A),
                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    Text(
                        text = if (isForex) {
                            "Learn the basics of the Forex market, currency pairs and trading strategies."
                        } else {
                            "Explore the world of cryptocurrency trading, order books and risk hygiene."
                        },
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Level Filter Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val levels = listOf(
                    "ALL" to "All Levels",
                    "BEGINNER" to "Beginner",
                    "INTERMEDIATE" to "Intermediate",
                    "ADVANCED" to "Advanced",
                    "PRO" to "Pro"
                )
                levels.forEach { (code, label) ->
                    val isSelected = selectedLevel == code
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) GoldAccent else NavyCard)
                            .border(
                                1.dp,
                                if (isSelected) GoldAccent else NavyBorder,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { onSelectLevel(code) }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color(0xFF060D1A) else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Showcase Lesson List with Play Icon & Chevron
            if (filteredLessons.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No lessons match the selected filter.",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(filteredLessons, key = { _, item -> item.id }) { index, lesson ->
                        ShowcaseAcademyLessonRow(
                            lesson = lesson,
                            accentColor = accent,
                            onOpenLesson = { selectedLessonIndex = index },
                            onToggleCompletion = { onToggleCompletion(lesson.id) }
                        )
                    }
                }
            }

            // Bottom Showcase Progress Footer ("0% Complete")
            Surface(
                color = NavyCard,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, NavyBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 4.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$progressPercent% Complete",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$completedCount / $totalCount Modules • Offline",
                            color = accent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(7.dp))
                    LinearProgressIndicator(
                        progress = { progressPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = accent,
                        trackColor = NavySurfaceVariant
                    )
                }
            }
        }
    }

    selectedLessonIndex?.let { idx ->
        val lesson = filteredLessons.getOrNull(idx) ?: return@let
        val latestLesson = lessons.firstOrNull { it.id == lesson.id } ?: lesson
        ShowcaseLessonDetailsDialog(
            lesson = latestLesson,
            accentColor = accent,
            hasNextLesson = idx < filteredLessons.lastIndex,
            onDismiss = { selectedLessonIndex = null },
            onToggleCompletion = { onToggleCompletion(latestLesson.id) },
            onNextLesson = {
                if (!latestLesson.isCompleted) {
                    onToggleCompletion(latestLesson.id)
                }
                if (idx < filteredLessons.lastIndex) {
                    selectedLessonIndex = idx + 1
                } else {
                    selectedLessonIndex = null
                }
            }
        )
    }
}

@Composable
private fun ShowcaseAcademyLessonRow(
    lesson: LessonItem,
    accentColor: Color,
    onOpenLesson: () -> Unit,
    onToggleCompletion: () -> Unit
) {
    Surface(
        color = NavyCard,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, NavyBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onOpenLesson)
            .testTag("lesson_card_${lesson.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular Play Icon Badge matching Showcase
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.18f))
                    .border(1.dp, accentColor.copy(alpha = 0.55f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (lesson.isCompleted) Icons.Default.Check else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = if (lesson.isCompleted) IndicatorBuy else accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = lesson.title,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (lesson.isPremium) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(IndicatorNeutralBackground)
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "PRO",
                                color = GoldAccentBright,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = lesson.level,
                        color = accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${lesson.durationMinutes} min",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clickable(onClick = onToggleCompletion),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(if (lesson.isCompleted) IndicatorBuy else Color.Transparent)
                        .border(1.5.dp, if (lesson.isCompleted) IndicatorBuy else TextMuted, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (lesson.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color(0xFF060D1A),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Open lesson",
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Showcase Lesson Details Dialog: Candlestick header banner, summary, "Key Points:" checkmarks, Gold "Next Lesson" pill button
 */
@Composable
fun ShowcaseLessonDetailsDialog(
    lesson: LessonItem,
    accentColor: Color,
    hasNextLesson: Boolean = false,
    onDismiss: () -> Unit,
    onToggleCompletion: () -> Unit,
    onNextLesson: () -> Unit = onToggleCompletion
) {
    val keyPoints = remember(lesson.id) {
        extractKeyPoints(lesson)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, NavyBorder, RoundedCornerShape(20.dp)),
            color = NavySurface
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Text(
                        text = "Lesson Details",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Showcase Candlestick Header Graphic
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(115.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(NavyCardElevated)
                        .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
                        .padding(10.dp)
                ) {
                    ShowcaseCandlestickStrip(
                        isBullishBias = true,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GoldAccent.copy(alpha = 0.2f))
                            .border(1.dp, GoldAccentBright.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "${lesson.track.title} • ${lesson.level}",
                            color = GoldAccentBright,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = lesson.title,
                    color = TextPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = lesson.summary,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Key Points with emerald checkmarks (matching Showcase Lesson Details)
                Text(
                    text = "Key Points:",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                keyPoints.forEach { point ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(IndicatorBuy),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF060D1A),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = point,
                            color = TextPrimary,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(NavyCard)
                        .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = lesson.fullContent,
                        color = TextSecondary,
                        fontSize = 12.5.sp,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = NavyCardElevated,
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, if (lesson.isCompleted) IndicatorBuy else NavyBorder),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .clickable(onClick = onToggleCompletion)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (lesson.isCompleted) "✓ Completed" else "Mark Done",
                                color = if (lesson.isCompleted) IndicatorBuy else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    ShowcaseGoldButton(
                        text = if (hasNextLesson) "Next Lesson" else "Finish Module",
                        onClick = onNextLesson,
                        height = 48.dp,
                        modifier = Modifier.weight(1.2f)
                    )
                }
            }
        }
    }
}

private fun extractKeyPoints(lesson: LessonItem): List<String> {
    val bullets = lesson.fullContent
        .lines()
        .map { it.trim() }
        .filter { it.startsWith("•") || it.startsWith("-") }
        .map {
            it.removePrefix("•")
                .removePrefix("-")
                .replace("**", "")
                .trim()
        }
        .filter { it.isNotBlank() }
        .take(3)
    return if (bullets.isNotEmpty()) bullets else listOf(
        "Core market mechanics",
        "Risk & liquidity awareness",
        "Practical chart study"
    )
}
