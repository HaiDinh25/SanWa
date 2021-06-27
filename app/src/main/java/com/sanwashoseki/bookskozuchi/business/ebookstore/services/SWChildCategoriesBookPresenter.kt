package com.sanwashoseki.bookskozuchi.business.ebookstore.services

import android.content.Context
import android.util.Log
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWChildCategoriesBookResponse
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWChildCategoriesBookPresenter : MVP<SWChildCategoriesBookInterface?> {

    var view: SWChildCategoriesBookInterface? = null
    var disposable = CompositeDisposable()
    var page = 0

    fun getCategoryBook(context: Context?, idCategory: Int, length: Int) {
        refresh()
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.getProductCategoryBook(idCategory, 0, length)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWChildCategoriesBookResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.getCategorySuccess(result)
                        } else {
                            view?.hideIndicator()
                            view?.showMessageError(result?.messages.toString())
                        }
                    }
                ) { Log.d("TAG", "getCategoryBook: $it") })
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    fun loadMoreBooks(context: Context?, idCategory: Int, length: Int) {
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.getProductCategoryBook(
                idCategory,
                ++page,
                length
            )!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result: SWChildCategoriesBookResponse? ->
                    if (result?.success == true) {
                        view?.onLoadMoreSuccess(result)
                    }
                })
        }
    }

    private fun refresh() {
        page = 0
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }

    override fun attachView(view: SWChildCategoriesBookInterface?) {
        this.view = view
    }

}