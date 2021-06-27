package com.sanwashoseki.bookskozuchi.business.shoppingcart.services

import android.content.Context
import android.util.Log
import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.response.SWGetShoppingCartResponse
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWShoppingCartPresenter : MVP<SWShoppingCartInterface> {

    var view: SWShoppingCartInterface? = null
    var disposable = CompositeDisposable()

    fun getShoppingCart(context: Context?, isLoading: Boolean) {
        if (isLoading) {
            view?.showIndicator()
        }
        val token = Sharepref.getUserToken(context)
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client?.getShoppingCart(Const.BEARER)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWGetShoppingCartResponse? ->
                        if (result?.success == true) {
                            view?.getShoppingCardSuccess(result)
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

    fun validButton(size: Int) {
        if (size != 0) {
            view?.validationButton(true)
        } else {
            view?.validationButton(false)
        }
    }

    override fun attachView(view: SWShoppingCartInterface) {
        this.view = view
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }
}