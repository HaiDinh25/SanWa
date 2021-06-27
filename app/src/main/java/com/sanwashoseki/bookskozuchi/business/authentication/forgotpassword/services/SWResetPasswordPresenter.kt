package com.sanwashoseki.bookskozuchi.business.authentication.forgotpassword.services

/*
 * Created by Dinh.Van.Hai on 16/11/2020
 * Mobile 0931670595
*/

import com.sanwashoseki.bookskozuchi.utilities.MVP
import io.reactivex.disposables.CompositeDisposable

class SWResetPasswordPresenter : MVP<SWResetPasswordInterface?> {

    var view: SWResetPasswordInterface? = null
    var disposable = CompositeDisposable()

    fun validation(password: String, confirmPassword: String) {
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            view?.updateButton(false)
        }  else {
            if (password.length >= 8) {
                if (password == confirmPassword) {
                    view?.updateButton(true)
                } else {
                    view?.updateButton(false)
                }
            } else {
                view?.updateButton(false)
            }
        }
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }

    override fun attachView(view: SWResetPasswordInterface?) {
        this.view = view
    }

}