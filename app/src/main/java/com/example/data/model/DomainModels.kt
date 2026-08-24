package com.example.data.model

data class Milestone(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val targetDays: Int,
    val isUnlocked: Boolean,
    val progressFraction: Float,
    val category: String = "Streak"
)

data class HealthBenefit(
    val id: String,
    val timeframeTitle: String,
    val targetMinutes: Long,
    val title: String,
    val description: String,
    val categoryEmoji: String,
    val isAchieved: Boolean,
    val progressPercent: Float
)

data class MotivationQuote(
    val quote: String,
    val author: String = "Quit Philosophy"
)
