package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_habits")
data class UserHabit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitType: HabitType,
    val customName: String = "",
    val startTimestamp: Long = System.currentTimeMillis(),
    val bestStreakHours: Long = 0,
    val dailyCost: Double = 10.0,
    val dailyMinutesSpent: Int = 30,
    val isEnabled: Boolean = true,
    val lastRelapseTimestamp: Long = 0L,
    val totalRelapses: Int = 0
)

@Entity(tableName = "check_ins")
data class CheckInEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val dateString: String, // YYYY-MM-DD
    val mood: Mood,
    val note: String = "",
    val habitId: Long = 0
)

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val dateString: String,
    val title: String = "",
    val content: String,
    val mood: Mood = Mood.GOOD,
    val tagsCsv: String = "" // comma separated JournalTag names
) {
    fun getTagsList(): List<String> {
        if (tagsCsv.isBlank()) return emptyList()
        return tagsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }
}

@Entity(tableName = "craving_logs")
data class CravingLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val habitType: HabitType,
    val triggerName: String = "",
    val intensity: Int = 3, // 1 to 5
    val actionTaken: String = "",
    val wasOvercome: Boolean = true,
    val durationSeconds: Int = 60,
    val reflectionNote: String = ""
)

@Entity(tableName = "trigger_stats")
data class TriggerStat(
    @PrimaryKey val triggerName: String,
    val count: Int = 1,
    val lastOccurred: Long = System.currentTimeMillis()
)

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val targetAmount: Double,
    val iconEmoji: String = "🎁",
    val isCustom: Boolean = false,
    val isAchieved: Boolean = false
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val userName: String = "Friend",
    val hasCompletedOnboarding: Boolean = false,
    val globalStartDate: Long = System.currentTimeMillis(),
    val selectedReasonsCsv: String = "",
    val notifyDailyCheckIn: Boolean = true,
    val notifyMorningMotivation: Boolean = true,
    val notifyEveningReflection: Boolean = true,
    val notifyCravingSupport: Boolean = true,
    val notifyMilestones: Boolean = true,
    val dailySpendEstimate: Double = 15.0,
    val dailyTimeSavedMinutes: Int = 45
) {
    fun getReasonsList(): List<String> {
        if (selectedReasonsCsv.isBlank()) return emptyList()
        return selectedReasonsCsv.split("|").filter { it.isNotEmpty() }
    }
}
