package com.sanwashoseki.bookskozuchi.business.ebookstore.services

import android.content.Context
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWCategoriesResponse
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWBookStoreCategoriesPresenter : MVP<SWBookStoreCategoriesInterface?> {

    var view: SWBookStoreCategoriesInterface? = null
    var disposable = CompositeDisposable()

    fun getCategories(context: Context?, isLoading: Boolean?) {
        if (isLoading == true) {
            view?.showIndicator()
        }
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.getCategory()!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWCategoriesResponse? ->
                        if (result?.success == true) {
                            view?.getCategoriesSuccess(result)
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

    override fun detachView() {
        view = null
        disposable.clear()
    }

    override fun attachView(view: SWBookStoreCategoriesInterface?) {
        this.view = view
    }

}