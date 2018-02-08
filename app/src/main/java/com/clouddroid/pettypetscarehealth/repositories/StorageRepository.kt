package com.clouddroid.pettypetscarehealth.repositories

import android.net.Uri
import android.os.Environment
import java.io.*

/**
 * Created by Arkadiusz on 01.12.2017
 */
class StorageRepository {

    companion object {
        fun saveFile(sourceUri: Uri): Uri {

            val folder = File(Environment.getExternalStorageDirectory().toString() + "/petty")
            folder.mkdir()

            val sourceFilename = sourceUri.path
            val destinationFilename = Environment.getExternalStorageDirectory().path + File.separatorChar + "petty/" + sourceUri.lastPathSegment

            var bis: BufferedInputStream? = null
            var bos: BufferedOutputStream? = null

            try {
                bis = BufferedInputStream(FileInputStream(sourceFilename))
                bos = BufferedOutputStream(FileOutputStream(destinationFilename, false))
                val buf = ByteArray(1024)
                bis.read(buf)
                do {
                    bos.write(buf)
                } while (bis.read(buf) != -1)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                bis?.close()
                bos?.close()
                return Uri.parse(destinationFilename)
            }
        }
    }

}