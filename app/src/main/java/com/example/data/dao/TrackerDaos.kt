package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Badge
import com.example.data.model.CravingEvent
import com.example.data.model.DailyCheckIn
import com.example.data.model.PersonalReward
import com.example.data.model.QuestItem
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfile)

    @Update
    suspend fun update(profile: UserProfile)
}

@Dao
interface BadgeDao {
    @Query("SELECT * FROM badges ORDER BY requiredThreshold ASC")
    fun getAllBadges(): Flow<List<Badge>>

    @Query("SELECT * FROM badges WHERE unlockedAt IS NOT NULL ORDER BY unlockedAt DESC")
    fun getUnlockedBadges(): Flow<List<Badge>>

    @Query("SELECT * FROM badges WHERE id = :id LIMIT 1")
    suspend fun getBadgeById(id: String): Badge?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBadges(badges: List<Badge>)

    @Update
    suspend fun updateBadge(badge: Badge)

    @Query("UPDATE badges SET unlockedAt = :unlockedTime WHERE id = :id AND unlockedAt IS NULL")
    suspend fun unlockBadge(id: String, unlockedTime: Long): Int
}

@Dao
interface DailyCheckInDao {
    @Query("SELECT * FROM daily_checkins ORDER BY timestamp DESC")
    fun getAllCheckIns(): Flow<List<DailyCheckIn>>

    @Query("SELECT * FROM daily_checkins WHERE dateIso = :dateIso LIMIT 1")
    suspend fun getCheckInForDate(dateIso: String): DailyCheckIn?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(checkIn: DailyCheckIn)

    @Query("SELECT COUNT(*) FROM daily_checkins")
    fun getCheckInCount(): Flow<Int>
}

@Dao
interface CravingDao {
    @Query("SELECT * FROM craving_events ORDER BY timestamp DESC")
    fun getAllCravings(): Flow<List<CravingEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCraving(event: CravingEvent)

    @Query("SELECT COUNT(*) FROM craving_events WHERE successful = 1")
    fun getSurfedCravingsCount(): Flow<Int>
}

@Dao
interface QuestDao {
    @Query("SELECT * FROM quests ORDER BY id ASC")
    fun getAllQuests(): Flow<List<QuestItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuest(quest: QuestItem)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertQuests(quests: List<QuestItem>)

    @Update
    suspend fun updateQuest(quest: QuestItem)

    @Query("DELETE FROM quests WHERE id = :id")
    suspend fun deleteQuest(id: Long)
}

@Dao
interface PersonalRewardDao {
    @Query("SELECT * FROM personal_rewards ORDER BY targetDays ASC")
    fun getAllRewards(): Flow<List<PersonalReward>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReward(reward: PersonalReward)

    @Update
    suspend fun updateReward(reward: PersonalReward)

    @Query("DELETE FROM personal_rewards WHERE id = :id")
    suspend fun deleteReward(id: Long)
}
