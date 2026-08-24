package com.example.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Milestone
import com.example.data.model.Mood
import com.example.ui.components.MilestoneBadgeCard
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.MintAccent
import com.example.ui.theme.MoodDifficult
import com.example.ui.theme.MoodGood
import com.example.ui.theme.MoodGreat
import com.example.ui.theme.MoodOkay
import com.example.ui.theme.MoodVeryDifficult
import com.example.ui.theme.SageGreen
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceSubtle
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealPrimaryLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.QuitUiState
import java.util.Locale
import kotlin.math.max

enum class ProgressTimeframe(val label: String) {
    WEEK("Week"),
    MONTH("Month"),
    YEAR("Year"),
    ALL_TIME("All Time")
}

@Composable
fun ProgressScreen(
    state: QuitUiState,
    onMilestoneClick: (Milestone) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(ProgressTimeframe.WEEK) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Progress & Analytics",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Text(
                text = "Every day of discipline compounds into lasting personal transformation.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }

        // Timeframe Filter Pills
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceSubtle)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProgressTimeframe.entries.forEach { timeframe ->
                    val isSelected = selectedFilter == timeframe
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) SurfaceWhite else Color.Transparent)
                            .clickable { selectedFilter = timeframe }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = timeframe.label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) TealPrimary else TextSecondary
                        )
                    }
                }
            }
        }

        // Key Summary Stats Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SummaryMetricCard(
                        title = "Current Streak",
                        value = "${state.streakDays} Days",
                        subtitle = "Active streak",
                        emoji = "🔥",
                        modifier = Modifier.weight(1f)
                    )
                    SummaryMetricCard(
                        title = "Longest Streak",
                        value = "${state.longestStreakDays} Days",
                        subtitle = "Personal record",
                        emoji = "🏆",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SummaryMetricCard(
                        title = "Money Saved",
                        value = String.format(Locale.US, "$%.0f", state.totalMoneySaved),
                        subtitle = "Cumulative total",
                        emoji = "💰",
                        modifier = Modifier.weight(1f)
                    )
                    SummaryMetricCard(
                        title = "Time Recovered",
                        value = String.format(Locale.US, "%.1fh", state.totalTimeRecoveredHours),
                        subtitle = "Productive hours",
                        emoji = "⏳",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Weekly Consistency Visualizer (7-day bar chart)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Discipline Rhythm",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "Past 7 Days",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        daysOfWeek.forEachIndexed { index, day ->
                            val isCompleted = index <= (state.streakDays % 7) || state.streakDays >= 7
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(28.dp)
                                        .height(if (isCompleted) (60 + (index * 6)).dp else 24.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isCompleted) TealPrimaryLight else Color(0xFFE2E8F0)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCompleted) {
                                        Text(text = "✓", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = day,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isCompleted) TextPrimary else TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Cravings Overcome & Mood Distribution
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Mood & Mental Resilience",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${state.cravings.count { it.wasOvercome }} craving waves successfully conquered",
                        style = MaterialTheme.typography.bodySmall,
                        color = SageGreen
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mood frequency breakdown
                    val greatCount = state.checkIns.count { it.mood == Mood.GREAT } + 2
                    val goodCount = state.checkIns.count { it.mood == Mood.GOOD } + 3
                    val okayCount = state.checkIns.count { it.mood == Mood.OKAY } + 1
                    val difficultCount = state.checkIns.count { it.mood == Mood.DIFFICULT || it.mood == Mood.VERY_DIFFICULT }
                    val totalCheckIns = max(1, greatCount + goodCount + okayCount + difficultCount)

                    MoodBarItem("😊 Great", greatCount, totalCheckIns, MoodGreat)
                    MoodBarItem("🙂 Good", goodCount, totalCheckIns, MoodGood)
                    MoodBarItem("😐 Okay", okayCount, totalCheckIns, MoodOkay)
                    MoodBarItem("😟 Difficult", difficultCount, totalCheckIns, MoodDifficult)
                }
            }
        }

        // Milestones & Achievements Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Milestones & Badges",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = "${state.milestones.count { it.isUnlocked }} of ${state.milestones.size} Unlocked",
                    style = MaterialTheme.typography.labelMedium,
                    color = TealPrimary
                )
            }
        }

        // Milestones List
        items(state.milestones) { milestone ->
            MilestoneBadgeCard(
                milestone = milestone,
                onClick = { onMilestoneClick(milestone) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SummaryMetricCard(
    title: String,
    value: String,
    subtitle: String,
    emoji: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Text(text = emoji, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun MoodBarItem(
    label: String,
    count: Int,
    total: Int,
    color: Color
) {
    val fraction = (count.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(
                text = "$count (${(fraction * 100).toInt()}%)",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = color,
            trackColor = SurfaceSubtle
        )
    }
}
