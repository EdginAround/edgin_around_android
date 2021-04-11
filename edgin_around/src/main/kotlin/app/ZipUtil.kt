package com.edgin.around.app

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class ZipUtil {
    fun unzip(fileStream: InputStream, destinationDir: File) {
        val zipStream = ZipInputStream(fileStream)
        var zipEntry = zipStream.getNextEntry()
        while (zipEntry != null) {
            unsipEntry(destinationDir, zipEntry, zipStream)
            zipEntry = zipStream.getNextEntry()
        }
        zipStream.closeEntry()
        zipStream.close()
    }

    private fun unsipEntry(destinationDir: File, zipEntry: ZipEntry, zipStream: ZipInputStream) {
        val destinationFile = File(destinationDir, zipEntry.getName())

        // Guard for "Zip Slip"
        val destDirPath = destinationDir.getCanonicalPath() + File.separator
        val destFilePath = destinationFile.getCanonicalPath()
        if (!destFilePath.startsWith(destDirPath)) {
            throw IOException("Entry is outside of the target dir: " + zipEntry.getName())
        }

        // Extract a file or create a directory
        if (zipEntry.isDirectory()) {
            if (!destinationFile.isDirectory() && !destinationFile.mkdirs()) {
                throw IOException("Failed to create a directory: " + destinationFile)
            }
        } else {
            // Ensure parent directory exists
            val parentDir = destinationFile.getParentFile()
            if (!parentDir.isDirectory() && !parentDir.mkdirs()) {
                throw IOException("Failed to create a directory " + parentDir)
            }
            extractFile(destinationFile, zipStream)
        }
    }

    private fun extractFile(destinationFile: File, zipStream: ZipInputStream) {
        val buffer = ByteArray(1024)
        val fileStream = FileOutputStream(destinationFile)
        while (true) {
            val len = zipStream.read(buffer)
            if (len < 1) {
                break
            }
            fileStream.write(buffer, 0, len)
        }
        fileStream.close()
    }
}
