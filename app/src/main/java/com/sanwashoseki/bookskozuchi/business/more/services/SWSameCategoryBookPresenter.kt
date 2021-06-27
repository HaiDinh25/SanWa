package com.sanwashoseki.bookskozuchi.business.more.services

import android.content.Context
import com.sanwashoseki.bookskozuchi.business.filter.models.responses.SWFilterBookResponse
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWSameCategoryBookPresenter : MVP<SWSameCategoryBookInterface> {

    var view: SWSameCategoryBookInterface? = null
    var disposable = CompositeDisposable()

    fun getSameCategoryBook(context: Context?, ids: List<Int>, start: Int, length: Int) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client?.getSameCategory(ids, start, length, null)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                    { result: SWFilterBookResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.getSameCategoryBookSuccess(result)
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

    fun getSamePublisherBook(context: Context?, ids: List<Int>, start: Int, length: Int) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client?.getSameCategory(null, start, length, ids)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                    { result: SWFilterBookResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.getSamePublisherBookSuccess(result)
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

    override fun attachView(view: SWSameCategoryBookInterface) {
        this.view = view
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }
}