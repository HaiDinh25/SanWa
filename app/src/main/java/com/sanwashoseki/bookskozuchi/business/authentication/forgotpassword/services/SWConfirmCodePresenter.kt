package com.sanwashoseki.bookskozuchi.business.authentication.forgotpassword.services

import com.sanwashoseki.bookskozuchi.utilities.MVP
import io.reactivex.disposables.CompositeDisposable

class SWConfirmCodePresenter : MVP<SWConfirmCodeInterface?> {

    var view: SWConfirmCodeInterface? = null
    var disposable = CompositeDisposable()

    fun validation(code: String) {
        if (code.isEmpty()) {
            view?.updateButton(false)
        } else {
            if (code.length < 6) {
                view?.updateButton(false)
            } else {
                view?.updateButton(true)
            }
        }
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }

    override fun attachView(view: SWConfirmCodeInterface?) {
        this.view = view
    }

}