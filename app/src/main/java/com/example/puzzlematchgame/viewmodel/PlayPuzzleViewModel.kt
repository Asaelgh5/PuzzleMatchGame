package com.example.puzzlematchgame.viewmodel

import androidx.lifecycle.ViewModel
import com.example.puzzlematchgame.game.PuzzleGame

class PlayPuzzleViewModel(puzzleSize: Int) : ViewModel() {

    val game = PuzzleGame(puzzleSize)
}