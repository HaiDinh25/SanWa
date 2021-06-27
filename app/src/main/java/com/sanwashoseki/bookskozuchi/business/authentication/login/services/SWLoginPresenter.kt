package com.sanwashoseki.bookskozuchi.business.authentication.login.services

/*
 * Created by Dinh.Van.Hai on 16/11/2020
 * Mobile 0931670595
*/

import android.content.Context
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.requests.SWLoginFacebookRequest
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.requests.SWLoginLineRequest
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.requests.SWLoginRequest
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.responses.SWLoginResponse
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWLoginPresenter : MVP<SWLoginInterface?> {

    var view: SWLoginInterface? = null
    var disposable = CompositeDisposable()

    fun loginEmail(context: Context?, request: SWLoginRequest) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.loginEmail(request)!!
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
                ) {
                    view?.hideIndicator()
                })
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

    fun validation(email: String, password: String, validEmail: Boolean) {
        if (validEmail) {
            if (email.isEmpty() || password.isEmpty()) {
                view?.updateButton(false)
            } else {
                if (password.length >= 8) {
                    view?.updateButton(true)
                } else {
                    view?.updateButton(false)
                }
            }
        } else {
            view?.updateButton(false)
        }
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }

    override fun attachView(view: SWLoginInterface?) {
        this.view = view
    }

}