package com.edgin.around.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.edgin.around.game.Connector
import com.edgin.around.game.Controls
import com.edgin.around.game.Gui
import com.edgin.around.game.Receiver
import com.edgin.around.game.Thruster
import com.edgin.around.rendering.Scene
import com.edgin.around.rendering.WorldExpositor
import com.edgin.around.widgets.ActionRing
import java.io.File
import kotlin.concurrent.thread

const val DEFAULT_WIDTH = 800
const val DEFAULT_HEIGHT = 800

class GameActivity : AppCompatActivity() {
    private lateinit var gameView: GameView
    private lateinit var leftActionRing: ActionRing
    private lateinit var rightActionRing: ActionRing
    private lateinit var receiver: Receiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        gameView = findViewById(R.id.game_view) as GameView
        leftActionRing = findViewById(R.id.action_ring_left) as ActionRing
        rightActionRing = findViewById(R.id.action_ring_right) as ActionRing

        runOnUiThread {
            val resourcePath = File(getFilesDir(), RESOURCE_DIR).getCanonicalPath()
            val scene = Scene()
            val world = WorldExpositor(resourcePath, DEFAULT_WIDTH, DEFAULT_HEIGHT)
            val gui = Gui()
            val thruster = Thruster(scene, world, gui)

            val connection = Connector(thruster).connect()
            if (connection == null) {
                return@runOnUiThread
            }

            receiver = connection.receiver

            val controls = Controls(connection.proxy, world)
            leftActionRing.setWheelListener(controls.MovementActionRingListener())
            rightActionRing.setWheelListener(controls.OrientationActionRingListener())

            gameView.initialize(GameRenderer(world, scene, thruster))

            thread { receiver.run() }
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
        receiver.stop()
    }
}
