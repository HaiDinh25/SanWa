package com.sanwashoseki.bookskozuchi.business.shoppingcart.services

import android.content.Context
import android.util.Log
import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.response.SWRemoveShoppingCartResponse
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWRemoveShoppingCartPresenter : MVP<SWRemoveShoppingCartInterface> {

    var view: SWRemoveShoppingCartInterface? = null
    var disposable = CompositeDisposable()

    fun removeItemShoppingCart(context: Context?, id: Int?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client?.removeShoppingCart(Const.BEARER, id)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWRemoveShoppingCartResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.removeItemSuccess(result)
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

    fun removeAllShoppingCart(context: Context?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client?.removeAllCartOrWishList(Const.BEARER, 1)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWRemoveShoppingCartResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.removeAllSuccess(result)
                            Log.d("TAG", "removeItemShoppingCart: " + result.success)
                        } else {
                            view?.hideIndicator()
                            view?.showMessageError(result?.messages.toString())
                            Log.d("TAG", "removeItemShoppingCart: " + result?.messages.toString())
                        }
                    }
                ) {
                    view?.hideIndicator()
                    view?.showMessageError(it.toString())
                    Log.d("TAG", "error: $it")
                })
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    override fun attachView(view: SWRemoveShoppingCartInterface) {
        this.view = view
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }
}