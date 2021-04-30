package com.edgin.around.game

import android.util.Log
import com.edgin.around.api.actions.Action
import java.io.InputStream
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Creates a thread listening for messages from the passed input stream and forwards them to the
 * `thruster`.
 */
class Receiver(val stream: InputStream, val thruster: Thruster) : Runnable {
    private val running = AtomicBoolean(false)
    private val factory = MotiveFactory()
    private val gson = Action.prepareGson()

    public fun stop() {
        running.set(false)
    }

    public override fun run() {
        Log.d(TAG, "Receiver: start")
        running.set(true)
        val reader = MessageProcessor(stream)
        while (running.get()) {
            for (message in reader.process()) {
                handleMessage(message)
            }
        }
        Log.d(TAG, "Receiver: stop")
    }

    private fun handleMessage(message: String) {
        // Uncomment to see message content
        // Log.d(TAG, "Message: '${message}'")

        val action = gson.fromJson(message, Action::class.java)
        val motive = factory.build(action)
        thruster.add(motive)
    }
}
