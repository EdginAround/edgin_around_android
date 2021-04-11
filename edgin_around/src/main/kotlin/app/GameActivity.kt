package com.edgin.around.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.edgin.around.game.Connector
import com.edgin.around.game.Gui
import com.edgin.around.game.Thruster
import com.edgin.around.rendering.Scene
import com.edgin.around.rendering.WorldExpositor
import java.io.File
import kotlin.concurrent.thread

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
            val resourcePath = File(getFilesDir(), RESOURCE_DIR).getCanonicalPath()
            val scene = Scene()
            val world = WorldExpositor(resourcePath, DEFAULT_WIDTH, DEFAULT_HEIGHT)
            val gui = Gui()
            val thruster = Thruster(scene, world, gui)
            connector = Connector(thruster)

            gameView.initialize(GameRenderer(world, scene, thruster))

            thread { connector.run() }
        }
    }

    protected override fun onResume() {
        super.onResume()
        gameView.onResume()
    }

    protected override fun onPause() {
        super.onPause()
        gameView.onPause()
    }

    protected override fun onDestroy() {
        super.onDestroy()
        connector.stop()
    }
}
