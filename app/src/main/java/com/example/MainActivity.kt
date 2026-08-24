package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.MilestoneCelebrationDialog
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.emergency.EmergencyCravingScreen
import com.example.ui.screens.insights.InsightsScreen
import com.example.ui.screens.journal.JournalScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.progress.ProgressScreen
import com.example.ui.screens.relapse.RelapseRecoveryDialog
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.NavigationTab
import com.example.ui.viewmodel.QuitViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: QuitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
                val showEmergencyCraving by viewModel.showEmergencyCraving.collectAsStateWithLifecycle()
                val selectedRelapseHabit by viewModel.selectedRelapseHabit.collectAsStateWithLifecycle()
                val celebratedMilestone by viewModel.celebratedMilestone.collectAsStateWithLifecycle()

                val hasCompletedOnboarding = uiState.userProfile?.hasCompletedOnboarding == true

                if (!hasCompletedOnboarding) {
                    OnboardingScreen(
                        onFinishOnboarding = { habits, reasons, startDate, dailySpend, dailyMinutes ->
                            viewModel.completeOnboarding(
                                selectedHabits = habits,
                                reasons = reasons,
                                startDateTimestamp = startDate,
                                dailySpend = dailySpend,
                                dailyMinutes = dailyMinutes
                            )
                        }
                    )
                } else if (showEmergencyCraving) {
                    EmergencyCravingScreen(
                        habits = uiState.habits.map { it.habitType },
                        onClose = { viewModel.closeEmergencyCraving() },
                        onOvercomeSuccess = { habit, trigger, action, intensity, duration, note ->
                            viewModel.recordCravingOvercome(
                                habitType = habit,
                                triggerName = trigger,
                                actionTaken = action,
                                intensity = intensity,
                                durationSeconds = duration,
                                note = note
                            )
                        }
                    )
                } else {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(BackgroundLight)
                            .statusBarsPadding(),
                        bottomBar = {
                            QuitBottomNavigationBar(
                                currentTab = currentTab,
                                onTabSelected = { viewModel.selectTab(it) }
                            )
                        },
                        containerColor = BackgroundLight
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            Crossfade(targetState = currentTab, label = "TabTransition") { tab ->
                                when (tab) {
                                    NavigationTab.HOME -> DashboardScreen(
                                        state = uiState,
                                        onEmergencyCravingClick = { viewModel.openEmergencyCraving() },
                                        onHabitRelapseClick = { viewModel.openRelapseDialog(it) },
                                        onCheckInSubmit = { mood, note -> viewModel.submitDailyCheckIn(mood, note) }
                                    )
                                    NavigationTab.PROGRESS -> ProgressScreen(
                                        state = uiState,
                                        onMilestoneClick = { viewModel.showMilestoneCelebration(it) }
                                    )
                                    NavigationTab.JOURNAL -> JournalScreen(
                                        state = uiState,
                                        onAddEntry = { title, content, mood, tags ->
                                            viewModel.addJournalEntry(title, content, mood, tags)
                                        },
                                        onDeleteEntry = { viewModel.deleteJournalEntry(it) }
                                    )
                                    NavigationTab.INSIGHTS -> InsightsScreen(
                                        state = uiState,
                                        onAddSavingsGoal = { title, amount, icon ->
                                            viewModel.addSavingsGoal(title, amount, icon)
                                        },
                                        onDeleteSavingsGoal = { viewModel.deleteSavingsGoal(it) }
                                    )
                                    NavigationTab.PROFILE -> ProfileScreen(
                                        state = uiState,
                                        onUpdateProfile = { viewModel.updateProfile(it) },
                                        onResetProgress = { viewModel.resetAllProgress() }
                                    )
                                }
                            }
                        }
                    }

                    // Relapse recovery modal if triggered
                    selectedRelapseHabit?.let { habit ->
                        RelapseRecoveryDialog(
                            habit = habit,
                            onDismiss = { viewModel.closeRelapseDialog() },
                            onConfirmReset = { reason -> viewModel.recordRelapse(habit.id, reason) }
                        )
                    }

                    // Milestone celebration dialog
                    celebratedMilestone?.let { milestone ->
                        MilestoneCelebrationDialog(
                            milestone = milestone,
                            onDismiss = { viewModel.dismissMilestoneCelebration() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuitBottomNavigationBar(
    currentTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars),
        color = SurfaceWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
    ) {
        NavigationBar(
            containerColor = SurfaceWhite,
            tonalElevation = 0.dp
        ) {
            val items = listOf(
                Triple(NavigationTab.HOME, Icons.Filled.Home, Icons.Outlined.Home),
                Triple(NavigationTab.PROGRESS, Icons.Filled.BarChart, Icons.Outlined.BarChart),
                Triple(NavigationTab.JOURNAL, Icons.Filled.Book, Icons.Outlined.Book),
                Triple(NavigationTab.INSIGHTS, Icons.Filled.Lightbulb, Icons.Outlined.Lightbulb),
                Triple(NavigationTab.PROFILE, Icons.Filled.Person, Icons.Outlined.Person)
            )

            items.forEach { (tab, filledIcon, outlinedIcon) ->
                val isSelected = currentTab == tab
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onTabSelected(tab) },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) filledIcon else outlinedIcon,
                            contentDescription = tab.label,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = tab.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = TealPrimary,
                        selectedTextColor = TealPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = TealPrimary.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                )
            }
        }
    }
}
