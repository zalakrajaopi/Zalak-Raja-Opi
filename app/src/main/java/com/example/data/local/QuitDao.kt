package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CheckInEntry
import com.example.data.model.CravingLog
import com.example.data.model.JournalEntry
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TriggerStat
import com.example.data.model.UserHabit
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuitDao {

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfileFlow(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    // User Habits
    @Query("SELECT * FROM user_habits WHERE isEnabled = 1")
    fun getActiveHabitsFlow(): Flow<List<UserHabit>>

    @Query("SELECT * FROM user_habits")
    fun getAllHabitsFlow(): Flow<List<UserHabit>>

    @Query("SELECT * FROM user_habits WHERE id = :id")
    suspend fun getHabitById(id: Long): UserHabit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: UserHabit): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabits(habits: List<UserHabit>)

    @Update
    suspend fun updateHabit(habit: UserHabit)

    @Query("DELETE FROM user_habits WHERE id = :id")
    suspend fun deleteHabit(id: Long)

    // Check-ins
    @Query("SELECT * FROM check_ins ORDER BY timestamp DESC")
    fun getAllCheckInsFlow(): Flow<List<CheckInEntry>>

    @Query("SELECT * FROM check_ins WHERE dateString = :dateString LIMIT 1")
    suspend fun getCheckInForDate(dateString: String): CheckInEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(checkIn: CheckInEntry): Long

    // Journal
    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    fun getAllJournalEntriesFlow(): Flow<List<JournalEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: JournalEntry): Long

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteJournalEntry(id: Long)

    // Craving Logs
    @Query("SELECT * FROM craving_logs ORDER BY timestamp DESC")
    fun getAllCravingsFlow(): Flow<List<CravingLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCravingLog(log: CravingLog): Long

    // Trigger Stats
    @Query("SELECT * FROM trigger_stats ORDER BY count DESC")
    fun getAllTriggersFlow(): Flow<List<TriggerStat>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateTrigger(trigger: TriggerStat)

    // Savings Goals
    @Query("SELECT * FROM savings_goals ORDER BY targetAmount ASC")
    fun getAllSavingsGoalsFlow(): Flow<List<SavingsGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsGoal(goal: SavingsGoalEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsGoals(goals: List<SavingsGoalEntity>)

    @Update
    suspend fun updateSavingsGoal(goal: SavingsGoalEntity)

    @Query("DELETE FROM savings_goals WHERE id = :id")
    suspend fun deleteSavingsGoal(id: Long)

    // Reset Data
    @Query("DELETE FROM check_ins")
    suspend fun clearCheckIns()

    @Query("DELETE FROM craving_logs")
    suspend fun clearCravings()

    @Query("DELETE FROM journal_entries")
    suspend fun clearJournal()
}
