package com.example.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HabitType
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
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    onFinishOnboarding: (
        habits: List<HabitType>,
        reasons: List<String>,
        startDate: Long,
        dailySpend: Double,
        dailyMinutes: Int
    ) -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) }

    val selectedHabits = remember { mutableStateListOf(HabitType.SMOKING) }
    val selectedReasons = remember {
        mutableStateListOf(
            "Improve my health",
            "Save money",
            "Increase focus",
            "Build self-control"
        )
    }
    var startDateOption by remember { mutableStateOf("Today") }
    var customDaysAgo by remember { mutableIntStateOf(0) }
    var dailySpendEstimate by remember { mutableDoubleStateOf(12.0) }
    var dailyMinutesEstimate by remember { mutableIntStateOf(45) }

    val reasonsOptions = listOf(
        "Improve my health",
        "Save money",
        "Improve relationships",
        "Increase focus",
        "Build self-control",
        "Feel better mentally",
        "Boost daily energy",
        "Other"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Header Top Bar with Step Indicators
            if (currentStep > 1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { if (currentStep > 1) currentStep-- },
                        modifier = Modifier.testTag("onboarding_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextSecondary
                        )
                    }

                    // Progress Dots
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (i in 1..5) {
                            Box(
                                modifier = Modifier
                                    .size(if (i == currentStep) 24.dp else 8.dp, 8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (i == currentStep) TealPrimary
                                        else if (i < currentStep) TealPrimaryLight.copy(alpha = 0.5f)
                                        else SurfaceBorder
                                    )
                            )
                        }
                    }

                    Text(
                        text = "$currentStep of 5",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animated Screen content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> -width } + fadeOut()
                            )
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> width } + fadeOut()
                            )
                        }
                    },
                    label = "OnboardingTransition"
                ) { step ->
                    when (step) {
                        1 -> Screen1Welcome(
                            onGetStarted = { currentStep = 2 }
                        )
                        2 -> Screen2Habits(
                            selectedHabits = selectedHabits,
                            onToggleHabit = { habit ->
                                if (selectedHabits.contains(habit)) {
                                    if (selectedHabits.size > 1) selectedHabits.remove(habit)
                                } else {
                                    selectedHabits.add(habit)
                                }
                            }
                        )
                        3 -> Screen3Reasons(
                            reasonsOptions = reasonsOptions,
                            selectedReasons = selectedReasons,
                            onToggleReason = { reason ->
                                if (selectedReasons.contains(reason)) {
                                    if (selectedReasons.size > 1) selectedReasons.remove(reason)
                                } else {
                                    selectedReasons.add(reason)
                                }
                            }
                        )
                        4 -> Screen4StartDate(
                            startDateOption = startDateOption,
                            onSelectOption = { startDateOption = it },
                            customDaysAgo = customDaysAgo,
                            onCustomDaysChange = { customDaysAgo = it },
                            dailySpend = dailySpendEstimate,
                            onDailySpendChange = { dailySpendEstimate = it }
                        )
                        5 -> Screen5PersonalizedGoal(
                            selectedHabits = selectedHabits,
                            selectedReasons = selectedReasons,
                            startDateOption = startDateOption,
                            customDaysAgo = customDaysAgo,
                            dailySpend = dailySpendEstimate
                        )
                    }
                }
            }

            // Bottom Navigation CTA (For Steps 2 to 5)
            if (currentStep in 2..4) {
                Button(
                    onClick = { currentStep++ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("onboarding_next_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text(
                        text = "Continue",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else if (currentStep == 5) {
                Button(
                    onClick = {
                        val startTime = if (startDateOption == "Today") {
                            System.currentTimeMillis()
                        } else {
                            System.currentTimeMillis() - (customDaysAgo.toLong() * 24 * 60 * 60 * 1000)
                        }
                        onFinishOnboarding(
                            selectedHabits.toList(),
                            selectedReasons.toList(),
                            startTime,
                            dailySpendEstimate,
                            dailyMinutesEstimate
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("start_journey_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text(
                        text = "Start My Journey",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun Screen1Welcome(
    onGetStarted: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Minimalist Q Icon Symbol
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = SurfaceWhite,
            shadowElevation = 8.dp,
            modifier = Modifier.size(110.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(TealPrimary, TealPrimaryLight)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Q",
                    fontSize = 62.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = (-1).sp
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "QUIT",
            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Take back control.",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
            color = TealPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "“Build better habits, one day at a time.”",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onGetStarted,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("get_started_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
        ) {
            Text(
                text = "Get Started",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🔒 100% Private • Stored only on your device",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun Screen2Habits(
    selectedHabits: List<HabitType>,
    onToggleHabit: (HabitType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "What do you want to quit?",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Select all habits you'd like to recover from. You can track them together or individually.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        val habitOptions = listOf(
            Triple(HabitType.SMOKING, "Smoking / Nicotine", "Cigarettes, vaping, pouches"),
            Triple(HabitType.ALCOHOL, "Alcohol", "Beer, wine, liquor, social drinking"),
            Triple(HabitType.PORN_MASTURBATION, "Porn & Masturbation", "Compulsive habits & visual urges"),
            Triple(HabitType.CUSTOM, "Other Unhealthy Habit", "Custom personalized habit")
        )

        habitOptions.forEach { (type, title, subtitle) ->
            val isSelected = selectedHabits.contains(type)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onToggleHabit(type) }
                    .testTag("habit_select_${type.name}"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) TealPrimary.copy(alpha = 0.06f) else SurfaceWhite
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) TealPrimary else SurfaceBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) TealPrimary.copy(alpha = 0.15f) else SurfaceSubtle,
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = type.iconEmoji, fontSize = 26.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) TealPrimary else Color.Transparent)
                            .border(
                                1.5.dp,
                                if (isSelected) TealPrimary else SurfaceBorder,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Screen3Reasons(
    reasonsOptions: List<String>,
    selectedReasons: List<String>,
    onToggleReason: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Why do you want to quit?",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your reasons will serve as your personal anchor whenever you face cravings or difficult moments.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        reasonsOptions.forEach { reason ->
            val isSelected = selectedReasons.contains(reason)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onToggleReason(reason) }
                    .testTag("reason_$reason"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) TealPrimary.copy(alpha = 0.08f) else SurfaceWhite
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) TealPrimary else SurfaceBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = reason,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        ),
                        color = if (isSelected) TealPrimary else TextPrimary
                    )

                    if (isSelected) {
                        Surface(
                            shape = CircleShape,
                            color = TealPrimary,
                            modifier = Modifier.size(22.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Screen4StartDate(
    startDateOption: String,
    onSelectOption: (String) -> Unit,
    customDaysAgo: Int,
    onCustomDaysChange: (Int) -> Unit,
    dailySpend: Double,
    onDailySpendChange: (Double) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "When did you start your quit journey?",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Starting today is a powerful milestone, or you can record if you began earlier.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Option 1: Today (Primary)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .clickable { onSelectOption("Today") }
                .testTag("start_date_today"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (startDateOption == "Today") TealPrimary.copy(alpha = 0.08f) else SurfaceWhite
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = if (startDateOption == "Today") 2.dp else 1.dp,
                color = if (startDateOption == "Today") TealPrimary else SurfaceBorder
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (startDateOption == "Today") TealPrimary else SurfaceSubtle,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "🌱",
                            fontSize = 22.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Today (Right now)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = "Make this the defining first day of recovery",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Option 2: Already Started (Days ago)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .clickable { onSelectOption("Earlier") }
                .testTag("start_date_earlier"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (startDateOption == "Earlier") TealPrimary.copy(alpha = 0.08f) else SurfaceWhite
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = if (startDateOption == "Earlier") 2.dp else 1.dp,
                color = if (startDateOption == "Earlier") TealPrimary else SurfaceBorder
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = SurfaceSubtle,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "⏳", fontSize = 22.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "I started earlier",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "Credit existing progress to your streak",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                if (startDateOption == "Earlier") {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "How many days clean so far?",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1, 3, 7, 14, 30).forEach { days ->
                            val isSelected = customDaysAgo == days
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) TealPrimary else SurfaceSubtle)
                                    .clickable { onCustomDaysChange(days) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$days d",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = if (isSelected) Color.White else TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Daily Spend Estimation Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "How much did you usually spend daily?",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = TextPrimary
                )
                Text(
                    text = "Used to dynamically calculate your money saved.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(5.0, 10.0, 15.0, 25.0).forEach { cost ->
                        val isSelected = dailySpend == cost
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) TealPrimary else SurfaceSubtle)
                                .clickable { onDailySpendChange(cost) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = String.format(Locale.US, "$%.0f", cost),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = if (isSelected) Color.White else TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Screen5PersonalizedGoal(
    selectedHabits: List<HabitType>,
    selectedReasons: List<String>,
    startDateOption: String,
    customDaysAgo: Int,
    dailySpend: Double
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            shape = CircleShape,
            color = SageGreen.copy(alpha = 0.15f),
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "✨", fontSize = 38.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Your journey starts today.",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "You've taken the hardest step by committing to change. Here is your personalized recovery plan:",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Selected Habits Overview Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Habits You Are Conquering",
                    style = MaterialTheme.typography.labelLarge,
                    color = TealPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                selectedHabits.forEach { habit ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = habit.iconEmoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = habit.displayName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(SurfaceBorder))
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Your Core Motivations",
                    style = MaterialTheme.typography.labelLarge,
                    color = TealPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                selectedReasons.take(3).forEach { reason ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "• ", color = SageGreen, fontWeight = FontWeight.Bold)
                        Text(text = reason, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Projected 30-day savings preview
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceSubtle,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Estimated 30-day savings",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = String.format(Locale.US, "$%.0f saved", dailySpend * 30),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = SageGreen
                    )
                }
                Text(text = "🎯", fontSize = 26.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
