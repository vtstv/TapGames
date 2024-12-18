package com.murr.taptheumber.levels

import android.os.Bundle

class EasyLevelActivity : BaseLevelActivity() {

    override val timeIncrement = 3000L
    override val timePenalty = 4000L
    override val gridColumnCount = 3
    override val gridRowCount = 3
    override val maxNumber = gridColumnCount * gridRowCount

    override fun onCreate(savedInstanceState: Bundle?) {
        timeRemaining = 45000L
        super.onCreate(savedInstanceState)
    }
}