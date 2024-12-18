package com.murr.taptheumber.levels

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.murr.taptheumber.AchievementsActivity
import com.murr.taptheumber.databinding.ActivityLevelSelectBinding
import com.murr.taptheumber.AboutActivity
import com.murr.taptheumber.HighScoresActivity

class LevelSelectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLevelSelectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLevelSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.easyButton.setOnClickListener {
            startActivity(Intent(this, EasyLevelActivity::class.java))
        }

        binding.mediumButton.setOnClickListener {
            startActivity(Intent(this, MediumLevelActivity::class.java))
        }

        binding.hardButton.setOnClickListener {
            startActivity(Intent(this, HardLevelActivity::class.java))
        }

        binding.achievementsButton.setOnClickListener {
            startActivity(Intent(this, AchievementsActivity::class.java))
        }

        binding.aboutButton.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

        binding.highScoresButton.setOnClickListener {
            startActivity(Intent(this, HighScoresActivity::class.java))
        }
    }
}