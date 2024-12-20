package com.murr.taptheumber.levels

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.murr.tapgame.KnightTourActivity
import com.murr.taptheumber.AboutActivity
import com.murr.taptheumber.AchievementsActivity
import com.murr.taptheumber.BaseActivity
import com.murr.taptheumber.R
import com.murr.taptheumber.databinding.ActivityLevelSelectBinding

class LevelSelectActivity : BaseActivity() {
    private lateinit var binding: ActivityLevelSelectBinding
    private lateinit var languageButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLevelSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        languageButton = binding.languageButton

        binding.knightTourButton.setOnClickListener {
            startActivity(Intent(this, KnightTourActivity::class.java))
        }
        binding.tapTheNumberButton.setOnClickListener {
            showTapTheNumberDifficultySelection()
        }

        binding.achievementsButton.setOnClickListener {
            startActivity(Intent(this, AchievementsActivity::class.java))
        }

        binding.aboutButton.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
        languageButton.setOnClickListener {
            showLanguageSelectionDialog()
        }
    }

    private fun showLanguageSelectionDialog() {
        // List of available languages
        val languages = arrayOf("English", "Deutsch", "Русский")

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.choose_language))
        builder.setSingleChoiceItems(languages, -1) { dialog, which ->
            when (which) {
                0 -> applyLocale("en") // English
                1 -> applyLocale("de") // Deutsch
                2 -> applyLocale("ru") // Russian
            }
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showTapTheNumberDifficultySelection() {
        val difficultyLevels = arrayOf(
            getString(R.string.easy_level),
            getString(R.string.medium_level),
            getString(R.string.hard_level)
        )

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.choose_difficulty))
        builder.setItems(difficultyLevels) { dialog, which ->
            when (which) {
                0 -> startActivity(Intent(this, EasyLevelActivity::class.java))
                1 -> startActivity(Intent(this, MediumLevelActivity::class.java))
                2 -> startActivity(Intent(this, HardLevelActivity::class.java))
            }
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}