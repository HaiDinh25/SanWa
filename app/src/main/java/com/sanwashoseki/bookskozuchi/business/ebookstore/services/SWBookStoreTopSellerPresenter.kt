package com.sanwashoseki.bookskozuchi.business.ebookstore.services

import android.content.Context
import android.util.Log
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookStoreResponse
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWBookStoreTopSellerPresenter : MVP<SWBookStoreTopSellerInterface?> {

    var view: SWBookStoreTopSellerInterface? = null
    var disposable = CompositeDisposable()

    fun getBookStoreTopSeller(context: Context?, length: Int, isLoading: Boolean) {
        if (isLoading) {
            view?.showIndicator()
        }
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.getBookStoreTopSeller(0, length)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWBookStoreResponse? ->
                        if (result?.success == true) {
                            view?.getTopSellerSuccess(result)
                        } else {
                            if (isLoading) {
                                view?.hideIndicator()
                            }
                            view?.showMessageError(result?.messages.toString())
                        }
                    }
                ) { e -> Log.d("TAG", "getBookStoreTopSeller: $e") })
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }

    override fun attachView(view: SWBookStoreTopSellerInterface?) {
        this.view = view
    }

}