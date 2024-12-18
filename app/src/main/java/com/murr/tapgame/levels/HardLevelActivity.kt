package com.murr.taptheumber.levels

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import androidx.core.content.ContextCompat
import com.murr.taptheumber.R
import kotlin.random.Random

class HardLevelActivity : BaseLevelActivity() {

    override val timeIncrement = 2000L
    override val timePenalty = 5000L
    override val gridColumnCount = 5
    override val gridRowCount = 5
    override val maxNumber = gridColumnCount * gridRowCount

    override fun onCreate(savedInstanceState: Bundle?) {
        timeRemaining = 20000L
        super.onCreate(savedInstanceState)
    }
}