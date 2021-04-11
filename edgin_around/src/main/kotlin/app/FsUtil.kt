package com.edgin.around.app

import java.io.File

class FsUtil {
    fun remove(file: File) {
        val contents = file.listFiles()
        if (contents != null) {
            for (subfile in contents) {
                remove(subfile)
            }
        }
        file.delete()
    }
}
