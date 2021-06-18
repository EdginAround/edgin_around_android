package com.edgin.around.game

import com.edgin.around.api.constants.PORT_DATA
import java.net.Socket

/** Connects to the server returning [Proxy] and [Receiver] if succeeded. */
class Connector(var thruster: Thruster) {
    data class ConnectionResult(val receiver: Receiver, val proxy: Proxy)

    public fun connect(): ConnectionResult? {
        val servers = Lan().listServers()
        if (servers.size == 0) {
            return null
        }

        val address = servers[0]
        val socket = Socket(address, PORT_DATA)
        val receiver = Receiver(socket.getInputStream(), thruster)
        val proxy = Proxy(socket.getOutputStream())
        return ConnectionResult(receiver, proxy)
    }
}
