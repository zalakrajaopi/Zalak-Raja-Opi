package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CheckInEntry
import com.example.data.model.CravingLog
import com.example.data.model.JournalEntry
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TriggerStat
import com.example.data.model.UserHabit
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserHabit::class,
        CheckInEntry::class,
        JournalEntry::class,
        CravingLog::class,
        TriggerStat::class,
        SavingsGoalEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class QuitDatabase : RoomDatabase() {

    abstract fun quitDao(): QuitDao

    companion object {
        @Volatile
        private var INSTANCE: QuitDatabase? = null

        fun getInstance(context: Context): QuitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuitDatabase::class.java,
                    "quit_recovery_database.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Initial default data seeding
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getInstance(context).quitDao()
                                seedInitialData(dao)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedInitialData(dao: QuitDao) {
            // Default preset goals
            val defaultGoals = listOf(
                SavingsGoalEntity(title = "Wireless Noise-Canceling Headphones", targetAmount = 150.0, iconEmoji = "🎧"),
                SavingsGoalEntity(title = "Weekend Wellness Retreat", targetAmount = 400.0, iconEmoji = "🏕️"),
                SavingsGoalEntity(title = "New Smartphone", targetAmount = 800.0, iconEmoji = "📱"),
                SavingsGoalEntity(title = "Emergency Freedom Fund", targetAmount = 1500.0, iconEmoji = "🛡️"),
                SavingsGoalEntity(title = "Skill Course / Education", targetAmount = 2500.0, iconEmoji = "🎓")
            )
            dao.insertSavingsGoals(defaultGoals)

            // Common starter triggers
            val starterTriggers = listOf(
                TriggerStat(triggerName = "Work & Life Stress", count = 4),
                TriggerStat(triggerName = "Late Night Scrolling", count = 3),
                TriggerStat(triggerName = "Boredom & Free Time", count = 3),
                TriggerStat(triggerName = "Social Outings & Gatherings", count = 2),
                TriggerStat(triggerName = "Feeling Lonely or Down", count = 2)
            )
            starterTriggers.forEach { dao.insertOrUpdateTrigger(it) }
        }
    }
}
