package com.sanwashoseki.bookskozuchi.business.more.services

import android.content.Context
import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWProfileResponse
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWProfilePresenter : MVP<SWProfileInterface> {

    var view: SWProfileInterface? = null
    var disposable = CompositeDisposable()

    fun getUserInfo(context: Context?) {
        view?.showIndicateGetInfo()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client?.getUserInfo(Const.BEARER)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                    { result: SWProfileResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.getInfoSuccess(result)
                        } else {
                            view?.hideIndicator()
                            view?.showMessageInfoError(result?.messages.toString())
                        }
                    }
                ){
                    view?.hideIndicator()
                    view?.showMessageInfoError(it.toString())
                })
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    override fun attachView(view: SWProfileInterface) {
        this.view = view
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }
}