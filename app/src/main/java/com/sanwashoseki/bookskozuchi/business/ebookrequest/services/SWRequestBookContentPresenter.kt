package com.sanwashoseki.bookskozuchi.business.ebookrequest.services

import android.content.Context
import com.sanwashoseki.bookskozuchi.business.ebookrequest.models.responses.SWRequestBookContentLibraryResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWAddShoppingCartWishListResponse
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody

class SWRequestBookContentPresenter : MVP<SWRequestBookContentInterface?> {

    var view: SWRequestBookContentInterface? = null
    var disposable = CompositeDisposable()

    fun getRequest(context: Context?, id: Int?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.getRequestBookContentLibrary(Const.BEARER, id)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWRequestBookContentLibraryResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.getRequestContentSuccess(result)
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

    fun replyRequest(context: Context?, bookRequestTopicId: RequestBody?, description: RequestBody?, file: ArrayList<MultipartBody.Part>) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.replyRequestBook(Const.BEARER, bookRequestTopicId, description, file)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWAddShoppingCartWishListResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.replyRequestSuccess(result)
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

    override fun attachView(view: SWRequestBookContentInterface?) {
        this.view = view
    }

}