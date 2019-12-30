package com.example.puzzlematchgame.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PlayPuzzleViewModelFactory(private val puzzleSize: Int) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlayPuzzleViewModel(puzzleSize) as T
    }
}