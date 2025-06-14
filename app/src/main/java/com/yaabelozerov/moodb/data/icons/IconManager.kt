package com.yaabelozerov.moodb.data.icons

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class IconManager @Inject constructor(@ApplicationContext private val app: Context) {
    suspend fun addIcon(uri: Uri, callback: suspend (String) -> Unit = {}) {
        withContext(Dispatchers.IO) {
            val fileName = System.currentTimeMillis().toString()
            val dir = File(app.filesDir, "Icons")
            dir.mkdir()

            val outFile = File(dir, fileName)
            val outStream = outFile.outputStream()
            val inStream = app.contentResolver.openInputStream(uri)
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