package com.sanwashoseki.bookskozuchi.utilities

import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import androidx.annotation.RequiresApi
import com.sanwashoseki.bookskozuchi.R

@RequiresApi(Build.VERSION_CODES.M)
class SWFingerprintHandler(private val context: Context) : FingerprintManager.AuthenticationCallback() {
    private var callback: (message: String, isSuccess: Boolean) -> Unit = { _, _ -> }
    fun startAuth(fingerPrintManager: FingerprintManager, cryptoObject: FingerprintManager.CryptoObject?,callback: (message: String, isSuccess: Boolean) -> Unit = { _, _ -> }) {
        val cancellationSignal = CancellationSignal()
        fingerPrintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
        this.callback = callback
    }

    override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()
        callback(context.getString(R.string.dialog_finger_faile),false)
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
        super.onAuthenticationSucceeded(result)
        callback("Fingerprint recognized",true)
    }
}