package com.sanwashoseki.bookskozuchi.business.authentication.register.services

import android.content.Context
import android.util.Log
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.requests.SWLoginFacebookRequest
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.requests.SWLoginLineRequest
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.responses.SWLoginResponse
import com.sanwashoseki.bookskozuchi.business.authentication.register.models.requests.SWRegisterRequest
import com.sanwashoseki.bookskozuchi.business.authentication.register.models.responses.SWRegisterResponse
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWRegisterPresenter : MVP<SWRegisterInterface?> {

    var view: SWRegisterInterface? = null
    var disposable = CompositeDisposable()

    fun register(context: Context?, request: SWRegisterRequest) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.register(request)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWRegisterResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.registerSuccess(result)
                            Log.d("TAG", "register: $result")
                        } else {
                            view?.hideIndicator()
                            view?.showMessageError(result?.messages.toString())
                        }
                    }
                ) {})
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    fun loginFacebook(context: Context?, request: SWLoginFacebookRequest) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.loginFacebook(request)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWLoginResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.loginSuccess(result)
                        } else {
                            view?.hideIndicator()
                            view?.showMessageError(result?.messages.toString())
                        }
                    }
                ) {})
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    fun loginLine(context: Context?, request: SWLoginLineRequest) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.loginLine(request)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWLoginResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.loginSuccess(result)
                        } else {
                            view?.hideIndicator()
                            view?.showMessageError(result?.messages.toString())
                        }
                    }
                ) {})
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    fun validation(
        firstName: String,
        lastName: String,
        email: String,
        pass: String,
        confirmPass: String,
        validEmail: Boolean
    ) {
        if (validEmail) {
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
                view?.updateButton(false)
            } else {
                if (pass.length >= 8) {
                    if (pass == confirmPass) {
                        view?.updateButton(true)
                    } else {
                        view?.updateButton(false)
                    }
                } else {
                    view?.updateButton(false)
                }
            }
        } else {
            view?.updateButton(false)
        }
    }

    override fun attachView(view: SWRegisterInterface?) {
        this.view = view
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }
}