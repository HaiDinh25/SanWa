package com.sanwashoseki.bookskozuchi.business.ebooklibrary.services

import android.content.Context
import android.util.Log
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants
import com.sanwashoseki.bookskozuchi.business.ebooklibrary.models.responses.SWMyBookResponse
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWMyBookPresenter : MVP<SWMyBookInterface?> {

    var view: SWMyBookInterface? = null
    var disposable = CompositeDisposable()
    var page = 0

    fun getMyBook(context: Context?) {
        refresh()
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.getMyBook(Const.BEARER, 0, SWConstants.MAX_VALUE)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWMyBookResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.getMyBookSuccess(result)
                        } else {
                            view?.hideIndicator()
                            view?.showMessageError(result?.messages.toString())
                        }
                        Log.d("TAG", "getMyBook: $result")
                    }
                ) {Log.d("TAG", "getMyBook: $it")})
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    private fun refresh() {
        page = 0
    }

    fun loadMore(context: Context?) {
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.getMyBook(Const.BEARER, ++page, SWConstants.MAX_VALUE)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWMyBookResponse? ->
                        if (result?.success == true) {
                            view?.loadMoreSuccess(result)
                        }
                        Log.d("TAG", "getMyBook: $result")
                    })
                {Log.d("TAG", "getMyBook: $it")})
        }
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }

    override fun attachView(view: SWMyBookInterface?) {
        this.view = view
    }

}