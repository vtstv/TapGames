package com.murr.taptheumber.levels

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.murr.taptheumber.Achievements
import com.murr.taptheumber.R
import com.murr.taptheumber.databinding.ActivityKnightTourBinding
import com.murr.taptheumber.BaseActivity

class KnightTourActivity : BaseActivity() {
    private lateinit var binding: ActivityKnightTourBinding
    private lateinit var gridLayout: GridLayout
    private lateinit var newGameButton: Button
    private lateinit var toggleHintsButton: Button
    private lateinit var moveNumberTextView: TextView
    private lateinit var fontSizeSpinner: android.widget.Spinner
    private lateinit var fontColorSpinner: android.widget.Spinner
    private lateinit var helpButton: Button
    private lateinit var undoButton: ImageView  // Added undo button
    private lateinit var closeButton: ImageView // Added close button

    private var totalKnightMoves = 0
    private val boardSize = 10
    private var moveNumber = 0
    private var totalMoves = 0
    private var isHintsEnabled = false
    private val cells = Array(boardSize * boardSize) { "" }
    private val possibleMovesX = intArrayOf(1, 2, 2, 1, -1, -2, -2, -1)
    private val possibleMovesY = intArrayOf(2, 1, -1, -2, -2, -1, 1, 2)
    private var currentCell = -1
    private var selectedFontSize = 20f
    private var selectedFontColor = Color.BLUE
    private var previousCell = -1 // Added previous cell for undo
    private var canUndo = false // Flag to check if undo is possible


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
        helpButton = binding.helpButton
        undoButton = binding.undoButton // Initialize undo button
        closeButton = binding.closeButton // Initialize close button
        gridLayout.rowCount = boardSize
        gridLayout.columnCount = boardSize
        setupSpinners()
        initializeBoard()
        setupButtons()
        Achievements.initializeKnightTourAchievements(this)
        // Set onClickListener for undo button
        undoButton.setOnClickListener { undoLastMove() }
        closeButton.setOnClickListener {
            returnToMainMenu()
        }
    }

    private fun initializeBoard() {
        gridLayout.removeAllViews()
        for (i in 0 until boardSize * boardSize) {
            // Create a FrameLayout to hold the button and ImageView
            val frameLayout = FrameLayout(this)
            frameLayout.layoutParams = GridLayout.LayoutParams().apply {
                rowSpec = GridLayout.spec(i / boardSize, 1f)
                columnSpec = GridLayout.spec(i % boardSize, 1f)
                width = 0
                height = 0
                setMargins(4, 4, 4, 4)
            }
            totalKnightMoves = 0
            // Create the button
            val button = Button(this)
            button.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            button.setPadding(8, 8, 8, 8)
            // Set cell color
            if ((i / boardSize + i % boardSize) % 2 == 0) {
                button.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            } else {
                button.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray))
            }
            button.setTextColor(selectedFontColor)
            button.textSize = selectedFontSize
            button.setOnClickListener { onCellClicked(i) }

            // Create the ImageView
            val imageView = ImageView(this)
            imageView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
            imageView.tag = "knight_icon" // Set a tag to identify the ImageView

            // Add the button and ImageView to the FrameLayout
            frameLayout.addView(button)
            frameLayout.addView(imageView)
            imageView.bringToFront()
            imageView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            ).apply {
                val padding = 5
                setMargins(padding, padding, padding, padding)
            }
            imageView.tag = "knight_icon"
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            // Add the FrameLayout to the GridLayout
            gridLayout.addView(frameLayout)
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
        helpButton.setOnClickListener {
            showHelpDialog()
        }
    }

    private fun startNewGame() {
        moveNumber = 0
        totalMoves = 0
        currentCell = -1
        previousCell = -1
        totalKnightMoves = 0
        canUndo = false
        moveNumberTextView.text = getString(R.string.move_number, 0)
        for (i in 0 until boardSize * boardSize) {
            cells[i] = ""
            val frameLayout = gridLayout.getChildAt(i) as FrameLayout
            val button = frameLayout.getChildAt(0) as Button
            val imageView = frameLayout.findViewWithTag<ImageView>("knight_icon")
            button.text = ""
            button.isEnabled = true
            if ((i / boardSize + i % boardSize) % 2 == 0) {
                button.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            } else {
                button.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray))
            }
            button.setTextColor(selectedFontColor)
            button.textSize = selectedFontSize
            // Clear the image
            imageView.setImageDrawable(null)

            moveNumberTextView.text = getString(R.string.move_number, 0)
        }
    }

    private fun showHelpDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.help_title))
            .setMessage(getString(R.string.help_message))
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun onCellClicked(cellIndex: Int) {
        if (moveNumber == 0) {
            moveNumber = 1
            totalMoves = 1
            cells[cellIndex] = moveNumber.toString()
            val frameLayout = gridLayout.getChildAt(cellIndex) as FrameLayout
            val button = frameLayout.getChildAt(0) as Button
            val imageView = frameLayout.findViewWithTag<ImageView>("knight_icon")
            button.text = ""
            button.setBackgroundColor(Color.TRANSPARENT)
            val knightIcon = ContextCompat.getDrawable(this, R.drawable.knight_icon)
            imageView.setImageDrawable(knightIcon)
            currentCell = cellIndex
            canUndo = false // Disable undo since it's the first move
            if (!hasValidMoves(currentCell / boardSize, currentCell % boardSize)) {
                showGameOverDialog()
            } else {
                updateHints()
            }
        } else {
            val row = cellIndex / boardSize
            val col = cellIndex % boardSize
            val prevRow = currentCell / boardSize
            val prevCol = currentCell % boardSize

            if (isPossibleMove(prevRow, prevCol, row, col) && cells[cellIndex] == "") {
                // Valid move
                previousCell = currentCell
                moveNumber++
                totalMoves++
                totalKnightMoves++


                val prevFrameLayout = gridLayout.getChildAt(currentCell) as FrameLayout
                val prevButton = prevFrameLayout.getChildAt(0) as Button
                val prevImageView = prevFrameLayout.findViewWithTag<ImageView>("knight_icon")
                prevButton.text = cells[currentCell]
                if ((currentCell / boardSize + currentCell % boardSize) % 2 == 0) {
                    prevButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                } else {
                    prevButton.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray))
                }
                prevImageView.setImageDrawable(null)



                cells[cellIndex] = moveNumber.toString()
                val frameLayout = gridLayout.getChildAt(cellIndex) as FrameLayout
                val button = frameLayout.getChildAt(0) as Button
                val imageView = frameLayout.findViewWithTag<ImageView>("knight_icon")
                button.text = ""
                button.setBackgroundColor(Color.TRANSPARENT)
                val knightIcon = ContextCompat.getDrawable(this, R.drawable.knight_icon)
                imageView.setImageDrawable(knightIcon)
                currentCell = cellIndex
                canUndo = true // Enable undo after valid move
                if (moveNumber == boardSize * boardSize) {
                    AlertDialog.Builder(this)
                        .setMessage(getString(R.string.board_completed))
                        .setPositiveButton(getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
                        .show()
                }

                if (!hasValidMoves(currentCell / boardSize, currentCell % boardSize)) {
                    showGameOverDialog()
                } else {
                    updateHints()
                }
            }
        }
        moveNumberTextView.text = getString(R.string.move_number, moveNumber)
        // Check for Knight Tour achievements
        when (totalKnightMoves) {
            10 -> Achievements.unlockAchievement(this, Achievements.KEY_KNIGHT_TOUR_10_MOVES)
            50 -> Achievements.unlockAchievement(this, Achievements.KEY_KNIGHT_TOUR_50_MOVES)
            100 -> Achievements.unlockAchievement(this, Achievements.KEY_KNIGHT_TOUR_100_MOVES)
        }
    }

    private fun undoLastMove() {
        if (canUndo && moveNumber > 1) {
            // Get the previous cell index from the cells array
            val prevCellIndex = previousCell;
            if(prevCellIndex != -1) {
                val currentFrameLayout = gridLayout.getChildAt(currentCell) as FrameLayout
                val currentButton = currentFrameLayout.getChildAt(0) as Button
                val currentImageView = currentFrameLayout.findViewWithTag<ImageView>("knight_icon")
                currentButton.text = moveNumber.toString()
                if ((currentCell / boardSize + currentCell % boardSize) % 2 == 0) {
                    currentButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                } else {
                    currentButton.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray))
                }
                currentImageView.setImageDrawable(null)


                val prevFrameLayout = gridLayout.getChildAt(prevCellIndex) as FrameLayout
                val prevButton = prevFrameLayout.getChildAt(0) as Button
                val prevImageView = prevFrameLayout.findViewWithTag<ImageView>("knight_icon")
                prevButton.text = ""
                prevButton.setBackgroundColor(Color.TRANSPARENT)
                val knightIcon = ContextCompat.getDrawable(this, R.drawable.knight_icon)
                prevImageView.setImageDrawable(knightIcon)

                cells[currentCell] = ""
                moveNumber--
                currentCell = prevCellIndex
                previousCell = -1
                totalMoves--
                totalKnightMoves--

                canUndo = false
                moveNumberTextView.text = getString(R.string.move_number, moveNumber)
                updateHints()
            }
        }
    }



    private fun isPossibleMove(prevRow: Int, prevCol: Int, row: Int, col: Int): Boolean {
        for (i in 0 until 8) {
            if (prevRow + possibleMovesY[i] == row && prevCol + possibleMovesX[i] == col) {
                return true
            }
        }
        return false
    }

    private fun hasValidMoves(row: Int, col: Int): Boolean {
        for (i in 0 until 8) {
            val nextRow = row + possibleMovesY[i]
            val nextCol = col + possibleMovesX[i]
            if (nextRow in 0 until boardSize && nextCol in 0 until boardSize && cells[nextRow * boardSize + nextCol] == "") {
                return true
            }
        }
        return false
    }

    private fun showGameOverDialog() {
        AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage("No more moves available!\nTotal moves: $totalKnightMoves")
            .setPositiveButton("New Game") { dialog, _ ->
                dialog.dismiss()
                startNewGame()
            }
            .setNegativeButton("Main Menu") { dialog, _ ->
                dialog.dismiss()
                returnToMainMenu()
            }
            .setCancelable(false)
            .show()
    }

    private fun returnToMainMenu() {
        val intent = Intent(this, LevelSelectActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
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
            val frameLayout = gridLayout.getChildAt(i) as FrameLayout
            val button = frameLayout.getChildAt(0) as Button
            val imageView = frameLayout.findViewWithTag<ImageView>("knight_icon")
            if (cells[i] == "") {
                button.text = ""
                imageView.setImageDrawable(null) // Remove any existing icon
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
                val frameLayout = gridLayout.getChildAt(move) as FrameLayout
                val button = frameLayout.getChildAt(0) as Button
                if (cells[move] == "") {
                    val hintText = when {
                        movesCount == 0 && moveNumber == boardSize * boardSize - 1 -> "++"
                        movesCount == 0 -> "--"
                        movesCount <= minMoves -> "+"
                        else -> ""
                    }
                    button.setTextColor(selectedFontColor)
                    button.text = hintText
                    // Set cell background color
                    if ((move / boardSize + move % boardSize) % 2 == 0) {
                        button.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                    } else {
                        button.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray))
                    }
                }
            }
        }
    }

    private fun clearHints() {
        for (i in 0 until boardSize * boardSize) {
            val frameLayout = gridLayout.getChildAt(i) as FrameLayout
            val button = frameLayout.getChildAt(0) as Button
            val imageView = frameLayout.findViewWithTag<ImageView>("knight_icon")
            if (cells[i] == "") {
                button.text = ""
                imageView.setImageDrawable(null) // Clear the icon
                // Restore the cell color
                if ((i / boardSize + i % boardSize) % 2 == 0) {
                    button.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                } else {
                    button.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray))
                }
            }
        }
    }


    private fun setupSpinners() {
        val fontSizeAdapter = android.widget.ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            (18..28 step 2).map { "$it" })
        fontSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fontSizeSpinner.adapter = fontSizeAdapter
        fontSizeSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                (view as? TextView)?.setTextColor(resources.getColor(R.color.white, theme))
                selectedFontSize = fontSizeAdapter.getItem(position)!!.toFloat()
                updateButtonFont()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }
        val fontColorAdapter = android.widget.ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOf("Blue", "Red", "Green", "Black", "Yellow")
        )
        fontColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fontColorSpinner.adapter = fontColorAdapter
        fontColorSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                (view as? TextView)?.setTextColor(resources.getColor(R.color.white, theme))
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

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }
        fontSizeSpinner.setSelection(2) // Assuming "20" is the 3rd item
        fontColorSpinner.setSelection(0) // Assuming "Blue" is the 1st item
    }

    private fun updateButtonFont() {
        for (i in 0 until gridLayout.childCount) {
            val frameLayout = gridLayout.getChildAt(i) as FrameLayout
            val button = frameLayout.getChildAt(0) as Button
            button.textSize = selectedFontSize
            button.setTextColor(selectedFontColor)
        }
    }
}