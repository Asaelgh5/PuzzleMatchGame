package com.example.puzzlematchgame.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.puzzlematchgame.R
import com.example.puzzlematchgame.game.Cell
import com.example.puzzlematchgame.view.custom.BoardView
import com.example.puzzlematchgame.viewmodel.PlayPuzzleViewModel
import com.example.puzzlematchgame.viewmodel.PlayPuzzleViewModelFactory
import kotlinx.android.synthetic.main.activity_play_game.*

class PlayGameActivity : AppCompatActivity(), BoardView.OnActionListener {

    private lateinit var viewModel: PlayPuzzleViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_game)

        val size = 5

        puzzleBoardView.setSize(size)
        puzzleBoardView.registerListener(this)

        val factory = PlayPuzzleViewModelFactory(size)
        viewModel = ViewModelProviders.of(this, factory).get(PlayPuzzleViewModel::class.java)
        viewModel.game.selectedCellLiveData.observe(this, Observer { updateSelectedCellUI(it) })
        viewModel.game.cellsLiveData.observe(this, Observer { updateCells(it) })
    }

    private fun updateCells(cells: List<Cell>?) = cells?.let {
        puzzleBoardView.updateCells(cells)
    }

    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let {
        puzzleBoardView.updateSelectedCellUI(cell.first, cell.second)
    }

    override fun onTouchEvent(coords: Pair<Float, Float>, cellSizePixels: Float) {
        viewModel.game.handleTouchEvent(coords, cellSizePixels)
    }

    override fun onSwapEvent(fromCoords: Pair<Int, Int>, toCoords: Pair<Int, Int>) {
        viewModel.game.handleSwapEvent(fromCoords, toCoords)
    }

    override fun onFinishSwapping() {
        viewModel.game.finishSwapping()
    }

}
