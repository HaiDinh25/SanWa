package com.sanwashoseki.bookskozuchi.business.ebookstore.services

import android.content.Context
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookStoreResponse
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWBookStoreHighLightPresenter : MVP<SWBookStoreHighLightInterface?> {

    var view: SWBookStoreHighLightInterface? = null
    var disposable = CompositeDisposable()
    var page = 0

    fun getBookStoreHighLight(context: Context?, length: Int, isLoading: Boolean) {
        refresh()
        if (isLoading) {
            view?.showIndicator()
        }
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.getBookStoreHighLight(true, 0 ,length)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWBookStoreResponse? ->
                        if (result?.success == true) {
                            view?.getHighLightSuccess(result)
                        } else {
                            if (isLoading) {
                                view?.hideIndicator()
                            }
                            view?.showMessageError(result?.messages.toString())
                        }
                    }
                ) {
                    view?.hideIndicator()
                    view?.showMessageError(it.toString())
                })
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    private fun refresh() {
        page = 0
    }

    fun loadMoreBooks(context: Context?, length: Int) {
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.getBookStoreHighLight(true, ++page ,length)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result: SWBookStoreResponse? ->
                    if (result?.success == true) {
                        view?.onLoadMoreSuccess(result)
                    }
                })
        }
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }

    override fun attachView(view: SWBookStoreHighLightInterface?) {
        this.view = view
    }

}