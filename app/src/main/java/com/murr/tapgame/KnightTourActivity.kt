package com.murr.taptheumber.levels

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.murr.taptheumber.R
import com.murr.taptheumber.databinding.ActivityKnightTourBinding

class KnightTourActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKnightTourBinding
    private lateinit var gridLayout: GridLayout
    private lateinit var newGameButton: Button
    private lateinit var toggleHintsButton: Button
    private lateinit var moveNumberTextView: TextView

    private val boardSize = 10
    private var moveNumber = 0
    private var totalMoves = 0
    private var isHintsEnabled = false
    private val cells = Array(boardSize * boardSize) { "" }
    private val possibleMovesX = intArrayOf(1, 2, 2, 1, -1, -2, -2, -1)
    private val possibleMovesY = intArrayOf(2, 1, -1, -2, -2, -1, 1, 2)
    private var currentCell = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKnightTourBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gridLayout = binding.knightTourGrid
        newGameButton = binding.newGameButton
        toggleHintsButton = binding.toggleHintsButton
        moveNumberTextView = binding.moveNumber

        gridLayout.rowCount = boardSize
        gridLayout.columnCount = boardSize

        initializeBoard()
        setupButtons()
    }

    private fun initializeBoard() {
        gridLayout.removeAllViews()
        for (i in 0 until boardSize * boardSize) {
            val button = Button(this)
            button.layoutParams = GridLayout.LayoutParams().apply {
                rowSpec = GridLayout.spec(i / boardSize, 1f)
                columnSpec = GridLayout.spec(i % boardSize, 1f)
                width = 0
                height = 0
                setMargins(2, 2, 2, 2)
            }
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray))
            button.setTextColor(ContextCompat.getColor(this, R.color.accent_blue))
            button.setOnClickListener { onCellClicked(i) }
            gridLayout.addView(button)
        }
    }

    private fun setupButtons() {
        newGameButton.setOnClickListener {
            if (moveNumber > 0) {
                AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to start a new game?")
                    .setPositiveButton("Yes") { dialog: DialogInterface, _: Int ->
                        dialog.dismiss()
                        startNewGame()
                    }
                    .setNegativeButton("No") { dialog: DialogInterface, _: Int ->
                        dialog.dismiss()
                    }
                    .show()
            } else {
                startNewGame()
            }
        }

        toggleHintsButton.setOnClickListener {
            isHintsEnabled = !isHintsEnabled
            toggleHintsButton.text = if (isHintsEnabled) "Disable Hints" else "Enable Hints"
            updateHints()
        }
    }

    private fun startNewGame() {
        moveNumber = 0
        totalMoves = 0
        currentCell = -1
        for (i in 0 until boardSize * boardSize) {
            cells[i] = ""
            val button = gridLayout.getChildAt(i) as Button
            button.text = ""
            button.isEnabled = true
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray))
            button.setTextColor(ContextCompat.getColor(this, R.color.accent_blue))

        }
        moveNumberTextView.text = "Move: 0"

    }

    private fun onCellClicked(cellIndex: Int) {
        if (moveNumber == 0) {
            moveNumber = 1
            totalMoves = 1
            cells[cellIndex] = moveNumber.toString()
            (gridLayout.getChildAt(cellIndex) as Button).text = moveNumber.toString()
            activateCell(cellIndex)
            currentCell = cellIndex
            updateHints()
        } else {
            val row = cellIndex / boardSize
            val col = cellIndex % boardSize
            val prevRow = currentCell / boardSize
            val prevCol = currentCell % boardSize

            if (isPossibleMove(prevRow, prevCol, row, col) && cells[cellIndex] == "") {
                moveNumber++
                totalMoves++
                if (moveNumber == boardSize * boardSize) {
                    AlertDialog.Builder(this)
                        .setMessage("Congratulations! You have completed the tour!")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .show()
                }
                cells[cellIndex] = moveNumber.toString()
                (gridLayout.getChildAt(cellIndex) as Button).text = moveNumber.toString()
                deactivateCell(currentCell)
                activateCell(cellIndex)
                currentCell = cellIndex
                updateHints()
            } else if (cellIndex == currentCell) {
                moveNumber--
                cells[cellIndex] = ""
                (gridLayout.getChildAt(cellIndex) as Button).text = ""
                deactivateCell(cellIndex)
                if (moveNumber > 0) {
                    activateCell(currentCell)
                }
                updateHints()
            }
        }
        moveNumberTextView.text = "Move: $moveNumber"
    }

    private fun isPossibleMove(prevRow: Int, prevCol: Int, row: Int, col: Int): Boolean {
        for (i in 0 until 8) {
            if (prevRow + possibleMovesY[i] == row && prevCol + possibleMovesX[i] == col) {
                return true
            }
        }
        return false
    }

    private fun activateCell(cellIndex: Int) {
        val button = gridLayout.getChildAt(cellIndex) as Button
        button.setBackgroundColor(ContextCompat.getColor(this, R.color.accent_blue))
        button.setTextColor(ContextCompat.getColor(this, R.color.light_gray))
    }

    private fun deactivateCell(cellIndex: Int) {
        val button = gridLayout.getChildAt(cellIndex) as Button
        button.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray))
        button.setTextColor(ContextCompat.getColor(this, R.color.accent_blue))
    }

    private fun updateHints() {
        if (!isHintsEnabled || moveNumber == 0) {
            clearHints()
            return
        }

        val currentRow = currentCell / boardSize
        val currentCol = currentCell % boardSize
        val validMoves = mutableListOf<Int>()

        for (i in 0 until 8) {
            val nextRow = currentRow + possibleMovesY[i]
            val nextCol = currentCol + possibleMovesX[i]
            if (nextRow in 0 until boardSize && nextCol in 0 until boardSize) {
                val nextCellIndex = nextRow * boardSize + nextCol
                if (cells[nextCellIndex] == "") {
                    validMoves.add(nextCellIndex)
                }
            }
        }

        for (i in 0 until boardSize * boardSize) {
            val button = gridLayout.getChildAt(i) as Button
            if (cells[i] == "") {
                button.text = ""
            }
        }
        val warnsdorffMoves = mutableListOf<Pair<Int, Int>>()
        for (move in validMoves) {
            val nextRow = move / boardSize
            val nextCol = move % boardSize
            var nextMovesCount = 0
            for (i in 0 until 8) {
                val futureRow = nextRow + possibleMovesY[i]
                val futureCol = nextCol + possibleMovesX[i]
                if (futureRow in 0 until boardSize && futureCol in 0 until boardSize) {
                    val futureCellIndex = futureRow * boardSize + futureCol
                    if (cells[futureCellIndex] == "") {
                        nextMovesCount++
                    }
                }
            }
            warnsdorffMoves.add(Pair(move, nextMovesCount))
        }
        warnsdorffMoves.sortBy { it.second }

        if (warnsdorffMoves.isNotEmpty()) {
            val minMoves = warnsdorffMoves[0].second
            for ((move, movesCount) in warnsdorffMoves) {
                val button = gridLayout.getChildAt(move) as Button
                if (cells[move] == "") {
                    if (movesCount == 0 && moveNumber == boardSize * boardSize -1) {
                        button.text = "++"
                    } else if (movesCount == 0) {
                        button.text = "--"
                    } else if (movesCount == minMoves) {
                        button.text = "+"
                    } else {
                        button.text = "-"
                    }
                }
            }
        }

    }

    private fun clearHints() {
        for (i in 0 until boardSize * boardSize) {
            val button = gridLayout.getChildAt(i) as Button
            if (cells[i] == "") {
                button.text = ""
            }
        }
    }
}