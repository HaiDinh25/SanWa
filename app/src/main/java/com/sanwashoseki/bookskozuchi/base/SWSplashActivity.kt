package com.sanwashoseki.bookskozuchi.base

import android.os.Bundle
import android.util.Log
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.responses.SWLoginResponse
import com.sanwashoseki.bookskozuchi.business.authentication.login.services.SWLoginInterface
import com.sanwashoseki.bookskozuchi.business.authentication.login.services.SWLoginPresenter
import com.sanwashoseki.bookskozuchi.utilities.SWUIHelper
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class SWSplashActivity : SWBaseActivity(), SWLoginInterface {

    companion object {
        val TAG: String = SWSplashActivity::class.java.simpleName
    }

    private var presenter: SWLoginPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "onCreate")
        SWUIHelper.setOverlayStatusBar(window, true)
        presenter?.attachView(this)
        Sharepref.setKeySearch(this, "")

        getFirebaseToken()
        if (Sharepref.getUserEmail(applicationContext) != "" && Sharepref.getUserPassWord(applicationContext) != "") {

        }
//        replaceActivity(SWLoginActivity::class.java)
        replaceActivity(SWMainActivity::class.java)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    private fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            Log.d("TAG", "Token: " + task.result.toString())
            Sharepref.setFirebaseToken(this, task.result.toString())
        })
    }

    override fun loginSuccess(result: SWLoginResponse?) {
        Sharepref.removeUserInfo(applicationContext)
        val email = result?.data?.customer?.email.toString()
        val password = Sharepref.getUserPassWord(applicationContext).toString()
        val token = result?.data?.accessToken?.tokenString.toString()
        val refreshToken = result?.data?.refreshToken?.tokenString.toString()
        Sharepref.saveUserInfo(applicationContext, email, password, token, refreshToken)
    }

    override fun showMessageError(msg: String) {
        Sharepref.removeUserInfo(this)
    }

    override fun updateButton(valid: Boolean) {}

    override fun showIndicator() {}

    override fun hideIndicator() {}

    override fun showNetworkError() {}

}