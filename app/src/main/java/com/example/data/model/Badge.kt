package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BadgeTier {
    BRONZE,
    SILVER,
    GOLD,
    PLATINUM,
    DIAMOND
}

enum class BadgeCategory {
    STREAK_DAYS,
    HOURS_SAVED,
    MONEY_SAVED,
    QUESTS,
    CRAVING_SURF
}

@Entity(tableName = "badges")
data class Badge(
    @PrimaryKey val id: String,
    val title: String,
    val category: BadgeCategory,
    val tier: BadgeTier,
    val requiredThreshold: Float, // Days clean, hours saved, etc.
    val description: String,
    val neuroBenefit: String,
    val xpReward: Int,
    val unlockedAt: Long? = null // null if locked
)
