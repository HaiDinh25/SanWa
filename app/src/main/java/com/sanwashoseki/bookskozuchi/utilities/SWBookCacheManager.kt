package com.sanwashoseki.bookskozuchi.utilities

import android.annotation.SuppressLint
import android.os.Environment
import android.util.Log
import com.sanwashoseki.bookskozuchi.others.Const
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.ResponseBody
import java.io.*
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset

// TODO: Hai
class SWBookCacheManager {

    companion object {

        @SuppressLint("StaticFieldLeak")
        private val context = SWApplication.context

        private fun loadBook() {
            decryptBook()
        }

        private fun decryptBook() {

        }

        private fun downloadBook() {
            loadBook()
        }

        fun checkUpdateTime(idBook: Int?, time: String?): Boolean {
            val bookIdlDir = File(SWApplication.cacheDir.toString())
            return if (bookIdlDir.exists()) {
                val updateTime = SWBookDecryption.getTextFromFile("${Const.FOLDER_SANWA}/${Sharepref.getUserEmail(context )}/$idBook/${Const.FILE_LOG}")
                Log.d("TAG", "onClick: ${updateTime.replace("\n", "")}")
                time.equals(updateTime.replace("\n", ""))
            } else {
                false
            }
        }

        fun checkExistFile(idBook: Int?, fileName: String?): Boolean {
            val file = File(SWApplication.cacheDir.toString(), "${Const.FOLDER_SANWA}/${Sharepref.getUserEmail(context)}/$idBook/$fileName")
            return file.exists()
        }

        fun fileConfigText(idBook: Int?, filename: String?): File {
            return File(SWApplication.cacheDir, "${Const.FOLDER_SANWA}/${Sharepref.getUserEmail(context)}/$idBook/$filename")
        }

        fun checkFileSample(idBook: Int?): Boolean {
            val bookIdlDir = File(SWApplication.cacheDir.toString(), "${Const.FOLDER_SANWA}/${Const.FOLDER_SAMPLE}/${idBook}/${Const.FILE_SAMPLE}")
            return bookIdlDir.exists()
        }

        fun isExist(idBook: String): Boolean {
            return false
        }

        fun writeBookToDisk(body: ResponseBody?, idBook: Int?): Boolean {
            // todo change the file location/name according to your needs
            val bookIdlDir = File(SWApplication.cacheDir.toString(),
                "${Const.FOLDER_SANWA}/${
                    Sharepref.getUserEmail(
                        context)
                }/$idBook")
            writeBookData(body, bookIdlDir, Const.FILE_DATA)
            return true
        }

        fun writeSampleToDisk(body: ResponseBody?, idBook: Int?): Boolean {
            // todo change the file location/name according to your needs
            val sanwaDir = File(SWApplication.cacheDir,
                { Const.FOLDER_SANWA }.toString())
            val emailDir = File(SWApplication.cacheDir.toString(),
                "${Const.FOLDER_SANWA}/${Const.FOLDER_SAMPLE}")
            val bookIdlDir = File(SWApplication.cacheDir.toString(),
                "${Const.FOLDER_SANWA}/${Const.FOLDER_SAMPLE}/$idBook")
            if (!sanwaDir.exists()) {
                if (!sanwaDir.mkdirs()) {
                    Log.d("App", "failed to create directory")
                    return true
                }
            }
            if (!emailDir.exists()) {
                if (!emailDir.mkdirs())
                    return true
            }
            if (!bookIdlDir.exists()) {
                if (!bookIdlDir.mkdirs())
                    return true
            }
            writeBookData(body, bookIdlDir, Const.FILE_SAMPLE)
            return true
        }

        fun writeKey(key: String?, filename: String, idBook: Int?): Boolean {
            val sanwaDir = File(SWApplication.cacheDir,
                { Const.FOLDER_SANWA }.toString())
            val emailDir = File(SWApplication.cacheDir.toString(),
                "${Const.FOLDER_SANWA}/${
                    Sharepref.getUserEmail(
                        context)
                }")
            val bookIdlDir = File(SWApplication.cacheDir.toString(),
                "${Const.FOLDER_SANWA}/${
                    Sharepref.getUserEmail(
                        context)
                }/$idBook")
            if (!sanwaDir.exists()) {
                if (!sanwaDir.mkdirs()) {
                    Log.d("App", "failed to create directory")
                    return true
                }
            }
            if (!emailDir.exists()) {
                if (!emailDir.mkdirs())
                    return true
            }
            if (!bookIdlDir.exists()) {
                if (!bookIdlDir.mkdirs())
                    return true
            }
            val file = File(bookIdlDir, filename)
            val fileOutput = FileOutputStream(file)
            val outputStreamWriter = OutputStreamWriter(fileOutput)
            outputStreamWriter.write(key)
            outputStreamWriter.flush()
            fileOutput.fd.sync()
            outputStreamWriter.close()
            return true
        }

        private fun writeBookData(body: ResponseBody?, file: File?, name: String): Boolean {
            return try {
                val futureStudioIconFile = File(file, name)
                var inputStream: InputStream? = null
                var outputStream: OutputStream? = null
                try {
                    val fileReader = ByteArray(4096)
                    val fileSize = body?.contentLength()
                    var fileSizeDownloaded: Long = 0
                    inputStream = body?.byteStream()
                    outputStream = FileOutputStream(futureStudioIconFile)
                    while (true) {
                        val read: Int = inputStream!!.read(fileReader)
                        if (read == -1) {
                            break
                        }
                        outputStream.write(fileReader, 0, read)
                        fileSizeDownloaded += read.toLong()

                        val progress = fileSize?.let { fSize -> (fileSizeDownloaded.toFloat() / fSize * 100) }
                        if (progress != null) {
                            progressDownload = progress.toInt()
                        }
                        Log.d("TAG", "writeBookData: $progressDownload")
                    }
                    outputStream.flush()
                    true
                } catch (e: IOException) {
                    false
                } finally {
                    inputStream?.close()
                    outputStream?.close()
                }
            } catch (e: IOException) {
                false
            }
        }

        var progressDownload = 0
            private set

        fun readFileCache(file: File): String {
            var jsonStr = "[]"
            val stream = FileInputStream(file)
            try {
                val fc: FileChannel = stream.channel
                val bb: MappedByteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size())
                jsonStr = Charset.defaultCharset().decode(bb).toString()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                stream.close()
            }
            return jsonStr
        }

        fun cacheBookHome(key: String?, folder: String, filename: String): Boolean {
            val sanwaDir = File(SWApplication.cacheDir,
                { Const.FOLDER_SANWA }.toString())
            val childDir = File(SWApplication.cacheDir.toString(),
                "${Const.FOLDER_SANWA}/$folder")
            if (!sanwaDir.exists()) {
                if (!sanwaDir.mkdirs()) {
                    Log.d("App", "failed to create directory")
                    return true
                }
            }
            if (!childDir.exists()) {
                if (!childDir.mkdirs())
                    return true
            }
            val file = File(childDir, filename)
            val fileOutput = FileOutputStream(file)
            val outputStreamWriter = OutputStreamWriter(fileOutput)
            outputStreamWriter.write(key)
            outputStreamWriter.flush()
            fileOutput.fd.sync()
            outputStreamWriter.close()
            return true
        }
    }

    suspend fun download(id: String, updatedDate: Long, callback: ((String) -> Unit)?) {
        val cacheId = "${id}_${updatedDate}"
        if (isExist(cacheId)) {
            loadBook()
        } else {
            downloadBook()
        }
        coroutineScope {
            val deferredOne = async { downloadBook() }
            val deferredTwo = async { loadBook() }
            deferredOne.await()
            deferredTwo.await()
        }
    }

}