package com.murr.taptheumber.levels

import android.os.Bundle

class MediumLevelActivity : BaseLevelActivity() {

    override val timeIncrement = 2000L
    override val timePenalty = 5000L
    override val gridColumnCount = 4
    override val gridRowCount = 4
    override val maxNumber = gridColumnCount * gridRowCount

    override fun onCreate(savedInstanceState: Bundle?) {
        timeRemaining = 30000L
        super.onCreate(savedInstanceState)
    }
}