package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_checkins")
data class DailyCheckIn(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateIso: String, // e.g. "2026-11-06"
    val timestamp: Long = System.currentTimeMillis(),
    val dayNumber: Long = 1, // e.g. Day 14 of sobriety journey
    val feeling: String = "Balanced", // e.g. "Calm & Grounded", "Restless & Bored", "Stressed", "Energized", "Lonely"
    val feelingEmoji: String = "🙂",
    val identifiedTriggers: String = "", // comma-separated triggers identified today
    val urgeLevel: Int = 1, // 1 (none) to 5 (extreme)
    val moodLevel: Int = 3, // 1 (low) to 5 (great)
    val activities: String = "", // replacement activities
    val notes: String = "" // user's daily journal reflection
)
