package com.murr.taptheumber.levels

import android.os.Bundle
import com.murr.taptheumber.Achievements

class HardLevelActivity : BaseLevelActivity() {
    override val timeIncrement = 1000L
    override val timePenalty = 7000L
    override val gridColumnCount = 4
    override val gridRowCount = 5
    override val maxNumber = gridColumnCount * gridRowCount

    override val level: String = Achievements.LEVEL_HARD

    override fun onCreate(savedInstanceState: Bundle?) {
        timeRemaining = 12000L
        super.onCreate(savedInstanceState)
    }
}