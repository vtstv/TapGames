package com.murr.taptheumber.levels

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.murr.taptheumber.Achievements
import com.murr.taptheumber.R
import com.murr.taptheumber.databinding.ActivityMainBinding
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartyFactory
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit

abstract class BaseLevelActivity : AppCompatActivity() {
    protected lateinit var binding: ActivityMainBinding
    protected var currentNumber = 1
    protected var score = 0
    protected var isGameRunning = false
    protected var timer: CountDownTimer? = null
    protected var timeRemaining = 0L
    protected open val timeIncrement = 2000L
    protected open val timePenalty = 5000L
    protected var lastTapTime = 0L
    protected var comboMultiplier = 1
    protected open val gridColumnCount = 4
    protected open val gridRowCount = 4
    protected open val maxNumber = gridColumnCount * gridRowCount

    // ADDED: Abstract property for level
    protected abstract val level: String

    // ADDED: Button size
    private val buttonSize = 120 // You can adjust this value

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.startButton.setOnClickListener {
            startGame()
        }
        setupGame()
    }

    protected open fun setupGame() {
        currentNumber = 1
        score = 0
        comboMultiplier = 1
        binding.scoreTextView.text = "Score: 0"
        binding.timerTextView.text = "Time: ${timeRemaining / 1000}"
        binding.buttonsGridLayout.columnCount = gridColumnCount
        binding.buttonsGridLayout.rowCount = gridRowCount
        val numbers = (1..maxNumber).shuffled()
        binding.buttonsGridLayout.removeAllViews()

        for (number in numbers) {
            val button = Button(this)
            button.text = number.toString()
            button.textSize = 20f
            button.setTextAppearance(this, R.style.ButtonStyle)
            button.background = ContextCompat.getDrawable(this, R.drawable.button_selector)
            button.setOnClickListener {
                onNumberButtonClick(button)
            }

            // Set static button size
            val params = GridLayout.LayoutParams().apply {
                width = buttonSize
                height = buttonSize
                rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8, 8, 8, 8)
            }
            button.layoutParams = params
            binding.buttonsGridLayout.addView(button)
        }
    }

    protected open fun startGame() {
        if (!isGameRunning) {
            isGameRunning = true
            binding.startButton.visibility = View.GONE
            timer = object : CountDownTimer(timeRemaining, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeRemaining = millisUntilFinished
                    binding.timerTextView.text = "Time: ${millisUntilFinished / 1000}"
                }

                override fun onFinish() {
                    endGame()
                }
            }.start()
        }
    }

    protected open fun onNumberButtonClick(button: Button) {
        if (!isGameRunning) return
        val buttonNumber = button.text.toString().toInt()
        if (buttonNumber == currentNumber) {
            handleCorrectAnswer(button)
        } else {
            handleIncorrectAnswer(button)
        }
    }

    protected open fun handleCorrectAnswer(button: Button) {
        score++
        binding.scoreTextView.text = "Score: $score"
        button.visibility = View.INVISIBLE
        currentNumber++
        timer?.cancel()

        timeRemaining += timeIncrement
        timer = object : CountDownTimer(timeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                binding.timerTextView.text = "Time: ${millisUntilFinished / 1000}"
            }

            override fun onFinish() {
                endGame()
            }
        }.start()

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTapTime < 500) {
            comboMultiplier++
            score += comboMultiplier
            binding.scoreTextView.text = "Score: $score (x$comboMultiplier)"
        } else {
            comboMultiplier = 1
        }
        lastTapTime = currentTime

        if (currentNumber > maxNumber) {
            endGame()
        } else {
            shuffleButtonNumbers()
        }
    }

    protected open fun handleIncorrectAnswer(button: Button) {
        timer?.cancel()
        timeRemaining -= timePenalty
        timer = object : CountDownTimer(timeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                binding.timerTextView.text = "Time: ${millisUntilFinished / 1000}"
            }

            override fun onFinish() {
                endGame()
            }
        }.start()

        comboMultiplier = 1

        // Change the button color to red briefly
        button.background =
            ContextCompat.getDrawable(this, R.drawable.round_button_pressed)
        Handler(Looper.getMainLooper()).postDelayed({
            button.background = ContextCompat.getDrawable(this, R.drawable.button_selector)
        }, 250)
    }

    // UPDATED: endGame() logic
    protected open fun endGame() {
        isGameRunning = false
        timer?.cancel()

        // Log the current score and high score for debugging
        Log.d("BaseLevelActivity", "Current Score: $score")
        Log.d(
            "BaseLevelActivity",
            "High Score ($level): ${Achievements.getHighScore(this, level)}"
        )

        // Update high score if necessary
        val currentHighScore = Achievements.getHighScore(this, level)
        if (score > currentHighScore) {
            Achievements.setHighScore(this, level, score)
            Log.d("BaseLevelActivity", "New high score set for $level: $score")
        }

        // Unlock level achievement
        Achievements.unlockAchievement(this, getLevelAchievementKey(level))

        // Check if the game was won or lost
        if (currentNumber > maxNumber) {
            // Game won
            showLevelCompleteDialog()
        } else {
            // Game lost
            showGameOverDialog()
        }
    }

    // ADDED: Helper function to get achievement key based on level
    private fun getLevelAchievementKey(level: String): String {
        return when (level) {
            Achievements.LEVEL_EASY -> Achievements.KEY_REACHED_LEVEL_EASY
            Achievements.LEVEL_MEDIUM -> Achievements.KEY_REACHED_LEVEL_MEDIUM
            Achievements.LEVEL_HARD -> Achievements.KEY_REACHED_LEVEL_HARD
            else -> throw IllegalArgumentException("Invalid level: $level")
        }
    }

    protected open fun shuffleButtonNumbers() {
        val visibleButtons = mutableListOf<Button>()
        for (i in 0 until binding.buttonsGridLayout.childCount) {
            val child = binding.buttonsGridLayout.getChildAt(i)
            if (child is Button && child.visibility == View.VISIBLE) {
                visibleButtons.add(child)
            }
        }
        val numbers = visibleButtons.map { it.text.toString().toInt() }.shuffled()
        for (i in 0 until visibleButtons.size) {
            visibleButtons[i].text = numbers[i].toString()
        }
    }

    private fun showLevelCompleteDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_level_complete, null)
        val congratsTextView = dialogView.findViewById<TextView>(R.id.congratsTextView)
        val scoreTextView = dialogView.findViewById<TextView>(R.id.scoreTextView)
        val returnButton = dialogView.findViewById<Button>(R.id.returnButton)
        val nextLevelButton = dialogView.findViewById<Button>(R.id.nextLevelButton) // Get the Next Level button
        val confettiView = dialogView.findViewById<KonfettiView>(R.id.confettiView)

        scoreTextView.text = "Your Score: $score"
        confettiView?.start(getConfetti())

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Set the click listener for the Return to Main Menu button
        returnButton.setOnClickListener {
            dialog.dismiss()
            returnToMainMenu()
        }

        // Check if there's a next level and show/hide the Next Level button accordingly
        if (hasNextLevel()) {
            nextLevelButton.visibility = View.VISIBLE
            nextLevelButton.setOnClickListener {
                dialog.dismiss()
                goToNextLevel()
            }
        } else {
            nextLevelButton.visibility = View.GONE
        }

        dialog.show()
    }

    // Helper function to check if there's a next level
    private fun hasNextLevel(): Boolean {
        return when (level) {
            Achievements.LEVEL_EASY -> true
            Achievements.LEVEL_MEDIUM -> true
            Achievements.LEVEL_HARD -> false
            else -> false // Default to no next level
        }
    }

    // Go to next level
    private fun goToNextLevel() {
        when (level) {
            Achievements.LEVEL_EASY -> {
                val intent = Intent(this, MediumLevelActivity::class.java)
                startActivity(intent)
                finish()
            }
            Achievements.LEVEL_MEDIUM -> {
                val intent = Intent(this, HardLevelActivity::class.java)
                startActivity(intent)
                finish()
            }
            Achievements.LEVEL_HARD -> {
                // All levels completed, show a congratulatory message or return to the main menu
                showCongratulationsDialog()
            }
            else -> returnToMainMenu() // If the level is not recognized, return to the main menu
        }
    }
    // Show Congratulation Dialog
    private fun showCongratulationsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Congratulations!")
            .setMessage("You have completed all levels!")
            .setPositiveButton("Return to Main Menu") { _, _ ->
                returnToMainMenu()
            }
            .setCancelable(false)
            .show()
    }

    private fun showGameOverDialog() {
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.dialog_game_over, null)
        val scoreTextView = dialogView.findViewById<TextView>(R.id.scoreTextView)
        val returnButton = dialogView.findViewById<Button>(R.id.returnButton)

        scoreTextView.text = "Your Score: $score"

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        returnButton.setOnClickListener {
            dialog.dismiss()
            returnToMainMenu()
        }

        dialog.show()
    }

    // ADDED: Create Confetti
    fun getConfetti(): Party {
        val confettiColors = intArrayOf(
            ContextCompat.getColor(this, R.color.yellow),
            ContextCompat.getColor(this, R.color.green),
            ContextCompat.getColor(this, R.color.blue),
            ContextCompat.getColor(this, R.color.orange),
            ContextCompat.getColor(this, R.color.pink)
        )

        val emitterConfig = Emitter(100L, TimeUnit.MILLISECONDS).max(100)
        return PartyFactory(emitterConfig)
            .spread(360)
            .shapes(Shape.Square, Shape.Circle)
            .colors(confettiColors.toList())
            .setSpeedBetween(0f, 30f)
            .position(Position.Relative(0.5, 0.3))
            .build()
    }

    // ADDED: Restart game
    private fun restartGame() {
        currentNumber = 1
        score = 0
        comboMultiplier = 1
        timeRemaining = when (level) {
            Achievements.LEVEL_EASY -> 30000L
            Achievements.LEVEL_MEDIUM -> 25000L
            Achievements.LEVEL_HARD -> 12000L
            else -> 45000L // Default time
        }
        setupGame()
        startGame()
    }

    private fun returnToMainMenu() {
        val intent = Intent(this, LevelSelectActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}