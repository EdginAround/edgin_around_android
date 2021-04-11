package com.edgin.around.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {
    private lateinit var spriteView: GameView
    private lateinit var buttonPlay: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        buttonPlay = findViewById(R.id.button_play) as Button
        buttonPlay.setOnClickListener { gotoGame() }

        val spriteDir = getFilesDir().getAbsolutePath() + "/" + RESOURCE_DIR + "/" + SPRITE_DIR
        spriteView = findViewById(R.id.sprite_view) as GameView
        spriteView.initialize(SpriteRenderer(spriteDir.toString()))
    }

    private fun gotoGame() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }
}
