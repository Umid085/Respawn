package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.dao.BadgeDao
import com.example.data.dao.CravingDao
import com.example.data.dao.DailyCheckInDao
import com.example.data.dao.PersonalRewardDao
import com.example.data.dao.QuestDao
import com.example.data.dao.UserProfileDao
import com.example.data.model.Badge
import com.example.data.model.BadgeCategory
import com.example.data.model.BadgeTier
import com.example.data.model.CravingEvent
import com.example.data.model.DailyCheckIn
import com.example.data.model.PersonalReward
import com.example.data.model.QuestItem
import com.example.data.model.UserProfile

class DatabaseConverters {
    @TypeConverter
    fun fromBadgeTier(tier: BadgeTier): String = tier.name

    @TypeConverter
    fun toBadgeTier(value: String): BadgeTier = try {
        BadgeTier.valueOf(value)
    } catch (_: Exception) {
        BadgeTier.BRONZE
    }

    @TypeConverter
    fun fromBadgeCategory(category: BadgeCategory): String = category.name

    @TypeConverter
    fun toBadgeCategory(value: String): BadgeCategory = try {
        BadgeCategory.valueOf(value)
    } catch (_: Exception) {
        BadgeCategory.STREAK_DAYS
    }
}

@Database(
    entities = [
        UserProfile::class,
        Badge::class,
        DailyCheckIn::class,
        CravingEvent::class,
        QuestItem::class,
        PersonalReward::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun badgeDao(): BadgeDao
    abstract fun dailyCheckInDao(): DailyCheckInDao
    abstract fun cravingDao(): CravingDao
    abstract fun questDao(): QuestDao
    abstract fun personalRewardDao(): PersonalRewardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "respawn_tracker.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        fun getInitialBadges(): List<Badge> = listOf(
            Badge(
                id = "badge_24h",
                title = "The Unplugging",
                category = BadgeCategory.STREAK_DAYS,
                tier = BadgeTier.BRONZE,
                requiredThreshold = 1.0f,
                description = "24 consecutive hours free of video games. You broke the auto-pilot loop.",
                neuroBenefit = "Brain begins sensing absence of high-frequency artificial dopamine spikes.",
                xpReward = 100
            ),
            Badge(
                id = "badge_3d",
                title = "Dopamine Shockwave",
                category = BadgeCategory.STREAK_DAYS,
                tier = BadgeTier.BRONZE,
                requiredThreshold = 3.0f,
                description = "3 days clean. Survived the acute withdrawal storm of restlessness and boredom.",
                neuroBenefit = "Peak physical agitation subsides. Restlessness transforms into usable raw energy.",
                xpReward = 150
            ),
            Badge(
                id = "badge_7d",
                title = "First Week Clean",
                category = BadgeCategory.STREAK_DAYS,
                tier = BadgeTier.SILVER,
                requiredThreshold = 7.0f,
                description = "7 full days without gaming! You proved you have command over your actions.",
                neuroBenefit = "Sleep cycles (REM) begin restorative rebound. Morning brain fog begins lifting.",
                xpReward = 350
            ),
            Badge(
                id = "badge_14d",
                title = "Fortnight Free",
                category = BadgeCategory.STREAK_DAYS,
                tier = BadgeTier.SILVER,
                requiredThreshold = 14.0f,
                description = "Two weeks clean. Environmental gaming triggers are losing their automatic power.",
                neuroBenefit = "Lower-intensity daily activities (reading, conversation, walking) start feeling engaging again.",
                xpReward = 500
            ),
            Badge(
                id = "badge_21d",
                title = "Neuroplastic Spark",
                category = BadgeCategory.STREAK_DAYS,
                tier = BadgeTier.SILVER,
                requiredThreshold = 21.0f,
                description = "21 days clean. The neurological threshold where new replacement routines take root.",
                neuroBenefit = "Prefrontal cortex strengthens impulse inhibition. Attention span starts recovering.",
                xpReward = 750
            ),
            Badge(
                id = "badge_30d",
                title = "One Month Free",
                category = BadgeCategory.STREAK_DAYS,
                tier = BadgeTier.GOLD,
                requiredThreshold = 30.0f,
                description = "30 full days gaming-free! A milestone only the truly dedicated achieve.",
                neuroBenefit = "Dopamine D2 receptor density shows marked biological recovery. Baseline motivation improves.",
                xpReward = 1200
            ),
            Badge(
                id = "badge_60d",
                title = "Titanium Will",
                category = BadgeCategory.STREAK_DAYS,
                tier = BadgeTier.GOLD,
                requiredThreshold = 60.0f,
                description = "60 days of real-world living. Urges have transformed into occasional brief ripples.",
                neuroBenefit = "Frustration tolerance and emotional stability significantly enhanced in daily life.",
                xpReward = 1800
            ),
            Badge(
                id = "badge_100d",
                title = "100 Days Strong",
                category = BadgeCategory.STREAK_DAYS,
                tier = BadgeTier.PLATINUM,
                requiredThreshold = 100.0f,
                description = "100 days of sovereignty! Triple-digit mastery and complete lifestyle metamorphosis.",
                neuroBenefit = "Full dopamine system reset. Real-life challenges provide genuine internal fulfillment.",
                xpReward = 3000
            ),
            Badge(
                id = "badge_180d",
                title = "Half-Year Monarch",
                category = BadgeCategory.STREAK_DAYS,
                tier = BadgeTier.PLATINUM,
                requiredThreshold = 180.0f,
                description = "6 months clean. You have rebuilt your habits and reclaimed hundreds of hours.",
                neuroBenefit = "Deep neuroadaptation complete. Identity shifted from 'ex-gamer' to high-functioning creator.",
                xpReward = 5000
            ),
            Badge(
                id = "badge_365d",
                title = "Respawn Legend",
                category = BadgeCategory.STREAK_DAYS,
                tier = BadgeTier.DIAMOND,
                requiredThreshold = 365.0f,
                description = "1 full calendar year without video games! Legendary victory over addiction.",
                neuroBenefit = "Complete cognitive and life rebirth. Your potential is fully unlocked in the real world.",
                xpReward = 10000
            ),
            Badge(
                id = "hours_25",
                title = "25 Hours Regained",
                category = BadgeCategory.HOURS_SAVED,
                tier = BadgeTier.BRONZE,
                requiredThreshold = 25.0f,
                description = "25 hours of your mortal life reclaimed from digital screens.",
                neuroBenefit = "Equivalent time to read 3 full-length books or complete 20 gym workouts.",
                xpReward = 200
            ),
            Badge(
                id = "hours_100",
                title = "Centurion Hours",
                category = BadgeCategory.HOURS_SAVED,
                tier = BadgeTier.SILVER,
                requiredThreshold = 100.0f,
                description = "100 hours saved! That's more than two full work weeks of productive vitality.",
                neuroBenefit = "Sufficient time to build a usable prototype app, learn basic guitar, or complete a course.",
                xpReward = 600
            ),
            Badge(
                id = "hours_500",
                title = "Grand Master of Time",
                category = BadgeCategory.HOURS_SAVED,
                tier = BadgeTier.PLATINUM,
                requiredThreshold = 500.0f,
                description = "500 hours reclaimed! A colossal investment back into your real self.",
                neuroBenefit = "Halfway to true intermediate mastery in any physical or intellectual domain.",
                xpReward = 2500
            ),
            Badge(
                id = "urge_first",
                title = "Wave Rider",
                category = BadgeCategory.CRAVING_SURF,
                tier = BadgeTier.BRONZE,
                requiredThreshold = 1.0f,
                description = "Successfully surfed your first severe craving without giving in to gaming.",
                neuroBenefit = "Proves urges are temporary 15-minute waves that rise and fall on their own.",
                xpReward = 200
            ),
            Badge(
                id = "urge_five",
                title = "Iron Fortress",
                category = BadgeCategory.CRAVING_SURF,
                tier = BadgeTier.SILVER,
                requiredThreshold = 5.0f,
                description = "Conquered 5 craving emergencies with breathwork and mindfulness.",
                neuroBenefit = "Dramatically strengthens the amygdala-prefrontal regulatory circuit.",
                xpReward = 600
            )
        )

        fun getInitialQuests(): List<QuestItem> = listOf(
            QuestItem(
                title = "20-minute screen-free walk outdoors",
                category = "Fitness",
                xpReward = 30
            ),
            QuestItem(
                title = "Read 15 pages of a physical book",
                category = "Mind",
                xpReward = 35
            ),
            QuestItem(
                title = "Tidy and declutter your desk area",
                category = "Declutter",
                xpReward = 30
            ),
            QuestItem(
                title = "Cook a fresh, nourishing home meal",
                category = "Life Admin",
                xpReward = 40
            ),
            QuestItem(
                title = "15-minute bodyweight or stretching workout",
                category = "Fitness",
                xpReward = 35
            ),
            QuestItem(
                title = "Spend 30 minutes on a real-life project or craft",
                category = "Skill",
                xpReward = 50
            ),
            QuestItem(
                title = "Reach out to a friend or family member",
                category = "Social",
                xpReward = 30
            ),
            QuestItem(
                title = "Write down 3 things you are grateful for today",
                category = "Mind",
                xpReward = 25
            )
        )

        fun getInitialRewards(): List<PersonalReward> = listOf(
            PersonalReward(
                title = "Treat myself to a gourmet meal or favorite restaurant",
                targetDays = 7,
                estimatedCost = 35f,
                notes = "Celebrate the First Week Clean milestone!"
            ),
            PersonalReward(
                title = "Buy top-tier running / workout shoes",
                targetDays = 30,
                estimatedCost = 120f,
                notes = "Funded by money saved from gaming and microtransactions."
            ),
            PersonalReward(
                title = "Weekend camping or road trip getaway",
                targetDays = 60,
                estimatedCost = 200f,
                notes = "Enjoying the physical world and real nature."
            ),
            PersonalReward(
                title = "Grand 100-Day Milestone celebration & personal gear upgrade",
                targetDays = 100,
                estimatedCost = 300f,
                notes = "100 Days Strong! Reborn in reality."
            )
        )
    }
}
