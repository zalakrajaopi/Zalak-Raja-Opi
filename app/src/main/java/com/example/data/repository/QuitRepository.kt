package com.example.data.repository

import com.example.data.local.QuitDao
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
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

class QuitRepository(private val quitDao: QuitDao) {

    val userProfile: Flow<UserProfileEntity?> = quitDao.getUserProfileFlow()
    val activeHabits: Flow<List<UserHabit>> = quitDao.getActiveHabitsFlow()
    val allHabits: Flow<List<UserHabit>> = quitDao.getAllHabitsFlow()
    val checkIns: Flow<List<CheckInEntry>> = quitDao.getAllCheckInsFlow()
    val journalEntries: Flow<List<JournalEntry>> = quitDao.getAllJournalEntriesFlow()
    val cravings: Flow<List<CravingLog>> = quitDao.getAllCravingsFlow()
    val triggers: Flow<List<TriggerStat>> = quitDao.getAllTriggersFlow()
    val savingsGoals: Flow<List<SavingsGoalEntity>> = quitDao.getAllSavingsGoalsFlow()

    private val quotes = listOf(
        MotivationQuote("You don't need to be perfect. You just need to keep moving forward."),
        MotivationQuote("One difficult moment doesn't erase your progress."),
        MotivationQuote("Your future self will thank you for the strength you show today."),
        MotivationQuote("Every craving is simply a wave. Notice it, breathe through it, and let it pass."),
        MotivationQuote("Self-control is a muscle. Every time you pause, it gets stronger."),
        MotivationQuote("Freedom is not the absence of cravings, but the mastery over your response."),
        MotivationQuote("Small daily choices build unbreakable long-term transformation.")
    )

    fun getDailyQuote(): MotivationQuote {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        return quotes[dayOfYear % quotes.size]
    }

    suspend fun completeOnboarding(
        selectedHabitTypes: List<HabitType>,
        reasons: List<String>,
        startDateTimestamp: Long,
        estimatedDailySpend: Double,
        estimatedDailyMinutes: Int
    ) {
        val profile = UserProfileEntity(
            id = 1,
            userName = "Friend",
            hasCompletedOnboarding = true,
            globalStartDate = startDateTimestamp,
            selectedReasonsCsv = reasons.joinToString("|"),
            dailySpendEstimate = estimatedDailySpend,
            dailyTimeSavedMinutes = estimatedDailyMinutes
        )
        quitDao.insertOrUpdateProfile(profile)

        val habits = selectedHabitTypes.map { habitType ->
            UserHabit(
                habitType = habitType,
                startTimestamp = startDateTimestamp,
                dailyCost = if (estimatedDailySpend > 0) estimatedDailySpend / max(1, selectedHabitTypes.size) else habitType.defaultDailyCost,
                dailyMinutesSpent = if (estimatedDailyMinutes > 0) estimatedDailyMinutes / max(1, selectedHabitTypes.size) else habitType.defaultDailyTimeMin,
                isEnabled = true
            )
        }
        quitDao.insertHabits(habits)

        // Seed initial sample check-in and encouraging journal welcome
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        quitDao.insertCheckIn(
            CheckInEntry(
                timestamp = System.currentTimeMillis(),
                dateString = todayStr,
                mood = Mood.GREAT,
                note = "Started my journey to take back control!"
            )
        )
        quitDao.insertJournalEntry(
            JournalEntry(
                timestamp = System.currentTimeMillis(),
                dateString = todayStr,
                title = "My Commitment to Recovery",
                content = "Today is Day 1 of taking back control of my life. My main reasons are: ${reasons.joinToString(", ")}. I am choosing health, focus, and freedom.",
                mood = Mood.GREAT,
                tagsCsv = "Motivation,Success"
            )
        )
    }

    suspend fun logCheckIn(mood: Mood, note: String) {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        quitDao.insertCheckIn(
            CheckInEntry(
                timestamp = System.currentTimeMillis(),
                dateString = todayStr,
                mood = mood,
                note = note
            )
        )
    }

    suspend fun logCravingOvercome(
        habitType: HabitType,
        triggerName: String,
        actionTaken: String,
        intensity: Int,
        durationSeconds: Int,
        note: String
    ) {
        quitDao.insertCravingLog(
            CravingLog(
                timestamp = System.currentTimeMillis(),
                habitType = habitType,
                triggerName = triggerName,
                intensity = intensity,
                actionTaken = actionTaken,
                wasOvercome = true,
                durationSeconds = durationSeconds,
                reflectionNote = note
            )
        )
        if (triggerName.isNotBlank()) {
            val existing = quitDao.getAllTriggersFlow()
            // increment trigger
            quitDao.insertOrUpdateTrigger(
                TriggerStat(
                    triggerName = triggerName.trim(),
                    count = 1,
                    lastOccurred = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun logJournalEntry(title: String, content: String, mood: Mood, tags: List<String>) {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        quitDao.insertJournalEntry(
            JournalEntry(
                timestamp = System.currentTimeMillis(),
                dateString = todayStr,
                title = title,
                content = content,
                mood = mood,
                tagsCsv = tags.joinToString(",")
            )
        )
    }

    suspend fun deleteJournalEntry(id: Long) = quitDao.deleteJournalEntry(id)

    suspend fun addSavingsGoal(title: String, targetAmount: Double, iconEmoji: String) {
        quitDao.insertSavingsGoal(
            SavingsGoalEntity(
                title = title,
                targetAmount = targetAmount,
                iconEmoji = iconEmoji,
                isCustom = true
            )
        )
    }

    suspend fun deleteSavingsGoal(id: Long) = quitDao.deleteSavingsGoal(id)

    suspend fun recordRelapse(habitId: Long, reason: String) {
        val habit = quitDao.getHabitById(habitId) ?: return
        val now = System.currentTimeMillis()
        val elapsedHours = max(0L, (now - habit.startTimestamp) / (1000 * 60 * 60))
        val newBest = max(habit.bestStreakHours, elapsedHours)
        val updated = habit.copy(
            startTimestamp = now,
            lastRelapseTimestamp = now,
            bestStreakHours = newBest,
            totalRelapses = habit.totalRelapses + 1
        )
        quitDao.updateHabit(updated)

        // Add compassionate journal reflection
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        quitDao.insertJournalEntry(
            JournalEntry(
                timestamp = now,
                dateString = todayStr,
                title = "Reflection & Reset",
                content = "Experienced a setback with ${habit.habitType.displayName}. Reason: ${if (reason.isNotBlank()) reason else "Felt overwhelmed"}. It's okay—progress is not erased. Learning from this trigger and continuing forward.",
                mood = Mood.DIFFICULT,
                tagsCsv = "Difficult day,Trigger"
            )
        )
    }

    suspend fun updateProfile(profile: UserProfileEntity) {
        quitDao.insertOrUpdateProfile(profile)
    }

    suspend fun resetAllProgress() {
        quitDao.clearCheckIns()
        quitDao.clearCravings()
        quitDao.clearJournal()
        val now = System.currentTimeMillis()
        val profile = quitDao.getUserProfile()
        if (profile != null) {
            quitDao.insertOrUpdateProfile(profile.copy(globalStartDate = now))
        }
    }

    fun calculateMilestones(streakDays: Int, cravingsOvercomeCount: Int, journalCount: Int, moneySaved: Double): List<Milestone> {
        val list = mutableListOf<Milestone>()
        list.add(Milestone("m1", "First Step", "Completed your first day clean.", "🌱", 1, streakDays >= 1, min(1f, streakDays / 1f)))
        list.add(Milestone("m2", "3 Days Strong", "Overcame the toughest initial 72 hours.", "🥉", 3, streakDays >= 3, min(1f, streakDays / 3f)))
        list.add(Milestone("m3", "One Week", "7 consecutive days of solid discipline.", "🥈", 7, streakDays >= 7, min(1f, streakDays / 7f)))
        list.add(Milestone("m4", "Two Weeks", "14 days clean. Brain receptors recalibrating.", "🥇", 14, streakDays >= 14, min(1f, streakDays / 14f)))
        list.add(Milestone("m5", "30 Days Clean", "One full month! New routines firmly established.", "💎", 30, streakDays >= 30, min(1f, streakDays / 30f)))
        list.add(Milestone("m6", "60 Days Solid", "Two months of resilience and mental clarity.", "🛡️", 60, streakDays >= 60, min(1f, streakDays / 60f)))
        list.add(Milestone("m7", "90 Days Reset", "Full psychological habit reboot milestone.", "🏆", 90, streakDays >= 90, min(1f, streakDays / 90f)))
        list.add(Milestone("m8", "6 Months Mastery", "Half a year of continuous self-mastery.", "👑", 180, streakDays >= 180, min(1f, streakDays / 180f)))
        list.add(Milestone("m9", "1 Year New You", "365 days clean. A transformed life.", "⭐", 365, streakDays >= 365, min(1f, streakDays / 365f)))
        list.add(Milestone("m10", "Craving Crusher", "Overcame 5 urge waves with grounding.", "💧", 5, cravingsOvercomeCount >= 5, min(1f, cravingsOvercomeCount / 5f), "Resilience"))
        list.add(Milestone("m11", "Reflective Mind", "Wrote 5 journal entries to process emotions.", "📖", 5, journalCount >= 5, min(1f, journalCount / 5f), "Reflection"))
        list.add(Milestone("m12", "Smart Saver", "Saved over $100 towards your goals.", "💰", 100, moneySaved >= 100.0, min(1f, (moneySaved / 100.0).toFloat()), "Savings"))
        return list
    }

    fun calculateHealthBenefits(streakMinutes: Long): List<HealthBenefit> {
        val benefits = listOf(
            HealthBenefit("h1", "20 Minutes", 20, "Heart Rate Normalizes", "Pulse and blood pressure begin returning to baseline healthy levels.", "❤️", streakMinutes >= 20, min(1f, streakMinutes / 20f)),
            HealthBenefit("h2", "8 Hours", 8 * 60, "Oxygen Levels Rise", "Blood oxygen levels increase to healthy normal; carbon monoxide levels drop.", "🫁", streakMinutes >= 8 * 60, min(1f, streakMinutes / (8f * 60))),
            HealthBenefit("h3", "24 Hours", 24 * 60, "Anxiety Peak Subsides", "Initial peak withdrawal waves begin moderating; recovery journey is underway.", "🧘", streakMinutes >= 24 * 60, min(1f, streakMinutes / (24f * 60))),
            HealthBenefit("h4", "48 Hours", 48 * 60, "Sensory Recovery", "Nerve endings begin rejuvenating; taste, smell, and alertness sharpen.", "✨", streakMinutes >= 48 * 60, min(1f, streakMinutes / (48f * 60))),
            HealthBenefit("h5", "72 Hours", 72 * 60, "Toxin Cleansing", "The body fully eliminates chemical residues; lung bronchial tubes begin relaxing.", "🌿", streakMinutes >= 72 * 60, min(1f, streakMinutes / (72f * 60))),
            HealthBenefit("h6", "7 Days", 7 * 24 * 60, "Dopamine Recalibration", "Brain reward centers start down-regulating artificial spikes; sleep improves.", "🧠", streakMinutes >= 7 * 24 * 60, min(1f, streakMinutes / (7f * 24 * 60))),
            HealthBenefit("h7", "14 Days", 14 * 24 * 60, "Circulation & Stamina", "Physical circulation noticeably improves; stamina, energy, and mental focus rise.", "⚡", streakMinutes >= 14 * 24 * 60, min(1f, streakMinutes / (14f * 24 * 60))),
            HealthBenefit("h8", "30 Days", 30 * 24 * 60, "Neural Pathway Rewiring", "Subconscious automatic impulses weaken significantly; self-confidence soars.", "🛡️", streakMinutes >= 30 * 24 * 60, min(1f, streakMinutes / (30f * 24 * 60))),
            HealthBenefit("h9", "60 Days", 60 * 24 * 60, "Stress Resilience", "Natural endorphin pathways function smoothly; emotional stability solidifies.", "🎯", streakMinutes >= 60 * 24 * 60, min(1f, streakMinutes / (60f * 24 * 60))),
            HealthBenefit("h10", "90 Days", 90 * 24 * 60, "Cognitive Reset", "Gray matter density in prefrontal cortex increases; habitual cravings decline by >85%.", "💎", streakMinutes >= 90 * 24 * 60, min(1f, streakMinutes / (90f * 24 * 60)))
        )
        return benefits
    }
}
