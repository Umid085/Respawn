package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quests")
data class QuestItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // "Fitness", "Mind", "Social", "Declutter", "Skill"
    val xpReward: Int = 30,
    val isCompletedToday: Boolean = false,
    val lastCompletedDate: String? = null,
    val completionCount: Int = 0,
    val isCustom: Boolean = false
)

@Entity(tableName = "personal_rewards")
data class PersonalReward(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val targetDays: Int,
    val estimatedCost: Float = 0f,
    val isClaimed: Boolean = false,
    val notes: String = ""
)
