package com.edgin.around.game

import com.edgin.around.api.constants.PORT_DATA
import java.net.InetAddress
import java.net.Socket

/** Connects to the server returning [Proxy] and [Receiver] if succeeded. */
object Connector {
    data class Connection(val receiver: Receiver, val proxy: Proxy)

    private var localConnection: Socket? = null

    public fun connectLocal(address: InetAddress): Boolean {
        if (localConnection != null) {
            return false
        }

        localConnection = Socket(address, PORT_DATA)
        return true
    }

    public fun getLocalConnection(): Connection? {
        val socket = localConnection
        if (socket == null) {
            return null
        }

        val receiver = Receiver(socket.getInputStream())
        val proxy = Proxy(socket.getOutputStream())
        return Connection(receiver, proxy)
    }
}
