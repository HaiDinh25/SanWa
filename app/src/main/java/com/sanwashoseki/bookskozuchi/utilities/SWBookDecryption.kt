package com.sanwashoseki.bookskozuchi.utilities

import android.content.Context
import android.os.Environment
import android.util.Base64
import com.sanwashoseki.bookskozuchi.others.Const
import java.io.*
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class SWBookDecryption(private val privateKeyString: String?) {

    companion object {
        val TAG: String = SWBookDecryption::class.java.simpleName

        const val TEXT_ENCODING = "Shift-JIS"
        const val LENGTH_BYTES = 4

        fun testTextBookDecryption() {
            // Log.e(TAG, "testTextBookDecryption")
            val privateKeyString =
                SWApplication.context.readTextFromAsset("EncryptedBooks/Text/privateKey.txt")
            // Log.e(TAG, "\t privateKeyString $privateKeyString")

            val fileData =
                SWApplication.context.readDataFromAsset("EncryptedBooks/Text/bookData.txt")
            // Log.e(TAG, "\t fileData ${fileData.size}")

            val bookContent = SWBookDecryption(privateKeyString).decryptTextFrom(fileData)
            // Log.e(TAG, "\t bookContent $bookContent")
        }

        fun testPDFBookDecryption() {
            // Log.e(TAG, "testPDFBookDecryption")
            val privateKeyString =
                SWApplication.context.readTextFromAsset("EncryptedBooks/PDF/rsaPrivateKeyPDFFile.txt")
            // Log.e(TAG, "\t privateKeyString $privateKeyString")

            val fileData =
                SWApplication.context.readDataFromAsset("EncryptedBooks/PDF/encryptedPDFFile.pdf")
            // Log.e(TAG, "\t fileData ${fileData.size}")

            val bookContent = SWBookDecryption(privateKeyString).decryptPDFFrom(fileData)
            // Log.e(TAG, "\t bookContent $bookContent")

            val outputFile = File(SWApplication.cacheDir.toString(), "abc.pdf")
            val fos = FileOutputStream(outputFile)
            fos.write(bookContent)
            fos.close()
        }

        fun readFileTextSample(child: String): String {
            val file = File(SWApplication.cacheDir.toString(), child)
            var str = ""
            try {
                FileInputStream(file).use { fis ->
                    InputStreamReader(fis, "Shift-JIS").use { isr ->
                        BufferedReader(isr).use { reader ->
                            while (reader.readLine().also { str += (it + "\r\n") } != null) {
                                println(str)
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                return str
            }
        }

        fun getTextFromFile(child: String): String {
            //Get file
            val fileEvents = File(SWApplication.cacheDir.toString(), child)
            //Read text from file
            val text = StringBuilder()
            try {
                val br = BufferedReader(FileReader(fileEvents))
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    text.append(line)
                    text.append('\n')
                }
                br.close()
            } catch (e: IOException) {
                //You'll need to add proper error handling here
            }
            return text.toString()
        }

        private fun fileDataByBookID(idBook: Int?, fileName: String): File {
            return File(SWApplication.cacheDir.toString(),
                "${Const.FOLDER_SANWA}/${Sharepref.getUserEmail(SWApplication.context)}/${idBook}/$fileName")
        }

        fun bookDecryptionPDF(idBook: Int?) {
            // Log.e(TAG, "testTextBookDecryption")
            val privateKeyString =
                getTextFromFile("${Const.FOLDER_SANWA}/${Sharepref.getUserEmail(SWApplication.context)}/${idBook}/${Const.FILE_PRIVATE_KEY}")
            // Log.e(TAG, "\t privateKeyString $privateKeyString")

            val file = fileDataByBookID(idBook, Const.FILE_DATA)
            val size: Int = file.length().toInt()
            val bytes = ByteArray(size)
            try {
                val buf = BufferedInputStream(FileInputStream(file))
                buf.read(bytes, 0, bytes.size)
                buf.close()
            } catch (e: FileNotFoundException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            val bookContent = SWBookDecryption(privateKeyString).decryptPDFFrom(bytes)
            val outputFile = fileDataByBookID(idBook, Const.FILE_BOOK_PDF)
            outputFile.createNewFile()
            val fos = FileOutputStream(outputFile)
            try {
                fos.write(bookContent)
                fos.flush()
                fos.close()
            } catch (e: IOException) {
                // Log.d(TAG, "bookDecryptionPDF: $e")
            }
        }

        fun bookDecryptionTXT(idBook: Int?): String {
            // Log.e(TAG, "testTextBookDecryption")
            val privateKeyString =
                getTextFromFile("${Const.FOLDER_SANWA}/${Sharepref.getUserEmail(SWApplication.context)}/${idBook}/${Const.FILE_PRIVATE_KEY}")
            // Log.e(TAG, "\t privateKeyString $privateKeyString")

            val file = fileDataByBookID(idBook, Const.FILE_DATA)
            val size: Int = file.length().toInt()
            val bytes = ByteArray(size)
            try {
                val fis = FileInputStream(file)
                val buf = BufferedInputStream(fis)
                buf.read(bytes, 0, bytes.size)
                buf.close()
                fis.close()
            } catch (e: FileNotFoundException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            val decryptedText = SWBookDecryption(privateKeyString).decryptTextFrom(bytes)
            return decryptedText
            // return Utils.formatRubyText(decryptedText)
        }
    }

    init {

    }

    fun decryptTextFrom(encryptedData: ByteArray): String {
        val decryptedData = decryptDataFrom(encryptedData)
        val bookContent = decryptedData.toString(Charset.forName(TEXT_ENCODING))
        // Log.e(TAG, "\t bookContenthfsdjh $bookContent")
        return bookContent
    }

    fun decryptPDFFrom(encryptedData: ByteArray): ByteArray {
        return decryptDataFrom(encryptedData)
    }

    private fun decryptDataFrom(encryptedData: ByteArray): ByteArray {
        val privateKeyString = privateKeyString ?: return ByteArray(0)
        // Log.e(TAG, "\t privateKeyString: $privateKeyString")

        val privateKey = privateKeyString.createPrivateKey()
        var dataOffset = 0

        val keyLengthData = encryptedData.copyOfRange(dataOffset, dataOffset + LENGTH_BYTES)
        // Log.e(TAG, "\t keyLengthData ${keyLengthData.size} dataOffset $dataOffset")

        val keyLengthValue = keyLengthData.intValue()
        // Log.e(TAG, "\t keyLengthValue $keyLengthValue dataOffset $dataOffset")

        dataOffset += LENGTH_BYTES

        val ivLengthData = encryptedData.copyOfRange(dataOffset, dataOffset + LENGTH_BYTES)
        // Log.e(TAG, "\t ivLengthData ${ivLengthData.size} dataOffset $dataOffset")

        val ivLengthValue = ivLengthData.intValue()
        // Log.e(TAG, "\t ivLengthValue $ivLengthValue dataOffset $dataOffset")

        dataOffset += LENGTH_BYTES

        val keyData = encryptedData.copyOfRange(dataOffset, dataOffset + keyLengthValue)
        // Log.e(TAG, "\t keyData ${keyData.size} dataOffset $dataOffset")

        dataOffset += keyLengthValue

        val ivData = encryptedData.copyOfRange(dataOffset, dataOffset + ivLengthValue)
        // Log.e(TAG, "\t ivData ${ivData.size}  dataOffset $dataOffset")

        dataOffset += ivLengthValue

        val bookData = encryptedData.copyOfRange(dataOffset, encryptedData.size)
        // Log.e(TAG, "\t bookData ${bookData.size} dataOffset $dataOffset")

        val decryptedKeyData = keyData.decryptKeyBy(privateKey)
        // Log.e(TAG, "\t decryptedKeyData ${decryptedKeyData.size}")

        val decryptedData = bookData.decryptDataBy(decryptedKeyData, ivData)
        // Log.e(TAG, "\t decryptedData ${decryptedData.size}")

        return decryptedData
    }

}

fun Context.readTextFromAsset(fileName: String): String {
    return assets.open(fileName).bufferedReader().use {
        it.readText()
    }
}

fun Context.readDataFromAsset(fileName: String): ByteArray {
    assets.open(fileName).use { inputStream ->
        val fileBytes = ByteArray(inputStream.available())
        inputStream.read(fileBytes)
        inputStream.close()
        return fileBytes
    }
}

fun String.normalizeKey(): String {
    return replace("\\r".toRegex(), "")
        .replace("\\n".toRegex(), "")
        .replace(System.lineSeparator().toRegex(), "")
        .replace("-----BEGIN RSA PRIVATE KEY-----", "")
        .replace("-----END RSA PRIVATE KEY-----", "")
}

fun String.createPrivateKey(): PrivateKey {
    val privateKeyBytes = Base64.decode(normalizeKey(), Base64.DEFAULT)
    return privateKeyBytes.createPrivateKey()
}

fun ByteArray.createPrivateKey(): PrivateKey {
    val specPrivateKey = PKCS8EncodedKeySpec(this)
    val factory = KeyFactory.getInstance("RSA")
    return factory.generatePrivate(specPrivateKey)
}

fun ByteArray.intValue(): Int {
    var result = 0
    for (i in indices) {
        result = result or (this[i].toInt() shl 8 * i)
    }
    return result
}

fun ByteArray.decryptDataBy(keyData: ByteArray, ivData: ByteArray): ByteArray {
    val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
    val paramSpec = IvParameterSpec(ivData)
    val keySpec = SecretKeySpec(keyData, "AES/ECB/PKCS7Padding")
    cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec)
    return cipher.doFinal(this)
}

fun ByteArray.decryptKeyBy(privateKey: PrivateKey): ByteArray {
    val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    cipher.init(Cipher.DECRYPT_MODE, privateKey)
    return cipher.doFinal(this)
}