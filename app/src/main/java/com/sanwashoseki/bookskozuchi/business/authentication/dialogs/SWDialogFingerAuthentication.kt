package com.sanwashoseki.bookskozuchi.business.authentication.dialogs

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Dialog
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.more.dialogs.SWDialogInputPassCode
import com.sanwashoseki.bookskozuchi.utilities.SWFingerprintHandler
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

class SWDialogFingerAuthentication(context: Context) : Dialog(context) {

    private lateinit var keyStore: KeyStore
    private lateinit var cipher: Cipher
    private var KEY_NAME = "AndroidKey"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_finger_authentication)

        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.attributes.windowAnimations = R.style.DialogBotToTopAnim
        setCancelable(false)

        val imgFinger: ImageView = findViewById(R.id.imgFinger)
        val tvMessage: TextView = findViewById(R.id.tvMessage)
        val btnPassCode: TextView = findViewById(R.id.btnPassCode)

        val shape = imgFinger.background as GradientDrawable
        shape.setColor(ContextCompat.getColor(context, R.color.colorAccent))
        imgFinger.background = shape

        val fingerPrintManager = context.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (!fingerPrintManager.isHardwareDetected) {
            dismiss()
//            val dialog = SWDialogInputPassCode(context, true)
//            dialog.show()
//            dismiss()
            Toast.makeText(context, "Hardware not detected", Toast.LENGTH_SHORT).show()
        } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            tvMessage.text = "Permission is not granted"
        } else if (!keyguardManager.isKeyguardSecure) {
            tvMessage.text = "Please add your phone Key guard"
        } else if (!fingerPrintManager.hasEnrolledFingerprints()) {
            tvMessage.text = "You should add atleast 1 fingerprint to use this feature"
        } else {
            val fingerprintHandler = SWFingerprintHandler(context)
            fingerprintHandler.startAuth(fingerPrintManager, null) { message: String, isSuccess: Boolean ->
                tvMessage.text = message
                if (isSuccess) {
                    imgFinger.setImageResource(R.drawable.ic_check)
                    tvMessage.setTextColor(ContextCompat.getColor(context, R.color.green))
                    shape.setColor(ContextCompat.getColor(context, R.color.green))
                    Toast.makeText(context, "Authentication succeeded.", Toast.LENGTH_SHORT).show()
                    dismiss()
                } else {
                    imgFinger.setImageResource(R.drawable.ic_finger_error)
                    tvMessage.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                    shape.setColor(ContextCompat.getColor(context, R.color.colorAccent))
                }
                imgFinger.background = shape
            }
        }

        btnPassCode.setOnClickListener {
            val dialog = SWDialogInputPassCode(context, true)
            dialog.show()
            dismiss()
//            dialog.onCallbackListener(this)
        }

        @TargetApi(Build.VERSION_CODES.M)
        fun generateKey() {
            try {
                keyStore = KeyStore.getInstance("AndroidKeyStore")
                val keyGenerator =
                    KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")

                keyStore.load(null)
                keyGenerator.init(KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                        KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build())
                keyGenerator.generateKey()
            } catch (e: KeyStoreException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: CertificateException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: InvalidAlgorithmParameterException) {
                e.printStackTrace()
            } catch (e: NoSuchProviderException) {
                e.printStackTrace()
            }
        }

        @TargetApi(Build.VERSION_CODES.M)
        fun cipherInit(): Boolean {
            try {
                cipher =
                    Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7)
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException("Failed to get Cipher", e)
            } catch (e: NoSuchPaddingException) {
                throw RuntimeException("Failed to get Cipher", e)
            }
            try {
                keyStore.load(null)
                val key = keyStore.getKey(KEY_NAME, null) as SecretKey
                cipher.init(Cipher.ENCRYPT_MODE, key)
                return true
            } catch (e: KeyPermanentlyInvalidatedException) {
                return false
            } catch (e: KeyStoreException) {
                throw RuntimeException("Failed to init Cipher", e)
            } catch (e: CertificateException) {
                throw RuntimeException("Failed to init Cipher", e)
            } catch (e: UnrecoverableKeyException) {
                throw RuntimeException("Failed to init Cipher", e)
            } catch (e: IOException) {
                throw RuntimeException("Failed to init Cipher", e)
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException("Failed to init Cipher", e)
            } catch (e: InvalidKeyException) {
                throw RuntimeException("Failed to init Cipher", e)
            }
        }
    }
}