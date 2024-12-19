package com.murr.taptheumber.levels

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.GridLayout
import android.widget.Spinner
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
    private lateinit var fontSizeSpinner: Spinner
    private lateinit var fontColorSpinner: Spinner

    private val boardSize = 10
    private var moveNumber = 0
    private var totalMoves = 0
    private var isHintsEnabled = false
    private val cells = Array(boardSize * boardSize) { "" }
    private val possibleMovesX = intArrayOf(1, 2, 2, 1, -1, -2, -2, -1)
    private val possibleMovesY = intArrayOf(2, 1, -1, -2, -2, -1, 1, 2)
    private var currentCell = -1

    private var selectedFontSize = 20f // Default font size
    private var selectedFontColor = Color.BLUE // Default font color

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKnightTourBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gridLayout = binding.knightTourGrid
        newGameButton = binding.newGameButton
        toggleHintsButton = binding.toggleHintsButton
        moveNumberTextView = binding.moveNumber
        fontSizeSpinner = binding.fontSizeSpinner
        fontColorSpinner = binding.fontColorSpinner

        gridLayout.rowCount = boardSize
        gridLayout.columnCount = boardSize

        setupSpinners()
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
                setMargins(4, 4, 4, 4)
            }
            button.setPadding(8, 8, 8, 8)
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray))
            button.setTextColor(selectedFontColor)
            button.textSize = selectedFontSize
            button.setOnClickListener { onCellClicked(i) }
            gridLayout.addView(button)
        }
    }

    private fun setupButtons() {
        newGameButton.setOnClickListener {
            if (moveNumber > 0) {
                AlertDialog.Builder(this)
                    .setMessage(getString(R.string.start_new_game_confirmation))
                    .setPositiveButton(getString(R.string.yes)) { dialog: DialogInterface, _: Int ->
                        dialog.dismiss()
                        startNewGame()
                    }
                    .setNegativeButton(getString(R.string.no)) { dialog: DialogInterface, _: Int ->
                        dialog.dismiss()
                    }
                    .show()
            } else {
                startNewGame()
            }
        }

        toggleHintsButton.setOnClickListener {
            isHintsEnabled = !isHintsEnabled
            toggleHintsButton.text = if (isHintsEnabled) getString(R.string.disable_hints) else getString(R.string.enable_hints)
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
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray))//same color
            button.setTextColor(selectedFontColor)
            button.textSize = selectedFontSize

        }
        moveNumberTextView.text = getString(R.string.move_number, 0)

    }

    private fun onCellClicked(cellIndex: Int) {
        if (moveNumber == 0) {
            moveNumber = 1
            totalMoves = 1
            cells[cellIndex] = moveNumber.toString()
            val button = gridLayout.getChildAt(cellIndex) as Button
            button.text = moveNumber.toString()
            button.setTextColor(selectedFontColor)
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
                        .setMessage(getString(R.string.board_completed))
                        .setPositiveButton(getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
                        .show()
                }
                cells[cellIndex] = moveNumber.toString()
                val button = gridLayout.getChildAt(cellIndex) as Button
                button.text = moveNumber.toString()
                button.setTextColor(selectedFontColor)
                currentCell = cellIndex
                updateHints()
            } else if (cellIndex == currentCell) {
                moveNumber--
                cells[cellIndex] = ""
                (gridLayout.getChildAt(cellIndex) as Button).text = ""
                if (moveNumber > 0) {
                    val button = gridLayout.getChildAt(currentCell) as Button
                    button.setTextColor(selectedFontColor)
                }
                updateHints()
            }
        }
        moveNumberTextView.text = getString(R.string.move_number, moveNumber)
    }

    private fun isPossibleMove(prevRow: Int, prevCol: Int, row: Int, col: Int): Boolean {
        for (i in 0 until 8) {
            if (prevRow + possibleMovesY[i] == row && prevCol + possibleMovesX[i] == col) {
                return true
            }
        }
        return false
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
    private fun setupSpinners() {
        val fontSizeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, (18..28 step 2).map { "$it" })
        fontSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fontSizeSpinner.adapter = fontSizeAdapter

        fontSizeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedFontSize = fontSizeAdapter.getItem(position)!!.toFloat()
                updateButtonFont()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val fontColorAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOf("Blue", "Red", "Green", "Black", "Yellow")
        )
        fontColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fontColorSpinner.adapter = fontColorAdapter

        fontColorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val colorName = fontColorAdapter.getItem(position)!!
                selectedFontColor = when (colorName) {
                    "Red" -> Color.RED
                    "Green" -> Color.GREEN
                    "Black" -> Color.BLACK
                    "Yellow" -> Color.YELLOW
                    else -> Color.BLUE
                }
                updateButtonFont()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        fontSizeSpinner.setSelection(2) // Assuming "20" is the 3rd item
        fontColorSpinner.setSelection(0) // Assuming "Blue" is the 1st item
    }

    private fun updateButtonFont() {
        for (i in 0 until gridLayout.childCount) {
            val button = gridLayout.getChildAt(i) as Button
            button.textSize = selectedFontSize
            button.setTextColor(selectedFontColor)
        }
    }
}