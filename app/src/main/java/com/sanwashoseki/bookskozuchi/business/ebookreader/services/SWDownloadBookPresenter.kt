package com.sanwashoseki.bookskozuchi.business.ebookreader.services

import android.content.Context
import com.sanwashoseki.bookskozuchi.business.ebookreader.models.response.SWDrmLicenseResponse
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import com.sanwashoseki.bookskozuchi.utilities.SWApplication
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response

class SWDownloadBookPresenter : MVP<SWDownloadBookInterface?> {

    var view: SWDownloadBookInterface? = null
    var disposable = CompositeDisposable()

    fun getDrmLicense(context: Context?, productId: Int?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.getDrmLicense(Const.BEARER, productId)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWDrmLicenseResponse? ->
                        if (result?.success == true) {
//                            view?.hideIndicator()
                            view?.getDrmLicenseSuccess(result)
                        } else {
//                            view?.hideIndicator()
                            view?.showDrmMessageError(result?.messages.toString())
                        }
                    }
                ) { })
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    fun downloadBook(context: Context?, productId: Int?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.downloadBook(Const.BEARER, productId)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: Response<ResponseBody>? ->
                        if (result?.isSuccessful == true) {
                            view?.hideIndicator()
                            view?.downloadBookSuccess(result)
                        } else {
                            view?.hideIndicator()
                            view?.showDrmMessageError(result?.message().toString())
                        }
                    }
                ) {
                    view?.hideIndicator()
                })
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    fun downloadSampleBook(context: Context?, productId: Int?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.downloadSampleBook(Const.BEARER, productId)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: Response<ResponseBody>? ->
                        if (result?.isSuccessful == true) {
                            view?.hideIndicator()
                            view?.downloadSampleBookSuccess(result)
                        } else {
                            view?.hideIndicator()
                            view?.showDownloadSampleMessageError(result?.message().toString())
                        }
                    }
                ) {
                    view?.hideIndicator()
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

    override fun attachView(view: SWDownloadBookInterface?) {
        this.view = view
    }

}