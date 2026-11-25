package com.pipbeacon.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.pipbeacon.app.data.lessons.LessonRepository
import com.pipbeacon.app.ui.components.ShowcaseBackground
import com.pipbeacon.app.ui.theme.CyanAccent
import com.pipbeacon.app.ui.theme.GoldAccent
import com.pipbeacon.app.ui.theme.GoldAccentBright
import com.pipbeacon.app.ui.theme.IndicatorBuy
import com.pipbeacon.app.ui.theme.IndicatorNeutralBackground
import com.pipbeacon.app.ui.theme.NavyBorder
import com.pipbeacon.app.ui.theme.NavyCard
import com.pipbeacon.app.ui.theme.NavySurfaceVariant
import com.pipbeacon.app.ui.theme.TextMuted
import com.pipbeacon.app.ui.theme.TextPrimary
import com.pipbeacon.app.ui.theme.TextSecondary

@Composable
fun TradingLessonsScreen(
    onBack: (() -> Unit)? = null,
    lessonRepository: LessonRepository = remember { LessonRepository() }
) {
    var selectedLevel by remember { mutableStateOf("ALL") }
    var selectedLessonIndex by remember { mutableStateOf<Int?>(null) }
    var refreshTrigger by remember { mutableStateOf(0) }

    val lessons = remember(selectedLevel, refreshTrigger) {
        lessonRepository.getLessons(selectedLevel)
    }
    val progress = remember(refreshTrigger) {
        lessonRepository.getProgress()
    }
    val progressPercent = if (progress.second > 0) (progress.first.toFloat() / progress.second * 100).toInt() else 0

    ShowcaseBackground(
        modifier = Modifier.testTag("trading_lessons_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBack != null) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = "Trading Academy",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Card
            Surface(
                color = NavyCard,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, NavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Academy Mastery",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${progress.first}/${progress.second} Modules",
                            color = GoldAccentBright,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$progressPercent% Complete • Available Offline",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { progressPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = GoldAccentBright,
                        trackColor = NavySurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "ALL" to "All Levels",
                    "BEGINNER" to "Beginner",
                    "INTERMEDIATE" to "Intermediate",
                    "ADVANCED" to "Advanced",
                    "PRO" to "Pro"
                ).forEach { (code, label) ->
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
                            .clickable { selectedLevel = code }
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

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(lessons) { idx, lesson ->
                    val accent = if (lesson.level == "Beginner") CyanAccent else GoldAccentBright
                    Surface(
                        color = NavyCard,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, NavyBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedLessonIndex = idx }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(accent.copy(alpha = 0.16f))
                                    .border(1.dp, accent.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (lesson.isCompleted) Icons.Default.Check else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = if (lesson.isCompleted) IndicatorBuy else accent,
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
                                            Text("PRO", color = GoldAccentBright, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(lesson.level, color = accent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.Schedule, contentDescription = null, tint = TextMuted, modifier = Modifier.size(11.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("${lesson.durationMinutes} min", color = TextMuted, fontSize = 11.sp)
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .clickable {
                                        lessonRepository.toggleLessonCompletion(lesson.id)
                                        refreshTrigger++
                                    },
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
                                        Icon(Icons.Default.Check, contentDescription = "Completed", tint = Color(0xFF060D1A), modifier = Modifier.size(13.dp))
                                    }
                                }
                            }
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }

    selectedLessonIndex?.let { idx ->
        val lesson = lessons.getOrNull(idx) ?: return@let
        ShowcaseLessonDetailsDialog(
            lesson = lesson,
            accentColor = GoldAccentBright,
            hasNextLesson = idx < lessons.lastIndex,
            onDismiss = { selectedLessonIndex = null },
            onToggleCompletion = {
                lessonRepository.toggleLessonCompletion(lesson.id)
                refreshTrigger++
            },
            onNextLesson = {
                if (!lesson.isCompleted) {
                    lessonRepository.toggleLessonCompletion(lesson.id)
                    refreshTrigger++
                }
                if (idx < lessons.lastIndex) selectedLessonIndex = idx + 1 else selectedLessonIndex = null
            }
        )
    }
}
