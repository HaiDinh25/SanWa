package com.sanwashoseki.bookskozuchi.books.services

import com.sanwashoseki.bookskozuchi.books.models.SWBookmarksResponse
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.utils.SWCommonResponse
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import com.sanwashoseki.bookskozuchi.utilities.SWApplication
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWBookmarksPresenter : MVP<SWBookmarksInterface?> {

    var view: SWBookmarksInterface? = null
    var disposable = CompositeDisposable()

    fun getBookmarks(productId: Int?, page: Int?, pageSize: Int?) {
        if (NetworkUtil.isNetworkConnected(SWApplication.context)) {
            view?.showIndicator()
            disposable.add(RetrofitClient.client!!.listBookmark(Const.BEARER, productId, page, pageSize)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWBookmarksResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.getBookMarkSuccess(result)
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

    fun deleteBookmarks(id: Int?) {
        if (NetworkUtil.isNetworkConnected(SWApplication.context)) {
            view?.showIndicator()
            disposable.add(RetrofitClient.client!!.deleteBookmark(Const.BEARER, id)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWCommonResponse<Any>? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.deleteBookMarkSuccess(result)
                        } else {
                            view?.hideIndicator()
                            view?.showMessageError(result?.messages.toString())
                        }
                    }
                ) {
                    view?.hideIndicator()
                })
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }

    override fun attachView(view: SWBookmarksInterface?) {
        this.view = view
    }

}