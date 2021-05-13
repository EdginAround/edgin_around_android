package com.edgin.around.app

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.edgin.around.game.Connector
import com.edgin.around.game.Controls
import com.edgin.around.game.Gui
import com.edgin.around.game.Thruster
import com.edgin.around.rendering.Scene
import com.edgin.around.rendering.WorldExpositor
import com.edgin.around.widgets.ActionRing
import java.io.File
import kotlin.concurrent.thread
import kotlin.math.PI

const val DEFAULT_WIDTH = 800
const val DEFAULT_HEIGHT = 800

class GameActivity : AppCompatActivity() {
    private lateinit var gameView: GameView
    private lateinit var leftActionRing: ActionRing
    private lateinit var rightActionRing: ActionRing
    private var connection: Connector.Connection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        gameView = findViewById(R.id.game_view) as GameView
        leftActionRing = findViewById(R.id.action_ring_left) as ActionRing
        rightActionRing = findViewById(R.id.action_ring_right) as ActionRing

        leftActionRing.configureAxis(0.4f * PI.toFloat(), 0.5f * PI.toFloat())
        rightActionRing.configureAxis(-0.4f * PI.toFloat(), -0.5f * PI.toFloat())
    }

    protected override fun onResume() {
        super.onResume()

        val conn = Connector.getLocalConnection()
        if (conn == null) {
            Log.e(TAG, "Failed to connect to the server")
            Toast.makeText(this, R.string.problem_no_local_server, Toast.LENGTH_LONG).show()
            return
        }

        val resourcePath = File(getFilesDir(), RESOURCE_DIR).getCanonicalPath()
        val scene = Scene()
        val world = WorldExpositor(resourcePath, DEFAULT_WIDTH, DEFAULT_HEIGHT)

        val controls = Controls(conn.proxy, world)
        val gui = Gui(this, controls, leftActionRing, rightActionRing)
        val thruster = Thruster(scene, world, gui)

        conn.receiver.setListener(thruster)

        gameView.initialize(GameRenderer(world, scene, thruster))
        gameView.onResume()

        thread { conn.receiver.run() }
    }

    protected override fun onPause() {
        super.onPause()
        gameView.onPause()
        connection?.receiver?.stop()
        connection = null
    }

    protected override fun onDestroy() {
        super.onDestroy()
    }
}
