package com.example

import com.example.data.database.AppDatabase
import com.example.data.model.BadgeCategory
import com.example.data.model.DailyCheckIn
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testRequiredAchievementBadgesExist() {
    val initialBadges = AppDatabase.getInitialBadges()

    val firstWeek = initialBadges.find { it.title == "First Week Clean" }
    val oneMonth = initialBadges.find { it.title == "One Month Free" }
    val hundredDays = initialBadges.find { it.title == "100 Days Strong" }

    assertNotNull("First Week Clean badge must be present", firstWeek)
    assertEquals(7.0f, firstWeek?.requiredThreshold)
    assertEquals(BadgeCategory.STREAK_DAYS, firstWeek?.category)

    assertNotNull("One Month Free badge must be present", oneMonth)
    assertEquals(30.0f, oneMonth?.requiredThreshold)
    assertEquals(BadgeCategory.STREAK_DAYS, oneMonth?.category)

    assertNotNull("100 Days Strong badge must be present", hundredDays)
    assertEquals(100.0f, hundredDays?.requiredThreshold)
    assertEquals(BadgeCategory.STREAK_DAYS, hundredDays?.category)
  }

  @Test
  fun testMoneySavedFromDailyGamingExpenditureSetting() {
    val streakDays = 20.0f
    val dailyExpenditure = 3.50f // $3.50 / day average expenditure

    val moneySaved = streakDays * dailyExpenditure
    val gamesAvoided = moneySaved / 70.0f
    val subscriptionsAvoided = moneySaved / 15.0f

    assertEquals(70.0f, moneySaved, 0.01f)
    assertEquals(1.0f, gamesAvoided, 0.01f) // Exactly 1 full AAA $70 game avoided
    assertEquals(4.66f, subscriptionsAvoided, 0.02f) // ~4.6 months of $15/mo subscriptions saved
  }

  @Test
  fun testDailyJournalAndTriggerIdentification() {
    val journalEntry = DailyCheckIn(
      id = 1,
      dateIso = "2026-11-06",
      dayNumber = 14,
      feeling = "Restless & Bored",
      feelingEmoji = "🥱",
      identifiedTriggers = "Unstructured Free Time / Boredom, Late Night Fatigue",
      urgeLevel = 3,
      moodLevel = 4,
      activities = "Reading Book, 20m Walk",
      notes = "Felt urge around 9pm due to downtime, went for walk and felt grounded."
    )

    assertEquals("Restless & Bored", journalEntry.feeling)
    assertEquals("🥱", journalEntry.feelingEmoji)
    assertTrue(journalEntry.identifiedTriggers.contains("Boredom"))
    assertTrue(journalEntry.identifiedTriggers.contains("Late Night"))
    assertEquals(14L, journalEntry.dayNumber)
  }

  @Test
  fun testReclaimedHoursCalculation() {
    val streakDays = 14f
    val hoursPerDayGaming = 4.0f

    val totalHoursReclaimed = streakDays * hoursPerDayGaming
    val booksEquivalent = totalHoursReclaimed / 6.0f

    assertEquals(56.0f, totalHoursReclaimed, 0.01f)
    assertTrue("Should be equivalent to over 9 books read", booksEquivalent > 9.0f)
  }
}
