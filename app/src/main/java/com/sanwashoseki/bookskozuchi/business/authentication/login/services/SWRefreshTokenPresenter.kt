package com.sanwashoseki.bookskozuchi.business.authentication.login.services

/*
 * Created by Dinh.Van.Hai on 16/11/2020
 * Mobile 0931670595
*/

import android.content.Context
import android.util.Log
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.requests.SWRefreshTokenRequest
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.responses.SWLoginResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.requests.SWAddShoppingCartWishListRequest
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWAddShoppingCartWishListResponse
import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.response.SWGetShoppingCartResponse
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWRefreshTokenPresenter : MVP<SWRefreshTokenInterface?> {

    var view: SWRefreshTokenInterface? = null
    var disposable = CompositeDisposable()
    var request: SWAddShoppingCartWishListRequest? = null

    fun refreshToken(context: Context?, request: SWRefreshTokenRequest?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.refreshToken(Const.BEARER, request)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWLoginResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.refreshSuccess(result)
                            Log.d("TAG", "refreshToken: " + result.data?.accessToken)
                        } else {
                            view?.hideIndicator()
                            view?.showMessageError(result?.messages.toString())
                            Log.d("TAG", "refreshToken: " + result?.messages)
                        }
                    }
                ) {})
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    fun addShoppingCart(context: Context?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.addShoppingCartWishList(Const.BEARER, request)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWAddShoppingCartWishListResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.addShoppingCartSuccess(result)
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

    private fun getShoppingCart(context: Context?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.getShoppingCart(Const.BEARER)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWGetShoppingCartResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.getShoppingCartSuccess(result)
                            Log.d("TAG", "getShoppingCart: success")
                        } else {
                            view?.hideIndicator()
                            view?.showMessageError(result?.messages.toString())
                        }
                    }
                ) {
                    Log.d("TAG", "getShoppingCart: $it")
                })
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }

    override fun attachView(view: SWRefreshTokenInterface?) {
        this.view = view
    }

}