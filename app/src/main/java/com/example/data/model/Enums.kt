package com.example.data.model

enum class HabitType(val displayName: String, val iconEmoji: String, val defaultDailyCost: Double, val defaultDailyTimeMin: Int) {
    SMOKING("Smoking", "🚬", 12.0, 45),
    ALCOHOL("Alcohol", "🍺", 15.0, 60),
    PORN_MASTURBATION("Porn & Masturbation", "🔒", 0.0, 50),
    CUSTOM("Custom Habit", "⚡", 10.0, 30);

    companion object {
        fun fromString(type: String): HabitType {
            return entries.firstOrNull { it.name.equals(type, ignoreCase = true) } ?: CUSTOM
        }
    }
}

enum class Mood(val displayName: String, val emoji: String, val rating: Int) {
    GREAT("Great", "😊", 5),
    GOOD("Good", "🙂", 4),
    OKAY("Okay", "😐", 3),
    DIFFICULT("Difficult", "😟", 2),
    VERY_DIFFICULT("Very difficult", "😣", 1);

    companion object {
        fun fromRating(rating: Int): Mood {
            return entries.firstOrNull { it.rating == rating } ?: OKAY
        }
    }
}

enum class JournalTag(val tagLabel: String) {
    CRAVING("Craving"),
    STRESS("Stress"),
    SUCCESS("Success"),
    TRIGGER("Trigger"),
    MOTIVATION("Motivation"),
    DIFFICULT_DAY("Difficult day"),
    MILESTONE("Milestone"),
    GRATITUDE("Gratitude")
}
