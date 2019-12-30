package com.example.puzzlematchgame.game

class Cell(
    val row: Int,
    val col: Int,
    var shape: Shape?,
    var animating: Boolean = false,
    var canMove: Boolean = true
)