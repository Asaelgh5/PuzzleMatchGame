package com.example.puzzlematchgame.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.puzzlematchgame.R
import com.example.puzzlematchgame.game.Cell
import com.example.puzzlematchgame.game.Shape
import kotlin.math.min

class BoardView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var size = 5
    private var cellSizePixels = 0f

    private var selectedRow = -1
    private var selectedCol = -1
    private var swapping = false
    private var swapFromShape: Shape? = null
    private var swapFromX = 0f
    private var swapFromY = 0f
    private var swapToShape: Shape? = null
    private var swapToX = 0f
    private var swapToY = 0f
    private var swapDirection = ""
    private var finishX = 0f
    private var finishY = 0f
//    private var canSwap = true

    private var listener: OnActionListener? = null
    private var cells: List<Cell>? = null

    private val squarePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 2f
    }

    private val squareSelectedPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = resources.getColor(R.color.selectedCell, null)
        strokeWidth = 6f
    }

    private val greenShapePaint = Paint().apply {
        style = Paint.Style.FILL
        color = resources.getColor(R.color.greenShape, null)
    }
    
    private val redShapePaint = Paint().apply {
        style = Paint.Style.FILL
        color = resources.getColor(R.color.redShape, null)
    }

    private val orangeShapePaint = Paint().apply {
        style = Paint.Style.FILL
        color = resources.getColor(R.color.orangeShape, null)
    }

    private val blueShapePaint = Paint().apply {
        style = Paint.Style.FILL
        color = resources.getColor(R.color.blueShape, null)
    }

    private val yellowShapePaint = Paint().apply {
        style = Paint.Style.FILL
        color = resources.getColor(R.color.yellowShape, null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val sizePixels = min(widthMeasureSpec, heightMeasureSpec)
//        val marginSelf = sizePixels
        setMeasuredDimension(sizePixels, sizePixels)
    }

    override fun onDraw(canvas: Canvas) {
        updateMeasurements()

        drawCells(canvas)
        drawSquares(canvas)
        if (swapping)
            animateSwap(canvas)
    }

    private fun updateMeasurements() {
        cellSizePixels = width.toFloat() / size
        Log.d("init", "Cell size pixels: $cellSizePixels")
    }

    private fun drawCells(canvas: Canvas) {
        cells?.forEach { cell ->
            if (!cell.animating) {
                val x = cell.col * cellSizePixels + cellSizePixels / 2f
                val y = cell.row * cellSizePixels + cellSizePixels / 2f

                val paintToUse = selectShape(cell.shape!!.type)
                canvas.drawCircle(x, y, cellSizePixels * 0.3f, paintToUse)
            }
        }
    }

    private fun drawSquares(canvas: Canvas) {
        cells?.forEach { cell ->
            val row = cell.row
            val col = cell.col

            val squarePaintToUse = if(selectedRow == row && selectedCol == col) squareSelectedPaint else squarePaint

            canvas.drawRect(col * cellSizePixels, row * cellSizePixels,
                           (col + 1) * cellSizePixels, (row + 1) * cellSizePixels, squarePaintToUse)
        }
    }
    
    private fun animateSwap(canvas: Canvas) {
        when (swapDirection) {
            "left" -> {
                swapFromX -= 1
                swapToX += 1
            }
            "right" -> {
                swapFromX += 1
                swapToX -= 1
            }
            "up" -> {
                swapFromY -= 1
                swapToY += 1
            }
            "down" -> {
                swapFromY += 1
                swapToY -= 1
            }
        }
        val swapToShapePaint = selectShape(swapToShape!!.type)
        canvas.drawCircle(swapToX, swapToY, cellSizePixels * 0.18f, swapToShapePaint)
        val swapFromShapePaint = selectShape(swapFromShape!!.type)
        canvas.drawCircle(swapFromX, swapFromY, cellSizePixels * 0.3f, swapFromShapePaint)
        invalidate()

        if ((swapDirection == "left" && swapFromX <= finishX) ||
            (swapDirection == "right" && swapFromX >= finishX) ||
            (swapDirection == "up" && swapFromY <= finishY) ||
            (swapDirection == "down" && swapFromY >= finishY)) {

            swapping = false
            swapFromShape = null
            swapToShape = null
            listener?.onFinishSwapping()
        }
    }

    private fun selectShape(type: String) = when(type) {
        "green" -> greenShapePaint
        "red" -> redShapePaint
        "orange" -> orangeShapePaint
        "blue" -> blueShapePaint
        "yellow" -> yellowShapePaint
        else -> Paint()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                listener?.onTouchEvent(Pair(event.y, event.x), cellSizePixels)
                true
            }
            MotionEvent.ACTION_MOVE -> {
                if (selectedRow == -1 || selectedCol == -1 || swapping || event.x < 0 || event.y < 0) 
                    return false
                
                val toRow = (event.y / cellSizePixels).toInt()
                val toCol = (event.x / cellSizePixels).toInt()
                if (toRow != selectedRow || toCol != selectedCol) {
                    swapping = true
                    
                    swapDirection = when {
                        selectedRow > toRow -> "down"
                        selectedRow < toRow -> "up"
                        selectedCol > toCol -> "right"
                        selectedCol < toCol -> "left"
                        else -> ""
                    }
                    val offset = cellSizePixels / 2f

                    swapFromShape = cells!![selectedRow * size + selectedCol].shape
                    swapFromX = selectedCol * cellSizePixels + offset
                    swapFromY = selectedRow * cellSizePixels + offset

                    swapToShape = cells!![toRow * size + toCol].shape
                    swapToX = toCol * cellSizePixels + offset
                    swapToY = toRow * cellSizePixels + offset
                    finishX = swapToX
                    finishY = swapToY
                    listener?.onSwapEvent(Pair(selectedRow, selectedCol), Pair(toRow, toCol))
                    Log.d("MOVE", "we can move the selected shape to $toRow, $toCol")
                }
                true
            }
            MotionEvent.ACTION_UP -> {
                Log.d("touch event", "Action Up")
                true
            }
            else -> false
        }
    }

    fun updateSelectedCellUI(row: Int, col: Int) {
        selectedRow = row
        selectedCol = col
        Log.d("Selected cell", "row: $row, col: $col")

        invalidate()
    }

    fun updateCells(cells: List<Cell>) {
        this.cells = cells
        invalidate()
    }

    fun registerListener(listener: OnActionListener) {
        this.listener = listener
    }

    fun setSize(newSize: Int) {
        size = newSize
    }

    interface OnActionListener {
        fun onTouchEvent(coords: Pair<Float, Float>, cellSizePixels: Float)
        fun onSwapEvent(fromCoords: Pair<Int, Int>, toCoords: Pair<Int, Int>)
        fun onFinishSwapping()
    }
}