package com.sanwashoseki.bookskozuchi.business.filter.services

import android.content.Context
import com.sanwashoseki.bookskozuchi.business.filter.models.responses.SWPublisherResponse
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWPublisherPresenter : MVP<SWPublisherInterface> {

    var view: SWPublisherInterface? = null
    var disposable = CompositeDisposable()

    fun getPublisher(context: Context?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client?.getPublisher()!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                    { result: SWPublisherResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.getPublisherSuccess(result)
                        } else {
                            view?.hideIndicator()
                            view?.showMessageError(result?.messages.toString())
                        }
                    }
                ){
                    view?.hideIndicator()
                    view?.showMessageError(it.toString())
                })
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    fun validButton(size: Int?) {
        if (size == 0) {
            view?.validButton(false)
        } else {
            view?.validButton(true)
        }
    }

    override fun attachView(view: SWPublisherInterface) {
        this.view = view
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }
}