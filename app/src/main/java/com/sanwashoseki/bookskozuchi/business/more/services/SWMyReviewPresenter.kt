package com.sanwashoseki.bookskozuchi.business.more.services

import android.content.Context
import com.sanwashoseki.bookskozuchi.business.authentication.register.models.responses.SWRegisterResponse
import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWMyReviewResponse
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWMyReviewPresenter : MVP<SWMyReviewInterface> {

    var view: SWMyReviewInterface? = null
    var disposable = CompositeDisposable()

    fun getMyReview(context: Context?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client?.getMyReview(Const.BEARER)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                    { result: SWMyReviewResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.getMyReviewSuccess(result)
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

    fun deleteReview(context: Context?, id: Int?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client?.deleteReview(Const.BEARER, id)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                    { result: SWRegisterResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.deleteReviewSuccess(result)
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

    override fun attachView(view: SWMyReviewInterface) {
        this.view = view
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }
}