package com.edgin.around.rendering

val LIB_VERSION: IntArray = intArrayOf(0, 1, 2)

class About {
    internal val bridge = AboutBridge()

    fun getVersion(): Array<String> {
        return bridge.getVersion()
    }

    fun checkVersion(): Boolean {
        val version = bridge.getVersion()
        val major = version[0].toInt()
        val minor = version[1].toInt()
        val patch = version[2].toInt()
        return (major == LIB_VERSION[0]) && (minor == LIB_VERSION[1]) && (patch == LIB_VERSION[2])
    }
}

class AboutBridge {
    external fun getVersion(): Array<String>
}
