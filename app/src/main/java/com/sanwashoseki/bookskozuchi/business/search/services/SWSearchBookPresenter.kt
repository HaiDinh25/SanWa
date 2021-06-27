package com.sanwashoseki.bookskozuchi.business.search.services

import android.content.Context
import android.util.Log
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants
import com.sanwashoseki.bookskozuchi.business.search.models.responses.SWSearchBookResponse
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWSearchBookPresenter : MVP<SWSearchBookInterface> {

    val TAG = "SWSearchBookPresenter"
    var view: SWSearchBookInterface? = null
    var disposable = CompositeDisposable()
    var page = 0

    fun searchBook(context: Context?, content: String) {
        refresh()
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client?.searchBook(content, 0, SWConstants.MAX_VALUE)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                    { result: SWSearchBookResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.searchSuccess(result)
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

    private fun refresh() {
        page = 0
    }

    fun loadMoreBooks(context: Context?, content: String) {
        if (NetworkUtil.isNetworkConnected(context)) {
            Log.d(TAG, "loadMoreBooks: search $page")
            disposable.add(RetrofitClient.client?.searchBook(content, ++page, SWConstants.MAX_VALUE * 2)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result: SWSearchBookResponse? ->
                    if (result?.success == true) {
                        view?.onLoadMoreSuccess(result)
                    }
                })
        }
    }

    override fun attachView(view: SWSearchBookInterface) {
        this.view = view
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }
}