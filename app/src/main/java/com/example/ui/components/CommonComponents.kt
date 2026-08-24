package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HabitType
import com.example.data.model.Milestone
import com.example.data.model.Mood
import com.example.data.model.UserHabit
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
import java.util.Locale
import kotlin.math.max

@Composable
fun MainProgressCircularCard(
    streakDays: Int,
    streakHours: Long,
    longestStreakDays: Int,
    moneySaved: Double,
    timeSavedHours: Double,
    primaryHabitName: String = "All Habits",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp), spotColor = TealPrimary.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Large circular progress ring
            val progressGoalDays = when {
                streakDays < 3 -> 3
                streakDays < 7 -> 7
                streakDays < 14 -> 14
                streakDays < 30 -> 30
                streakDays < 60 -> 60
                streakDays < 90 -> 90
                else -> 365
            }
            val progressFraction = (streakDays.toFloat() / progressGoalDays.toFloat()).coerceIn(0.05f, 1f)
            val animatedProgress by animateFloatAsState(
                targetValue = progressFraction,
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                label = "ProgressRing"
            )

            Box(
                modifier = Modifier.size(190.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Track
                Canvas(modifier = Modifier.size(180.dp)) {
                    drawCircle(
                        color = Color(0xFFF1F5F9),
                        radius = size.minDimension / 2 - 12.dp.toPx(),
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Gradient Progress Arc
                Canvas(modifier = Modifier.size(180.dp)) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(TealPrimaryLight, MintAccent, TealPrimary)
                        ),
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (streakDays > 0) "$streakDays ${if (streakDays == 1) "Day" else "Days"}" else "${streakHours}h",
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Clean & Free",
                        style = MaterialTheme.typography.labelLarge,
                        color = TealPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceSubtle
                    ) {
                        Text(
                            text = "Target: $progressGoalDays days",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Subtitle Streak pill
            Surface(
                shape = CircleShape,
                color = TealPrimary.copy(alpha = 0.08f),
                border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(TealPrimary, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$streakDays day active streak",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = TealPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bottom 4-stat grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceSubtle)
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "Best Streak", value = "$longestStreakDays d")
                Box(modifier = Modifier.width(1.dp).height(32.dp).background(SurfaceBorder))
                StatItem(label = "Saved", value = String.format(Locale.US, "$%.0f", moneySaved))
                Box(modifier = Modifier.width(1.dp).height(32.dp).background(SurfaceBorder))
                StatItem(label = "Time Gained", value = String.format(Locale.US, "%.1fh", timeSavedHours))
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
    }
}

@Composable
fun IndividualHabitCard(
    habit: UserHabit,
    onRelapseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val now = System.currentTimeMillis()
    val elapsedMillis = max(0L, now - habit.startTimestamp)
    val elapsedDays = (elapsedMillis / (1000 * 60 * 60 * 24)).toInt()
    val elapsedHours = (elapsedMillis / (1000 * 60 * 60))

    val targetGoal = when {
        elapsedDays < 3 -> 3
        elapsedDays < 7 -> 7
        elapsedDays < 14 -> 14
        elapsedDays < 30 -> 30
        else -> 90
    }
    val progress = (elapsedDays.toFloat() / targetGoal.toFloat()).coerceIn(0.05f, 1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = SurfaceSubtle,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = habit.habitType.iconEmoji, fontSize = 20.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = habit.habitType.displayName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = TextPrimary
                        )
                        Text(
                            text = if (elapsedDays > 0) "$elapsedDays days clean" else "${elapsedHours}h clean",
                            style = MaterialTheme.typography.bodySmall,
                            color = TealPrimary
                        )
                    }
                }

                IconButton(
                    onClick = onRelapseClick,
                    modifier = Modifier.testTag("reset_habit_${habit.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset habit progress",
                        tint = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Current: $elapsedDays d",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "Next milestone: $targetGoal d",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = TealPrimaryLight,
                    trackColor = SurfaceSubtle
                )
            }
        }
    }
}

@Composable
fun MoodSelectorRow(
    selectedMood: Mood,
    onMoodSelected: (Mood) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Mood.entries.forEach { mood ->
            val isSelected = mood == selectedMood
            val moodColor = when (mood) {
                Mood.GREAT -> MoodGreat
                Mood.GOOD -> MoodGood
                Mood.OKAY -> MoodOkay
                Mood.DIFFICULT -> MoodDifficult
                Mood.VERY_DIFFICULT -> MoodVeryDifficult
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) moodColor.copy(alpha = 0.12f) else Color.Transparent)
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) moodColor else SurfaceBorder,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { onMoodSelected(mood) }
                    .padding(horizontal = 8.dp, vertical = 10.dp)
            ) {
                Text(text = mood.emoji, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mood.displayName,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    ),
                    color = if (isSelected) moodColor else TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun MilestoneBadgeCard(
    milestone: Milestone,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (milestone.isUnlocked) SurfaceWhite else SurfaceSubtle
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (milestone.isUnlocked) TealPrimaryLight.copy(alpha = 0.3f) else SurfaceBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (milestone.isUnlocked) TealPrimary.copy(alpha = 0.1f) else Color(0xFFE2E8F0),
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = milestone.iconEmoji, fontSize = 24.sp)
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = milestone.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (milestone.isUnlocked) TextPrimary else TextSecondary
                    )
                    if (milestone.isUnlocked) {
                        Surface(
                            shape = CircleShape,
                            color = TealPrimary,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Unlocked",
                                tint = Color.White,
                                modifier = Modifier.padding(3.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = milestone.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { milestone.progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(CircleShape),
                    color = if (milestone.isUnlocked) SageGreen else TealPrimaryLight,
                    trackColor = Color(0xFFE2E8F0)
                )
            }
        }
    }
}
