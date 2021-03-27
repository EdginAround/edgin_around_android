package com.edgin.around.game

import android.util.Log
import com.edgin.around.api.constants.API_VERSION
import com.edgin.around.api.constants.PORT_BROADCAST
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketTimeoutException

data class HelloMessage(
    @SerializedName("name")
    val name: String,

    @SerializedName("version")
    val version: String
)

class Lan {
    val gson = Gson()

    fun listServers(): ArrayList<InetAddress> {
        Log.i(TAG, "Searching for servers...")
        val socket = DatagramSocket()

        var interfaces = NetworkInterface.getNetworkInterfaces()
        while (interfaces.hasMoreElements()) {
            val networkInterface = interfaces.nextElement()
            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue
            }

            for (interfaceAddress in networkInterface.getInterfaceAddresses()) {
                val broadcastAddress = interfaceAddress.getBroadcast()
                if (broadcastAddress == null) {
                    continue
                }

                sendDicsoveryBroadcast(socket, broadcastAddress)
            }
        }

        return gatherResponses(socket)
    }

    private fun sendDicsoveryBroadcast(socket: DatagramSocket, broadcastAddress: InetAddress) {
        Log.i(TAG, "Broadcasting on: $broadcastAddress")
        socket.setBroadcast(true)

        val name = "edgin_around"
        val version = "${API_VERSION[0]}.${API_VERSION[1]},${API_VERSION[2]}"
        val message = HelloMessage(name, version)
        val json = gson.toJson(message)
        val buffer = json.toByteArray(Charsets.UTF_8)
        val packet = DatagramPacket(buffer, buffer.size, broadcastAddress, PORT_BROADCAST)

        socket.send(packet)
    }

    private fun gatherResponses(socket: DatagramSocket): ArrayList<InetAddress> {
        socket.setSoTimeout(1000)

        val result = ArrayList<InetAddress>()
        val bufferSize = 1024
        var buffer = ByteArray(bufferSize)
        var packet = DatagramPacket(buffer, bufferSize)
        while (true) {
            try {
                socket.receive(packet)
            } catch (e: SocketTimeoutException) {
                // Expected exception
                break
            }
            val response = String(packet.getData(), 0, packet.getLength())
            val address = packet.getAddress()
            Log.i(TAG, "Response from $address: '$response'")
            result.add(address)
        }

        Log.i(TAG, "${result.size} servers found")
        return result
    }
}
