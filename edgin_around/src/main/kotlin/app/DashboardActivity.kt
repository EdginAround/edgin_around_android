package com.edgin.around.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.edgin.around.game.Connector
import com.edgin.around.game.Lan
import kotlin.concurrent.thread

class DashboardActivity : AppCompatActivity() {
    private lateinit var spriteView: GameView
    private lateinit var buttonPlay: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        buttonPlay = findViewById(R.id.button_play) as Button
        buttonPlay.setOnClickListener { connectLocal() }

        val spriteDir = getFilesDir().getAbsolutePath() + "/" + RESOURCE_DIR + "/" + SPRITE_DIR
        spriteView = findViewById(R.id.sprite_view) as GameView
        spriteView.initialize(SpriteRenderer(spriteDir.toString()))
    }

    private fun connectLocal() {
        runOnUiThread { buttonPlay.setEnabled(false) }

        thread {
            val servers = Lan().listServers()
            if (servers.size == 0) {
                Log.e(TAG, "No local server found")
                runOnUiThread {
                    Toast.makeText(this, R.string.problem_no_local_server, Toast.LENGTH_LONG).show()
                    buttonPlay.setEnabled(true)
                }
            } else if (Connector.connectLocal(servers[0])) {
                gotoGame()
                runOnUiThread { buttonPlay.setEnabled(true) }
            } else {
                Log.e(TAG, "Failed to connect to a local server: ${servers[0]}")
                runOnUiThread {
                    Toast.makeText(this, R.string.problem_server_connect, Toast.LENGTH_LONG).show()
                    buttonPlay.setEnabled(true)
                }
            }
        }
    }

    private fun gotoGame() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }
}
