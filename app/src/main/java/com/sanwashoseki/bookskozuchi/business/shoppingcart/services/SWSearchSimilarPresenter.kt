package com.sanwashoseki.bookskozuchi.business.shoppingcart.services

import android.content.Context
import android.util.Log
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookStoreResponse
import com.sanwashoseki.bookskozuchi.business.filter.models.responses.SWPublisherResponse
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWSearchSimilarPresenter : MVP<SWSearchSimilarInterface> {

    var view: SWSearchSimilarInterface? = null
    var disposable = CompositeDisposable()

    fun getSearchSimilar(context: Context?, start: Int?, length: Int?, productId: Int?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client?.getSimilar(start, length, productId)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWBookStoreResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.getSearchSimilarSuccess(result)
                        } else {
                            view?.hideIndicator()
                            view?.showMessageError(result?.messages.toString())
                        }
                    }
                ) {
                    view?.hideIndicator()
                    view?.showMessageError(it.toString())
                    Log.d("TAG", "getSearchSimilar: $it")
                })
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

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

    override fun attachView(view: SWSearchSimilarInterface) {
        this.view = view
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }
}