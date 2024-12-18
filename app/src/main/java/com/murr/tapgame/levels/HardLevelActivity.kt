package com.murr.taptheumber.levels

import android.os.Bundle
import com.murr.taptheumber.Achievements

class HardLevelActivity : BaseLevelActivity() {
    override val timeIncrement = 2000L
    override val timePenalty = 5000L
    override val gridColumnCount = 4
    override val gridRowCount = 6
    override val maxNumber = gridColumnCount * gridRowCount

    override val level: String = Achievements.LEVEL_HARD

    override fun onCreate(savedInstanceState: Bundle?) {
        timeRemaining = 20000L
        super.onCreate(savedInstanceState)
    }
}