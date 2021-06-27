package com.sanwashoseki.bookskozuchi.business.ebookrequest.services

import android.content.Context
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants
import com.sanwashoseki.bookskozuchi.business.ebookrequest.models.responses.SWRequestBookLibraryResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWAddShoppingCartWishListResponse
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWRequestBookPresenter : MVP<SWRequestBookInterface?> {

    var view: SWRequestBookInterface? = null
    var disposable = CompositeDisposable()

    fun getRequest(context: Context?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.getRequestBookLibrary(0, SWConstants.MAX_VALUE)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWRequestBookLibraryResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.getRequestSuccess(result)
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

    fun getMyRequest(context: Context?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.getMyRequestBook(Const.BEARER,0, SWConstants.MAX_VALUE, null)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWRequestBookLibraryResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.getMyRequestSuccess(result)
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

    fun deleteRequest(context: Context?, id: Int?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.deleteRequest(Const.BEARER,id)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWAddShoppingCartWishListResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.deleteRequestSuccess(result)
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

    override fun attachView(view: SWRequestBookInterface?) {
        this.view = view
    }

}