package com.sanwashoseki.bookskozuchi.business.shoppingcart.services

import android.content.Context
import android.util.Log
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWAddShoppingCartWishListResponse
import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.requests.SWMakeThePaymentRequest
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWMakeThePaymentPresenter : MVP<SWMakeThePaymentInterface> {

    var view: SWMakeThePaymentInterface? = null
    var disposable = CompositeDisposable()

    fun makeThePayment(context: Context?, request: SWMakeThePaymentRequest?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client?.makeThePayment(Const.BEARER, request)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWAddShoppingCartWishListResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.makePaymentSuccess(result)
                        } else {
                            view?.hideIndicator()
                            view?.showMessageError(result?.messages.toString())
                        }
                    }
                ) {
                    view?.hideIndicator()
                    view?.showMessageError(it.toString())
                    Log.d("TAG", "getShoppingCard: $it")
                })
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    fun validation(cardNumber: String, cvv: String) {
        if (cardNumber.isEmpty() || cvv.isEmpty()) {
            view?.updateButton(false)
        } else {
            view?.updateButton(true)
        }
    }

    override fun attachView(view: SWMakeThePaymentInterface) {
        this.view = view
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }
}