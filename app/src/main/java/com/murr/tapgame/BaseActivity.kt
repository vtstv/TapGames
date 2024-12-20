package com.murr.taptheumber

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

open class BaseActivity : AppCompatActivity() {

    private val sharedPrefFile = "com.murr.taptheumber.sharedprefs"
    private lateinit var receiver: LanguageChangedReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        receiver = LanguageChangedReceiver()
        registerReceiver(receiver, IntentFilter("com.murr.taptheumber.LANGUAGE_CHANGED"))
        loadLocale()
    }

    override fun onResume() {
        super.onResume()
        loadLocale()
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

    protected fun applyLocale(languageCode: String) {
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

        // Notify other activities about the locale change
        val intent = Intent("com.murr.taptheumber.LANGUAGE_CHANGED")
        sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}