package com.murr.taptheumber.levels

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.murr.taptheumber.R
import com.murr.taptheumber.databinding.ActivityMainBinding

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
            button.setOnClickListener {
                onNumberButtonClick(button)
            }
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.button_background))
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

        shuffleButtonNumbers()

        if (currentNumber > maxNumber) {
            endGame()
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

        button.setBackgroundColor(ContextCompat.getColor(this, R.color.red))

        Handler(Looper.getMainLooper()).postDelayed({
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.button_background))
        }, 250)
    }

    protected open fun endGame() {
        isGameRunning = false
        timer?.cancel()
        binding.startButton.visibility = View.VISIBLE
        binding.startButton.text = "Restart"

        setupGame()
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
}