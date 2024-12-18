package com.murr.taptheumber

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.murr.taptheumber.databinding.ActivityHighScoresBinding

class HighScoresActivity: AppCompatActivity() {
    private lateinit var binding: ActivityHighScoresBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHighScoresBinding.inflate (layoutInflater)
        setContentView (binding.root)
        showHighScores ()
    }

    //  showHighScores() in onResume
    override fun onResume() {
        super.onResume()
        showHighScores()
    }

    private fun showHighScores() {
        binding.highScoreEasyTextView.text = "High Score (Easy): ${Achievements.getHighScore (this, Achievements.LEVEL_EASY)}"
        binding.highScoreMediumTextView.text = "High Score (Medium): ${Achievements.getHighScore (this, Achievements.LEVEL_MEDIUM)}"
        binding.highScoreHardTextView.text = "High Score (Hard): ${Achievements.getHighScore (this, Achievements.LEVEL_HARD)}"
    }
}