package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val quitTimestamp: Long = System.currentTimeMillis(),
    val hoursPerDayGaming: Float = 4.0f,
    val dailyGamingSpendDollars: Float = 3.50f, // Average daily gaming expenditure setting
    val monthlySpendDollars: Float = 105.0f,
    val primaryGameGenre: String = "Competitive Shooters & RPGs",
    val whyStatement: String = "To regain mental clarity, master my real life, protect my relationships, and invest my hours into meaningful skills.",
    val totalXp: Int = 0,
    val level: Int = 1
)
