package com.sanwashoseki.bookskozuchi.business.ebookstore.services

import android.content.Context
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookStoreResponse
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWBookStoreLatestPresenter : MVP<SWBookStoreLatestInterface?> {

    var view: SWBookStoreLatestInterface? = null
    var disposable = CompositeDisposable()
    var page: Int = 0;

    fun getBookStoreLatest(context: Context?, length: Int, isLoading: Boolean) {
        refresh()
        if (isLoading) {
            view?.showIndicator()
        }
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.getBookStoreLatest(true, 0, length)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWBookStoreResponse? ->
                        if (result?.success == true) {
                            view?.getLatestSuccess(result)
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

    fun loadMoreBooks(context: Context?, length: Int) {
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.getBookStoreLatest(true, ++page, length)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWBookStoreResponse? ->
                        if (result?.success == true) {
                            view?.onLoadMoreSuccess(result)
                        }
                    }
                ) {})
        }
    }

    private fun refresh() {
        page = 0
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }

    override fun attachView(view: SWBookStoreLatestInterface?) {
        this.view = view
    }

}