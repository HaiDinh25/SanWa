package com.sanwashoseki.bookskozuchi.business.notifications.services

import android.content.Context
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants
import com.sanwashoseki.bookskozuchi.business.notifications.models.responses.SWNotificationResponse
import com.sanwashoseki.bookskozuchi.business.notifications.models.responses.SWSetStatusNotification
import com.sanwashoseki.bookskozuchi.business.notifications.models.responses.SWUnreadNotificationResponse
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import com.sanwashoseki.bookskozuchi.utilities.SWApplication
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SWNotificationsPresenter : MVP<SWNotificationsInterface> {

    var view: SWNotificationsInterface? = null
    var disposable = CompositeDisposable()

    fun getUnreadNotifications(context: Context?, isLoading: Boolean?) {
        if (isLoading == true) {
            view?.showIndicator()
        }
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client?.getUnreadNotifications(Const.BEARER)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWUnreadNotificationResponse? ->
                        if (result?.success == true) {
                            view?.getUnreadNotificationsSuccess(result)
                        } else {
                            view?.hideIndicator()
                            if (result?.code == 403) {
                                view?.expiredToken()
                            } else {
                                view?.showMessageError(result?.messages.toString())
                            }
                        }
                    }
                ) {
                    view?.hideIndicator()
                    view?.expiredToken()
                    view?.showMessageError(it.toString())
                })
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    fun getNotifications(context: Context?, isLoading: Boolean?) {
        if (isLoading == true) {
            view?.showIndicator()
        }
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client?.getNotifications(Const.BEARER,
                0,
                SWConstants.MAX_VALUE)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWNotificationResponse? ->
                        if (result?.success == true) {
                            view?.getNotificationsSuccess(result)
                        } else {
                            view?.hideIndicator()
                            if (result?.code == 403) {
                                view?.expiredToken()
                            } else {
                                view?.showMessageError(result?.messages.toString())
                            }
                        }
                    }
                ) {
                    view?.hideIndicator()
                    view?.expiredToken()
                    view?.showMessageError(it.toString())
                })
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    fun setStatusNotifications(id: Int) {
        if (NetworkUtil.isNetworkConnected(SWApplication.context)) {
            disposable.add(RetrofitClient.client?.setStatusNotification(Const.BEARER, id)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWSetStatusNotification? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.setStatusSuccess(result)
                        } else {
                            view?.hideIndicator()
                            if (result?.code == 403) {
                                view?.expiredToken()
                            } else {
                                view?.showMessageError(result?.messages.toString())
                            }
                        }
                    }
                ) {
                    view?.hideIndicator()
                    view?.expiredToken()
                    view?.showMessageError(it.toString())
                })
        }
    }

    override fun attachView(view: SWNotificationsInterface) {
        this.view = view
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }
}