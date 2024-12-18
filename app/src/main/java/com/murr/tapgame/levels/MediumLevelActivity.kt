package com.murr.taptheumber.levels

import android.os.Bundle
import com.murr.taptheumber.Achievements

class MediumLevelActivity : BaseLevelActivity() {
    override val timeIncrement = 2000L
    override val timePenalty = 5000L
    override val gridColumnCount = 4
    override val gridRowCount = 4
    override val maxNumber = gridColumnCount * gridRowCount

    // ADDED: Set the level here
    override val level: String = Achievements.LEVEL_MEDIUM

    override fun onCreate(savedInstanceState: Bundle?) {
        timeRemaining = 30000L
        super.onCreate(savedInstanceState)
    }
}