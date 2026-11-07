package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "craving_events")
data class CravingEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val trigger: String, // "Boredom", "Stress", "Fatigue", "Gaming Content", "Loneliness"
    val intensity: Int, // 1 to 10
    val strategyUsed: String, // "Box Breathing", "Walk Outside", "Cold Water", "Manifesto Read"
    val successful: Boolean = true,
    val note: String = ""
)
