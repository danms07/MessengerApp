package com.hms.demo.messengerapp

import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

object AnimationUtils {
    private const val TAG = "Utils"

    /**
     * create demo dir
     *
     * @param dirPath dir path
     * @return result
     */
    fun createResourceDirs(dirPath: String?): Boolean {
        val dir = File(dirPath)
        return if (!dir.exists()) {
            if (dir.parentFile.mkdir()) {
                dir.mkdir()
            } else {
                dir.mkdir()
            }
        } else false
    }

    /**
     * copy assets folders to sdCard
     * @param context context
     * @param foldersName folderName
     * @param path path
     * @return result
     */
    fun copyAssetsFilesToDirs(
        context: Context,
        foldersName: String,
        path: String
    ): Boolean {
        try {
            val files = context.assets.list(foldersName)
            files?.let{
                for (file in it) {
                    if (!copyAssetsFileToDirs(
                            context,
                            foldersName + File.separator + file,
                            path + File.separator + file
                        )
                    ) {
                        Log.e(
                            TAG,
                            "Copy resource file fail, please check permission"
                        )
                        return false
                    }
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, e.toString())
            return false
        }
        return true
    }

    /**
     * copy resource file to sdCard
     *
     * @param context  context
     * @param fileName fileName
     * @param path     sdCard path
     * @return result
     */
    private fun copyAssetsFileToDirs(
        context: Context,
        fileName: String?,
        path: String?
    ): Boolean {
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null
        try {
            inputStream = fileName?.let{
                context.assets.open(it)
            }
            val file = path?.let{File(it)}
            outputStream = FileOutputStream(file)
            val temp = ByteArray(4096)
            var n: Int
            inputStream?.run{
                while (-1 !=read(temp).also { n = it }) {
                    outputStream.write(temp, 0, n)
                }
            }

        } catch (e: IOException) {
            Log.e(TAG, e.toString())
            return false
        } finally {
            try {
                inputStream?.close()
                outputStream?.close()
            } catch (e: IOException) {
                Log.e(TAG, toString())
            }
        }
        return true
    }

    /**
     * Add authentication parameters.
     *
     * @return JsonObject of Authentication parameters.
     */
    val authJson: JSONObject
        get() {

            return JSONObject().apply {
                put("projectId", "projectId-test")
                put("appId", "appId-test")
                put("authApiKey", "authApiKey-test")
                put("clientSecret", "clientSecret-test")
                put("clientId", "clientId-test")
                put("token", "token-test")
            }
        }
}