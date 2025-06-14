package com.yaabelozerov.moodb.data.icons

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

class IconManager(private val context: Context) {
    suspend fun addIcon(uri: Uri, callback: suspend (String) -> Unit = {}) {
        withContext(Dispatchers.IO) {
            val fileName = System.currentTimeMillis().toString()
            val dir = File(context.filesDir, "Icons")
            dir.mkdir()

            val outFile = File(dir, fileName)
            val outStream = outFile.outputStream()
            val inStream = context.contentResolver.openInputStream(uri)
            Timber.tag("IconManager").i("Loading file to ${outFile.absolutePath}")

            try {
                outStream.write(
                    inStream!!.readBytes()
                )
                callback(outFile.absolutePath)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                inStream?.close()
                outStream.close()
            }
        }
    }

    suspend fun deleteIcon(path: String) {
        withContext(Dispatchers.IO) {
            val file = File(path)
            if (file.exists()) { file.delete() }
            else {
                Timber.tag("deleteIcon").e("Icon on path $path does not exist") }
        }
    }
}