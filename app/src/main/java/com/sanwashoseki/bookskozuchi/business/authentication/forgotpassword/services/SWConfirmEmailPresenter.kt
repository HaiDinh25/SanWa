package com.sanwashoseki.bookskozuchi.business.authentication.forgotpassword.services

/*
 * Created by Dinh.Van.Hai on 16/11/2020
 * Mobile 0931670595
*/

import android.content.Context
import com.sanwashoseki.bookskozuchi.business.authentication.forgotpassword.models.requests.SWConfirmEmailRequest
import com.sanwashoseki.bookskozuchi.business.authentication.register.models.responses.SWRegisterResponse
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWConfirmEmailPresenter : MVP<SWConfirmEmailInterface?> {

    var view: SWConfirmEmailInterface? = null
    var disposable = CompositeDisposable()

    fun confirmEmail(context: Context?, request: SWConfirmEmailRequest?) {
        if (NetworkUtil.isNetworkConnected(context)) {
            view?.showIndicator()
            disposable.add(RetrofitClient.client!!.confirmEmail(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWRegisterResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.confirmSuccess(result)
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

    fun validation(email: String, validEmail: Boolean) {
        if (validEmail) {
            if (email.isEmpty()) {
                view?.updateButton(false)
            } else {
                view?.updateButton(true)
            }
        } else {
            view?.updateButton(false)
        }
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }

    override fun attachView(view: SWConfirmEmailInterface?) {
        this.view = view
    }

}