package com.sanwashoseki.bookskozuchi.business.filter.services

import android.content.Context
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants
import com.sanwashoseki.bookskozuchi.business.filter.models.responses.SWFilterBookResponse
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWFilterBookPresenter : MVP<SWFilterBookInterface> {

    var view: SWFilterBookInterface? = null
    var disposable = CompositeDisposable()

    fun filterInCategory(context: Context?, categoryIds: Int?, isAudioReading: Boolean?, priceMin: String?, priceMax: String?, publisherIds: ArrayList<Int>?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client?.filterInCategory(categoryIds, 0, SWConstants.MAX_VALUE, isAudioReading, priceMin, priceMax, publisherIds)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                    { result: SWFilterBookResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.filterSuccess(result)
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

    fun filterHighLightBook(context: Context?, isHighLight: Boolean?, categoryIds: List<Int>?, isAudioReading: Boolean?, priceMin: String?, priceMax: String?, publisherIds: ArrayList<Int>?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client?.filterHighLightBook(isHighLight, categoryIds, 0, SWConstants.MAX_VALUE, isAudioReading, priceMin, priceMax, publisherIds)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                    { result: SWFilterBookResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.filterSuccess(result)
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

    fun filterTopSellerBook(context: Context?, categoryIds: List<Int>?, isAudioReading: Boolean?, priceMin: String?, priceMax: String?, publisherIds: List<Int>?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client?.filterTopSellerBook(categoryIds, 0, SWConstants.MAX_VALUE, isAudioReading, priceMin, priceMax, publisherIds)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                    { result: SWFilterBookResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.filterSuccess(result)
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

    fun filterLatestBook(context: Context?, isLatest: Boolean?, categoryIds: List<Int>?, isAudioReading: Boolean?, priceMin: String?, priceMax: String?, publisherIds: List<Int>?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client?.filterLatestBook(isLatest, categoryIds, 0, SWConstants.MAX_VALUE, isAudioReading, priceMin, priceMax, publisherIds)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                    { result: SWFilterBookResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.filterSuccess(result)
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

    fun filterInSearch(context: Context?, author: String?, categoryIds: List<Int>?, isAudioReading: Boolean?, priceMin: String?, priceMax: String?, publisherIds: List<Int>?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client?.filterInSearch(author, categoryIds, 0, SWConstants.MAX_VALUE, isAudioReading, priceMin, priceMax, publisherIds)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                    { result: SWFilterBookResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.filterSuccess(result)
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

    override fun attachView(view: SWFilterBookInterface) {
        this.view = view
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }
}