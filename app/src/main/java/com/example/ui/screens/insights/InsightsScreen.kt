package com.example.ui.screens.insights

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HealthBenefit
import com.example.data.model.SavingsGoalEntity
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.MintAccent
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

@Composable
fun InsightsScreen(
    state: QuitUiState,
    onAddSavingsGoal: (title: String, amount: Double, icon: String) -> Unit,
    onDeleteSavingsGoal: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var isAddingGoal by remember { mutableStateOf(false) }
    var goalTitle by remember { mutableStateOf("") }
    var goalAmountStr by remember { mutableStateOf("") }
    var goalEmoji by remember { mutableStateOf("🎁") }

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
                text = "Recovery Insights",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Text(
                text = "Understand your triggers, savings rewards, and physical recovery timeline.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }

        // Section 1: Know Your Triggers
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Know Your Triggers",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = "High-risk situations and patterns",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                        Text(text = "🛡️", fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val triggersList = if (state.triggers.isNotEmpty()) state.triggers else listOf(
                        com.example.data.model.TriggerStat("Work & Mental Stress", 4),
                        com.example.data.model.TriggerStat("Late Night Fatigue", 3),
                        com.example.data.model.TriggerStat("Boredom / Free Time", 2),
                        com.example.data.model.TriggerStat("Social Outings", 1)
                    )
                    val maxTriggerCount = max(1, triggersList.maxOfOrNull { it.count } ?: 1)

                    triggersList.forEach { trigger ->
                        val fraction = (trigger.count.toFloat() / maxTriggerCount.toFloat()).coerceIn(0.1f, 1f)
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = trigger.triggerName,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${trigger.count} times recorded",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TealPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { fraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(CircleShape),
                                color = TealPrimaryLight,
                                trackColor = SurfaceSubtle
                            )
                        }
                    }
                }
            }
        }

        // Section 2: Money Saved & Wishlist Goals
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Money Saved Calculator",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = String.format(Locale.US, "$%.2f accumulated so far", state.totalMoneySaved),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = SageGreen
                            )
                        }

                        IconButton(
                            onClick = { isAddingGoal = !isAddingGoal },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(SurfaceSubtle)
                                .testTag("add_savings_goal_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Goal",
                                tint = TealPrimary
                            )
                        }
                    }

                    // Add Custom Goal Inline Form
                    AnimatedVisibility(visible = isAddingGoal) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceSubtle)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Create a Personal Reward Goal",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = goalTitle,
                                onValueChange = { goalTitle = it },
                                placeholder = { Text("Goal Title (e.g. Vacation Trip, New Watch)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = SurfaceWhite,
                                    unfocusedContainerColor = SurfaceWhite,
                                    focusedBorderColor = TealPrimary,
                                    unfocusedBorderColor = Color.Transparent
                                ),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = goalAmountStr,
                                    onValueChange = { goalAmountStr = it },
                                    placeholder = { Text("Target $") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = SurfaceWhite,
                                        unfocusedContainerColor = SurfaceWhite,
                                        focusedBorderColor = TealPrimary,
                                        unfocusedBorderColor = Color.Transparent
                                    ),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = goalEmoji,
                                    onValueChange = { goalEmoji = it },
                                    placeholder = { Text("Icon") },
                                    modifier = Modifier.width(70.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = SurfaceWhite,
                                        unfocusedContainerColor = SurfaceWhite,
                                        focusedBorderColor = TealPrimary,
                                        unfocusedBorderColor = Color.Transparent
                                    ),
                                    singleLine = true
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    val amount = goalAmountStr.toDoubleOrNull() ?: 100.0
                                    if (goalTitle.isNotBlank()) {
                                        onAddSavingsGoal(goalTitle, amount, goalEmoji.ifBlank { "🎁" })
                                        goalTitle = ""
                                        goalAmountStr = ""
                                        isAddingGoal = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                            ) {
                                Text("Add Savings Goal", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "That's enough for...",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val goals = state.savingsGoals
                    goals.forEach { goal ->
                        val progressFraction = (state.totalMoneySaved / goal.targetAmount).toFloat().coerceIn(0f, 1f)
                        val isAchieved = state.totalMoneySaved >= goal.targetAmount

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isAchieved) SageGreen.copy(alpha = 0.08f) else SurfaceSubtle,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = goal.iconEmoji, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = goal.title,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = String.format(Locale.US, "Goal: $%.0f", goal.targetAmount),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextMuted
                                            )
                                        }
                                    }

                                    if (isAchieved) {
                                        Surface(
                                            shape = CircleShape,
                                            color = SageGreen,
                                            modifier = Modifier.size(22.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Achieved",
                                                tint = Color.White,
                                                modifier = Modifier.padding(3.dp)
                                            )
                                        }
                                    } else if (goal.isCustom) {
                                        IconButton(onClick = { onDeleteSavingsGoal(goal.id) }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = TextMuted,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { progressFraction },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(CircleShape),
                                    color = if (isAchieved) SageGreen else TealPrimaryLight,
                                    trackColor = Color(0xFFE2E8F0)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Health & Benefits Timeline
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Health & Recovery Timeline",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = "General Benefits",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted
                )
            }
        }

        items(state.healthBenefits) { benefit ->
            HealthBenefitTimelineCard(benefit = benefit)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HealthBenefitTimelineCard(benefit: HealthBenefit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (benefit.isAchieved) SurfaceWhite else SurfaceSubtle
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (benefit.isAchieved) SageGreen.copy(alpha = 0.3f) else SurfaceBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = CircleShape,
                color = if (benefit.isAchieved) SageGreen.copy(alpha = 0.15f) else Color(0xFFE2E8F0),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = benefit.categoryEmoji, fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = benefit.timeframeTitle,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (benefit.isAchieved) SageGreen else TealPrimary
                    )
                    if (benefit.isAchieved) {
                        Surface(
                            shape = CircleShape,
                            color = SageGreen,
                            modifier = Modifier.size(18.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Achieved",
                                tint = Color.White,
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Upcoming",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = benefit.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = benefit.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                if (!benefit.isAchieved && benefit.progressPercent > 0.05f) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { benefit.progressPercent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(CircleShape),
                        color = TealPrimaryLight,
                        trackColor = Color(0xFFE2E8F0)
                    )
                }
            }
        }
    }
}
