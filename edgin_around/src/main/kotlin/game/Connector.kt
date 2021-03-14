package com.edgin.around.game

import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

import android.util.Log

import com.edgin.around.api.actions.Action
import com.edgin.around.api.actions.ActionDeserializer
import com.edgin.around.api.constants.PORT_DATA

class Connector(var thruster: Thruster) : Runnable {
    private val running = AtomicBoolean(false)
    private val factory = MotiveFactory()
    private val gson = Action.prepareGson()

    public fun stop() {
        running.set(false)
    }

    override public fun run() {
        Log.d(TAG, "Connector: start")
        running.set(true)

        val servers = Lan().listServers()
        if (servers.size == 0) {
            stop()
            return
        }

        val address = servers[0]
        val socket = Socket(address, PORT_DATA)
        val reader = MessageProcessor(socket.getInputStream())

        while (running.get()) {
            for (message in reader.process()) {
                handleMessage(message)
            }
        }
        Log.d(TAG, "Connector: stop")
    }

    private fun handleMessage(message: String) {
        // Uncomment to see message content
        // Log.d(TAG, "Message: '${message}'")

        val action = gson.fromJson(message, Action::class.java)
        val motive = factory.build(action)
        thruster.add(motive)
    }
}

