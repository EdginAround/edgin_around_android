package com.edgin.around

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {
    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        gameView = findViewById(R.id.game_view) as GameView
    }

    override protected fun onResume() {
        super.onResume()
        gameView.onResume()
    }

    override protected fun onPause() {
        super.onPause();
        gameView.onPause();
    }
}

