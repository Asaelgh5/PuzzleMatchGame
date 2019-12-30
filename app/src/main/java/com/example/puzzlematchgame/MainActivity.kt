package com.example.puzzlematchgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.puzzlematchgame.view.PlayGameActivity

class MainActivity : AppCompatActivity() {

    private lateinit var playButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playButton = findViewById(R.id.playButton)
        playButton.setOnClickListener {
            val intent = Intent(this@MainActivity, PlayGameActivity::class.java)
            startActivity(intent)
        }
    }
}
