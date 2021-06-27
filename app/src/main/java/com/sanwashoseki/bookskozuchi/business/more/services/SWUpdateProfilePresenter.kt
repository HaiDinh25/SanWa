package com.sanwashoseki.bookskozuchi.business.more.services

/*
 * Created by Dinh.Van.Hai on 11/12/2020
 * Mobile 0931670595
*/

import android.content.Context
import com.sanwashoseki.bookskozuchi.business.more.models.requests.SWUpdateProfileRequest
import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWUpdateProfileResponse
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWUpdateProfilePresenter : MVP<SWUpdateProfileInterface?> {

    var view: SWUpdateProfileInterface? = null
    var disposable = CompositeDisposable()

    fun updateUserInfo(context: Context?, request: SWUpdateProfileRequest?) {
//        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client?.setUserInfo(Const.BEARER, request)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWUpdateProfileResponse? ->
                        if (result?.success == true) {
//                            view?.hideIndicator()
                            view?.updateSuccess(result)
                        } else {
//                            view?.hideIndicator()
                            view?.showMessageError(result?.messages.toString())
                        }
                    }
                ) {})
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    fun validationButton(firstName: String, lastName: String, phone: String, address: String) {
        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            view?.updateButton(false)
        } else {
            view?.updateButton(true)
        }
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }

    override fun attachView(view: SWUpdateProfileInterface?) {
        this.view = view
    }

}