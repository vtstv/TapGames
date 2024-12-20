package com.murr.taptheumber

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {

    private lateinit var appIconImageView: ImageView
    private lateinit var appNameTextView: TextView
    private lateinit var versionTextView: TextView
    private lateinit var aboutText: TextView
    private lateinit var developerName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        appIconImageView = findViewById(R.id.appIconImageView)
        appNameTextView = findViewById(R.id.appNameTextView)
        versionTextView = findViewById(R.id.versionTextView)
        aboutText = findViewById(R.id.aboutText)
        developerName = findViewById(R.id.developerName)

        animateAppIcon()
        animateAppName()
        animateVersion()
        animateAboutText()

        developerName.setOnClickListener {
            onDeveloperNameClicked(it)
        }
    }

    private fun animateAppIcon() {
        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate)
        appIconImageView.startAnimation(rotateAnimation)
    }

    private fun animateAppName() {
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        appNameTextView.startAnimation(fadeInAnimation)
    }

    private fun animateVersion() {
        val slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        versionTextView.startAnimation(slideUpAnimation)
    }

    private fun animateAboutText() {
        val slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        // Delay the animation slightly
        slideUpAnimation.startOffset = 300
        aboutText.startAnimation(slideUpAnimation)
    }

    fun onDeveloperNameClicked(view: View) {
        val linkedInProfileUrl = "https://murr.li"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkedInProfileUrl))

        // Check if there's an app to handle the intent before starting the activity
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            // Handle the case where there's no app to handle the intent (e.g., display a message)
            Toast.makeText(this, "No app found to open LinkedIn profile", Toast.LENGTH_SHORT).show()
        }
    }
}