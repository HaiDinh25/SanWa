package com.sanwashoseki.bookskozuchi.business.more.services

import android.content.Context
import com.sanwashoseki.bookskozuchi.business.more.models.requests.SWChangePasswordRequest
import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWChangePasswordResponse
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWChangePasswordPresenter : MVP<SWChangePasswordInterface?> {

    var view: SWChangePasswordInterface? = null
    var disposable = CompositeDisposable()

    fun changePassword(context: Context?, request: SWChangePasswordRequest?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client?.changePassword(Const.BEARER, request)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWChangePasswordResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.changeSuccess()
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

    fun validation(oldPass: String, newPass: String, checkPass: Boolean) {
        if (oldPass.isEmpty() || newPass.isEmpty()) {
            view?.updateButton(false)
        } else {
            if (oldPass.length < 8 || newPass.length < 8) {
                view?.updateButton(false)
            } else {
                if (oldPass != newPass) {
                    if (checkPass) {
                        view?.updateButton(true)
                    } else {
                        view?.updateButton(false)
                    }
                } else {
                    view?.updateButton(false)
                }
            }
        }
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }

    override fun attachView(view: SWChangePasswordInterface?) {
        this.view = view
    }

}