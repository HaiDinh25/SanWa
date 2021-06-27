package com.sanwashoseki.bookskozuchi.business.more.services

/*
 * Created by Dinh.Van.Hai on 16/11/2020
 * Mobile 0931670595
*/

import android.content.Context
import com.sanwashoseki.bookskozuchi.business.authentication.logout.models.requests.SWLogoutRequest
import com.sanwashoseki.bookskozuchi.business.authentication.logout.models.responses.SWLogoutResponse
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWLogoutPresenter : MVP<SWLogoutInterface?> {

    var view: SWLogoutInterface? = null
    var disposable = CompositeDisposable()

    fun logout(context: Context?, request: SWLogoutRequest?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client?.logout(Const.BEARER, request)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { _: SWLogoutResponse? ->
                        view?.hideIndicator()
                        view?.logoutSuccess()
                    },
                    { msg: Throwable? ->
                        view?.hideIndicator()
                        view?.showMessageError(msg.toString())
                    }
                ) {})
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }

    override fun attachView(view: SWLogoutInterface?) {
        this.view = view
    }

}