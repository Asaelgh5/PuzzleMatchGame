package com.example.puzzlematchgame.game

import android.util.Log
import androidx.lifecycle.MutableLiveData

class PuzzleGame(puzzleSize: Int) {

    val selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    val cellsLiveData = MutableLiveData<List<Cell>>()

    private val size = puzzleSize
    private var selectedRow: Int
    private var selectedCol: Int
    private var swapFromCell: Cell? = null
    private var swapToCell: Cell? = null

    private val board: Board

    init {
        val cells = arrayListOf<Cell>()

        val types = arrayListOf("green", "red", "orange", "blue", "yellow")
        for (row in 0 until size) {
            for (col in 0 until size) {
                val selectedType = types.shuffled().first()

                val newCell = Cell(row, col, Shape(selectedType))
                cells.add(newCell)
            }
        }

        board = Board(size, cells)

        selectedRow = -1
        selectedCol = -1

        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
    }

    private fun updateSelectCell(row: Int, col: Int) {
        selectedRow = row
        selectedCol = col
        selectedCellLiveData.postValue(Pair(row, col))
    }

    fun finishSwapping() {
        swapFromCell!!.animating = false
        swapToCell!!.animating = false

        swapFromCell = null
        swapToCell = null
        Log.d("finish swap", "finishing......")
    }

    fun handleSwapEvent(fromCoords: Pair<Int, Int>, toCoords: Pair<Int, Int>) {
        Log.d("swap event", "from: $fromCoords")
        Log.d("swap event", "to: $toCoords")
        val fromCell = board.getCell(fromCoords.first, fromCoords.second)
        val toCell = board.getCell(toCoords.first, toCoords.second)

        if (fromCell.animating || toCell.animating) return
        fromCell.animating = true
        toCell.animating = true

        val auxShape = fromCell.shape
        fromCell.shape = toCell.shape
        toCell.shape = auxShape
        swapFromCell = fromCell
        swapToCell = toCell

        updateSelectCell(-1, -1)
    }

    fun handleTouchEvent(coords: Pair<Float, Float>, cellSizePixels: Float) {
        /*var row = -1
        var col = -1*/

        val row = (coords.first / cellSizePixels).toInt()
        val col = (coords.second / cellSizePixels).toInt()

        updateSelectCell(row, col)
    }
}