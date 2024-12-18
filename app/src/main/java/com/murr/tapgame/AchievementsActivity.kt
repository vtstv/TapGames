package com.murr.taptheumber

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.murr.taptheumber.databinding.ActivityAchievementsBinding

class AchievementsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAchievementsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAchievementsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showHighScores()
        showAchievements()
    }

    // Call showHighScores and showAchievements in onResume
    override fun onResume() {
        super.onResume()
        showHighScores()
        showAchievements()
    }

    private fun showHighScores() {
        binding.highScoreEasyTextView.text =
            "High Score (Easy): ${Achievements.getHighScore(this, Achievements.LEVEL_EASY)}"
        binding.highScoreMediumTextView.text =
            "High Score (Medium): ${Achievements.getHighScore(this, Achievements.LEVEL_MEDIUM)}"
        binding.highScoreHardTextView.text =
            "High Score (Hard): ${Achievements.getHighScore(this, Achievements.LEVEL_HARD)}"
    }

    private fun showAchievements() {
        binding.achievementEasyTextView.text =
            "Reach Easy Level: ${getAchievementStatus(Achievements.KEY_REACHED_LEVEL_EASY)}"
        binding.achievementMediumTextView.text =
            "Reach Medium Level: ${getAchievementStatus(Achievements.KEY_REACHED_LEVEL_MEDIUM)}"
        binding.achievementHardTextView.text =
            "Reach Hard Level: ${getAchievementStatus(Achievements.KEY_REACHED_LEVEL_HARD)}"
    }

    private fun getAchievementStatus(achievementKey: String): String {
        return if (Achievements.isAchievementUnlocked(this, achievementKey)) "Unlocked" else "Locked"
    }
}