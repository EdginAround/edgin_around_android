package com.edgin.around.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread

import com.edgin.around.rendering.Scene
import com.edgin.around.rendering.WorldExpositor
import com.edgin.around.game.Connector
import com.edgin.around.game.Gui
import com.edgin.around.game.Thruster

const val RESOURCE_DIR: String = "/sdcard/edgin_around/resources/"
const val SPRITE_DIR: String = "/sdcard/edgin_around/resources/sprites/"
const val DEFAULT_WIDTH = 800
const val DEFAULT_HEIGHT = 800

class GameActivity : AppCompatActivity() {
    private lateinit var gameView: GameView
    private lateinit var connector: Connector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        gameView = findViewById(R.id.game_view) as GameView

        runOnUiThread {
            val scene = Scene()
            val world = WorldExpositor(RESOURCE_DIR, DEFAULT_WIDTH, DEFAULT_HEIGHT)
            val gui = Gui()
            val thruster = Thruster(scene, world, gui)
            connector = Connector(thruster)

            gameView.initialize(GameRenderer(world, scene, thruster))

            thread { connector.run() }
        }
    }

    override protected fun onResume() {
        super.onResume()
        gameView.onResume()
    }

    override protected fun onPause() {
        super.onPause()
        gameView.onPause()
    }

    override protected fun onDestroy() {
        super.onDestroy()
        connector.stop()
    }
}

