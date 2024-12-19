package com.murr.taptheumber

import android.content.Context

object Achievements {
    private const val PREFS_NAME = "achievements"

    // High Score Keys
    private const val KEY_HIGH_SCORE_EASY = "high_score_easy"
    private const val KEY_HIGH_SCORE_MEDIUM = "high_score_medium"
    private const val KEY_HIGH_SCORE_HARD = "high_score_hard"

    const val LEVEL_EASY = "easy"
    const val LEVEL_MEDIUM = "medium"
    const val LEVEL_HARD = "hard"

    // Knight Tour Achievement Keys
    const val KEY_KNIGHT_TOUR_10_MOVES = "knight_tour_10_moves"
    const val KEY_KNIGHT_TOUR_50_MOVES = "knight_tour_50_moves"
    const val KEY_KNIGHT_TOUR_100_MOVES = "knight_tour_100_moves"

    // Achievement Keys
    const val KEY_REACHED_LEVEL_EASY = "reached_level_easy"
    const val KEY_REACHED_LEVEL_MEDIUM = "reached_level_medium"
    const val KEY_REACHED_LEVEL_HARD = "reached_level_hard"

    fun getHighScore(context: Context, level: String): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return when (level) {
            LEVEL_EASY -> prefs.getInt(KEY_HIGH_SCORE_EASY, 0)
            LEVEL_MEDIUM -> prefs.getInt(KEY_HIGH_SCORE_MEDIUM, 0)
            LEVEL_HARD -> prefs.getInt(KEY_HIGH_SCORE_HARD, 0)
            else -> 0
        }
    }

    fun setHighScore(context: Context, level: String, score: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        when (level) {
            LEVEL_EASY -> editor.putInt(KEY_HIGH_SCORE_EASY, score)
            LEVEL_MEDIUM -> editor.putInt(KEY_HIGH_SCORE_MEDIUM, score)
            LEVEL_HARD -> editor.putInt(KEY_HIGH_SCORE_HARD, score)
        }
        editor.apply()
    }

    fun unlockAchievement(context: Context, achievementKey: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (!prefs.getBoolean(achievementKey, false)) {
            val editor = prefs.edit()
            editor.putBoolean(achievementKey, true)
            editor.apply()
        }
    }

    fun initializeKnightTourAchievements(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        if (!prefs.contains(KEY_KNIGHT_TOUR_10_MOVES)) {
            editor.putBoolean(KEY_KNIGHT_TOUR_10_MOVES, false)
        }
        if (!prefs.contains(KEY_KNIGHT_TOUR_50_MOVES)) {
            editor.putBoolean(KEY_KNIGHT_TOUR_50_MOVES, false)
        }
        if (!prefs.contains(KEY_KNIGHT_TOUR_100_MOVES)) {
            editor.putBoolean(KEY_KNIGHT_TOUR_100_MOVES, false)
        }
        editor.apply()
    }

    fun isAchievementUnlocked(context: Context, achievementKey: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(achievementKey, false)
    }

    // unlock level achievements directly in endGame()
    fun checkAndUnlockLevelAchievements(context: Context, level: String) {
        when (level) {
            LEVEL_EASY -> unlockAchievement(context, KEY_REACHED_LEVEL_EASY)
            LEVEL_MEDIUM -> unlockAchievement(context, KEY_REACHED_LEVEL_MEDIUM)
            LEVEL_HARD -> unlockAchievement(context, KEY_REACHED_LEVEL_HARD)
        }
    }
}