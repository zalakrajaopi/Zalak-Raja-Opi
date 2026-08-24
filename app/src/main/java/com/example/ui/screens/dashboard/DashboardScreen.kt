package com.example.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Mood
import com.example.data.model.UserHabit
import com.example.ui.components.IndividualHabitCard
import com.example.ui.components.MainProgressCircularCard
import com.example.ui.components.MoodSelectorRow
import com.example.ui.theme.AmberWarning
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
import java.util.Calendar

@Composable
fun DashboardScreen(
    state: QuitUiState,
    onEmergencyCravingClick: () -> Unit,
    onHabitRelapseClick: (UserHabit) -> Unit,
    onCheckInSubmit: (Mood, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var checkInMood by remember { mutableStateOf(Mood.GOOD) }
    var checkInNote by remember { mutableStateOf("") }
    var showNoteField by remember { mutableStateOf(false) }

    // Dynamic greeting based on time of day
    val hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (hourOfDay) {
        in 5..11 -> "Good morning 👋"
        in 12..16 -> "Good afternoon 👋"
        else -> "Good evening 👋"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Dashboard Header
            Column {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Keep going. You're building a stronger version of yourself.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        // Emergency Craving Action Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onEmergencyCravingClick() }
                    .shadow(4.dp, shape = RoundedCornerShape(20.dp), spotColor = TealPrimary.copy(alpha = 0.2f))
                    .testTag("craving_button"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, TealPrimaryLight.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(TealPrimary.copy(alpha = 0.05f), MintAccent.copy(alpha = 0.12f))
                            )
                        )
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = TealPrimary,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "🧘", fontSize = 22.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "I'm having a craving",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TealPrimary
                            )
                            Text(
                                text = "Tap for instant calm, breathing & support",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = TealPrimary
                    ) {
                        Text(
                            text = "Pause",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Main Progress Card
        item {
            MainProgressCircularCard(
                streakDays = state.streakDays,
                streakHours = state.streakHours,
                longestStreakDays = state.longestStreakDays,
                moneySaved = state.totalMoneySaved,
                timeSavedHours = state.totalTimeRecoveredHours
            )
        }

        // Daily Motivation Quote Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "“",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = SageGreen,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = state.dailyQuote.quote,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Daily Reflection • Focus & Growth",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        // Daily Check-In Card
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "How are you feeling today?",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = "Track your daily emotional resilience",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }

                        if (state.hasCheckedInToday) {
                            Surface(
                                shape = CircleShape,
                                color = SageGreen.copy(alpha = 0.12f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = SageGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Checked In",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = SageGreen
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    MoodSelectorRow(
                        selectedMood = checkInMood,
                        onMoodSelected = { checkInMood = it }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!state.hasCheckedInToday) {
                        AnimatedVisibility(visible = showNoteField) {
                            Column {
                                OutlinedTextField(
                                    value = checkInNote,
                                    onValueChange = { checkInNote = it },
                                    placeholder = { Text("Add an optional quick thought or wins...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = SurfaceSubtle,
                                        unfocusedContainerColor = SurfaceSubtle,
                                        focusedBorderColor = TealPrimary,
                                        unfocusedBorderColor = Color.Transparent
                                    ),
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (showNoteField) "Hide note" else "+ Add note",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = TealPrimary,
                                modifier = Modifier
                                    .clickable { showNoteField = !showNoteField }
                                    .padding(4.dp)
                            )

                            Button(
                                onClick = { onCheckInSubmit(checkInMood, checkInNote) },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                modifier = Modifier.testTag("complete_checkin_button")
                            ) {
                                Text(
                                    text = "Complete Check-In",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White
                                )
                            }
                        }
                    } else {
                        state.todayCheckIn?.let { today ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SurfaceSubtle,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = today.mood.emoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Logged as ${today.mood.displayName}${if (today.note.isNotBlank()) ": \"${today.note}\"" else ""}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Habit Cards Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tracked Habits",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = "${state.habits.size} Active",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted
                )
            }
        }

        // Individual Habit Cards
        items(state.habits) { habit ->
            IndividualHabitCard(
                habit = habit,
                onRelapseClick = { onHabitRelapseClick(habit) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
