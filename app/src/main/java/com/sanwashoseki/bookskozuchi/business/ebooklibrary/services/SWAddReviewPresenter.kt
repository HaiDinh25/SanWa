package com.sanwashoseki.bookskozuchi.business.ebooklibrary.services

import android.content.Context
import com.sanwashoseki.bookskozuchi.business.ebooklibrary.models.requests.SWAddReviewRequest
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWAddShoppingCartWishListResponse
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWAddReviewPresenter : MVP<SWAddReviewInterface?> {

    var view: SWAddReviewInterface? = null
    var disposable = CompositeDisposable()

    fun sendReview(context: Context?, request: SWAddReviewRequest?) {
        if (NetworkUtil.isNetworkConnected(context)) {
            view?.showIndicator()
            disposable.add(RetrofitClient.client!!.sendReview(Const.BEARER, request)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWAddShoppingCartWishListResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.sendReviewSuccess(result)
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

    override fun attachView(view: SWAddReviewInterface?) {
        this.view = view
    }

}