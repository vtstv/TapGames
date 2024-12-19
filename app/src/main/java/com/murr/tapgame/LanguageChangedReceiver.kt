package com.murr.taptheumber

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class LanguageChangedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Recreate the activity to apply the new locale
        if (context is Activity) {
            context.recreate()
        }
    }
}