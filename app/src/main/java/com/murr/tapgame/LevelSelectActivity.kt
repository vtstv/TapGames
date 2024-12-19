package com.murr.taptheumber.levels

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.ConfigurationCompat
import com.murr.taptheumber.AchievementsActivity
import com.murr.taptheumber.databinding.ActivityLevelSelectBinding
import com.murr.taptheumber.AboutActivity
import com.murr.taptheumber.HighScoresActivity
import com.murr.taptheumber.R
import java.util.Locale

class LevelSelectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLevelSelectBinding
    private lateinit var languageButton: Button
    private val sharedPrefFile = "com.murr.taptheumber.sharedprefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocale()
        binding = ActivityLevelSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        languageButton = binding.languageButton

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

        binding.highScoresButton.setOnClickListener {
            startActivity(Intent(this, HighScoresActivity::class.java))
        }

        binding.aboutButton.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
        binding.knightTourButton.setOnClickListener {
            startActivity(Intent(this, KnightTourActivity::class.java))
        }
        languageButton.setOnClickListener {
            showLanguageSelectionDialog()
        }
    }
    private fun showLanguageSelectionDialog() {
        // List of available languages (you can add more)
        val languages = arrayOf("English", "Русский")

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.choose_language))
        builder.setSingleChoiceItems(languages, -1) { dialog, which ->
            when (which) {
                0 -> setLocale("en") // English
                1 -> setLocale("ru") // Russian
            }
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)

        // Save the selected language in SharedPreferences
        val sharedPreferences = getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("language", languageCode)
        editor.apply()

        // Recreate the activity to apply the new locale
        recreate()
    }

    private fun loadLocale() {
        val sharedPreferences = getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val languageCode = sharedPreferences.getString("language", "en") // Default to English

        val locale = Locale(languageCode!!)
        Locale.setDefault(locale)

        val resources = resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}