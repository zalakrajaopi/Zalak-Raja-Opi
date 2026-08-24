package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.QuitDatabase
import com.example.data.model.CheckInEntry
import com.example.data.model.CravingLog
import com.example.data.model.HabitType
import com.example.data.model.HealthBenefit
import com.example.data.model.JournalEntry
import com.example.data.model.Milestone
import com.example.data.model.Mood
import com.example.data.model.MotivationQuote
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TriggerStat
import com.example.data.model.UserHabit
import com.example.data.model.UserProfileEntity
import com.example.data.repository.QuitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

data class QuitUiState(
    val userProfile: UserProfileEntity? = null,
    val habits: List<UserHabit> = emptyList(),
    val checkIns: List<CheckInEntry> = emptyList(),
    val journalEntries: List<JournalEntry> = emptyList(),
    val cravings: List<CravingLog> = emptyList(),
    val triggers: List<TriggerStat> = emptyList(),
    val savingsGoals: List<SavingsGoalEntity> = emptyList(),
    val dailyQuote: MotivationQuote = MotivationQuote("You don't need to be perfect. You just need to keep moving forward."),
    val milestones: List<Milestone> = emptyList(),
    val healthBenefits: List<HealthBenefit> = emptyList(),
    val streakDays: Int = 0,
    val streakHours: Long = 0,
    val streakMinutes: Long = 0,
    val longestStreakDays: Int = 0,
    val totalMoneySaved: Double = 0.0,
    val totalTimeRecoveredHours: Double = 0.0,
    val hasCheckedInToday: Boolean = false,
    val todayCheckIn: CheckInEntry? = null,
    val isLoading: Boolean = true
)

enum class NavigationTab(val label: String) {
    HOME("Home"),
    PROGRESS("Progress"),
    JOURNAL("Journal"),
    INSIGHTS("Insights"),
    PROFILE("Profile")
}

class QuitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuitRepository

    init {
        val database = QuitDatabase.getInstance(application)
        repository = QuitRepository(database.quitDao())
    }

    private val _currentTab = MutableStateFlow(NavigationTab.HOME)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _showEmergencyCraving = MutableStateFlow(false)
    val showEmergencyCraving: StateFlow<Boolean> = _showEmergencyCraving.asStateFlow()

    private val _selectedRelapseHabit = MutableStateFlow<UserHabit?>(null)
    val selectedRelapseHabit: StateFlow<UserHabit?> = _selectedRelapseHabit.asStateFlow()

    private val _celebratedMilestone = MutableStateFlow<Milestone?>(null)
    val celebratedMilestone: StateFlow<Milestone?> = _celebratedMilestone.asStateFlow()

    val uiState: StateFlow<QuitUiState> = combine(
        repository.userProfile,
        repository.activeHabits,
        repository.checkIns,
        repository.journalEntries,
        repository.cravings,
        repository.triggers,
        repository.savingsGoals
    ) { flows ->
        val profile = flows[0] as? UserProfileEntity
        @Suppress("UNCHECKED_CAST")
        val habits = (flows[1] as? List<UserHabit>) ?: emptyList()
        @Suppress("UNCHECKED_CAST")
        val checkIns = (flows[2] as? List<CheckInEntry>) ?: emptyList()
        @Suppress("UNCHECKED_CAST")
        val journal = (flows[3] as? List<JournalEntry>) ?: emptyList()
        @Suppress("UNCHECKED_CAST")
        val cravings = (flows[4] as? List<CravingLog>) ?: emptyList()
        @Suppress("UNCHECKED_CAST")
        val triggers = (flows[5] as? List<TriggerStat>) ?: emptyList()
        @Suppress("UNCHECKED_CAST")
        val savings = (flows[6] as? List<SavingsGoalEntity>) ?: emptyList()

        val now = System.currentTimeMillis()
        val startDate = profile?.globalStartDate ?: (habits.minOfOrNull { it.startTimestamp } ?: now)
        val elapsedMillis = max(0L, now - startDate)
        val elapsedMinutes = elapsedMillis / (1000 * 60)
        val elapsedHours = elapsedMillis / (1000 * 60 * 60)
        val elapsedDays = (elapsedHours / 24).toInt()

        // Calculate money saved & time saved
        val dailySpend = profile?.dailySpendEstimate ?: (habits.sumOf { it.dailyCost }.takeIf { it > 0 } ?: 15.0)
        val dailyMinutes = profile?.dailyTimeSavedMinutes ?: (habits.sumOf { it.dailyMinutesSpent }.takeIf { it > 0 } ?: 45)

        val totalDaysFraction = elapsedMillis.toDouble() / (1000.0 * 60 * 60 * 24)
        val moneySaved = totalDaysFraction * dailySpend
        val timeSavedHours = totalDaysFraction * (dailyMinutes / 60.0)

        // Best streak calculation
        val maxHabitStreakDays = habits.maxOfOrNull { (it.bestStreakHours / 24).toInt() } ?: 0
        val longestStreak = max(elapsedDays, maxHabitStreakDays)

        // Today check in check
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val todayCheckIn = checkIns.firstOrNull { it.dateString == todayStr }

        val milestones = repository.calculateMilestones(
            streakDays = elapsedDays,
            cravingsOvercomeCount = cravings.count { it.wasOvercome },
            journalCount = journal.size,
            moneySaved = moneySaved
        )

        val healthBenefits = repository.calculateHealthBenefits(elapsedMinutes)

        QuitUiState(
            userProfile = profile,
            habits = habits,
            checkIns = checkIns,
            journalEntries = journal,
            cravings = cravings,
            triggers = triggers,
            savingsGoals = savings,
            dailyQuote = repository.getDailyQuote(),
            milestones = milestones,
            healthBenefits = healthBenefits,
            streakDays = elapsedDays,
            streakHours = elapsedHours,
            streakMinutes = elapsedMinutes,
            longestStreakDays = longestStreak,
            totalMoneySaved = moneySaved,
            totalTimeRecoveredHours = timeSavedHours,
            hasCheckedInToday = todayCheckIn != null,
            todayCheckIn = todayCheckIn,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = QuitUiState(isLoading = true)
    )

    fun selectTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun openEmergencyCraving() {
        _showEmergencyCraving.value = true
    }

    fun closeEmergencyCraving() {
        _showEmergencyCraving.value = false
    }

    fun openRelapseDialog(habit: UserHabit) {
        _selectedRelapseHabit.value = habit
    }

    fun closeRelapseDialog() {
        _selectedRelapseHabit.value = null
    }

    fun showMilestoneCelebration(milestone: Milestone) {
        _celebratedMilestone.value = milestone
    }

    fun dismissMilestoneCelebration() {
        _celebratedMilestone.value = null
    }

    fun completeOnboarding(
        selectedHabits: List<HabitType>,
        reasons: List<String>,
        startDateTimestamp: Long,
        dailySpend: Double,
        dailyMinutes: Int
    ) {
        viewModelScope.launch {
            repository.completeOnboarding(
                selectedHabitTypes = selectedHabits,
                reasons = reasons,
                startDateTimestamp = startDateTimestamp,
                estimatedDailySpend = dailySpend,
                estimatedDailyMinutes = dailyMinutes
            )
        }
    }

    fun submitDailyCheckIn(mood: Mood, note: String) {
        viewModelScope.launch {
            repository.logCheckIn(mood, note)
        }
    }

    fun recordCravingOvercome(
        habitType: HabitType,
        triggerName: String,
        actionTaken: String,
        intensity: Int,
        durationSeconds: Int,
        note: String
    ) {
        viewModelScope.launch {
            repository.logCravingOvercome(
                habitType = habitType,
                triggerName = triggerName,
                actionTaken = actionTaken,
                intensity = intensity,
                durationSeconds = durationSeconds,
                note = note
            )
            _showEmergencyCraving.value = false
        }
    }

    fun addJournalEntry(title: String, content: String, mood: Mood, tags: List<String>) {
        viewModelScope.launch {
            repository.logJournalEntry(title, content, mood, tags)
        }
    }

    fun deleteJournalEntry(id: Long) {
        viewModelScope.launch {
            repository.deleteJournalEntry(id)
        }
    }

    fun recordRelapse(habitId: Long, reason: String) {
        viewModelScope.launch {
            repository.recordRelapse(habitId, reason)
            _selectedRelapseHabit.value = null
        }
    }

    fun addSavingsGoal(title: String, targetAmount: Double, iconEmoji: String) {
        viewModelScope.launch {
            repository.addSavingsGoal(title, targetAmount, iconEmoji)
        }
    }

    fun deleteSavingsGoal(id: Long) {
        viewModelScope.launch {
            repository.deleteSavingsGoal(id)
        }
    }

    fun updateProfile(profile: UserProfileEntity) {
        viewModelScope.launch {
            repository.updateProfile(profile)
        }
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            repository.resetAllProgress()
        }
    }
}
