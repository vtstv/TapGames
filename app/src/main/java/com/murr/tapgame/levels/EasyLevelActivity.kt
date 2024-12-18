package com.murr.taptheumber.levels

import android.os.Bundle
import com.murr.taptheumber.Achievements

class EasyLevelActivity : BaseLevelActivity() {
    override val timeIncrement = 3000L
    override val timePenalty = 4000L
    override val gridColumnCount = 4
    override val gridRowCount = 2
    override val maxNumber = gridColumnCount * gridRowCount

    override val level: String = Achievements.LEVEL_EASY

    override fun onCreate(savedInstanceState: Bundle?) {
        timeRemaining = 45000L
        super.onCreate(savedInstanceState)
    }
}