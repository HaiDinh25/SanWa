package com.sanwashoseki.bookskozuchi.business.ebookreader.services

import android.content.Context
import android.util.Log
import com.sanwashoseki.bookskozuchi.business.ebookreader.models.response.SWDrmLicenseResponse
import com.sanwashoseki.bookskozuchi.business.ebookreader.models.response.SWReadingNowResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWBookDetailResponse
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import com.sanwashoseki.bookskozuchi.utilities.SWBookCacheManager
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response

class SWReadingNowPresenter : MVP<SWReadingNowInterface?> {

    var view: SWReadingNowInterface? = null
    var disposable = CompositeDisposable()
    var bookDetail: SWBookDetailResponse.Data? = null
    var bookId: Int? = null

    fun getReadingNow(context: Context?) {
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.getReadingNow(Const.BEARER)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWReadingNowResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.getReadingNowSuccess(result)
                        } else {
                            view?.hideIndicator()
                            view?.showMessageError(result?.messages.toString())
                        }
                    }
                ) { })
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    fun getBookDetail(context: Context?) {
        if (NetworkUtil.isNetworkConnected(context)) {
//            view?.showIndicator()
            disposable.add(RetrofitClient.client!!.getDetailBook(Const.BEARER, bookId)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWBookDetailResponse? ->
                        if (result?.success == true) {
                            view?.getBookDetailSuccess(result)
                            SWBookCacheManager.writeKey(result.toString(), Const.FILE_BOOK_DETAIL, bookId)
                            if (result.data?.isPurchased == true) {
                                getDrmLicense(context, bookId)
                            } else {
                                downloadSampleBook(context, bookId)
                            }
                        } else {
                            view?.hideIndicator()
                            view?.showMessageError(result?.messages.toString())
                        }
                    }
                ) { })
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    private fun getDrmLicense(context: Context?, productId: Int?) {
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.getDrmLicense(Const.BEARER, productId)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWDrmLicenseResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.getDrmLicenseSuccess(result)
                            SWBookCacheManager.writeKey(result.data?.decryptionKey.toString(), Const.FILE_PRIVATE_KEY, bookId)
                            SWBookCacheManager.writeKey(result.data?.encryptionKey.toString(), Const.FILE_PUBLIC_KEY, bookId)
                            SWBookCacheManager.writeKey(bookDetail?.updatedOnUtc, Const.FILE_LOG, bookId)
                        } else {
                            view?.hideIndicator()
                            view?.showMessageError(result?.messages.toString())
                        }
                    }
                ) { })
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    fun downloadBook(context: Context?, productId: Int?) {
        if (NetworkUtil.isNetworkConnected(context)) {
//            view?.showIndicator()
            disposable.add(RetrofitClient.client!!.downloadBook(Const.BEARER, productId)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: Response<ResponseBody>? ->
                        if (result?.isSuccessful == true) {
                            view?.hideIndicator()
                            view?.downloadBookSuccess(result)
                            SWBookCacheManager.writeBookToDisk(result.body(), bookId)
                            Log.d("TAG", "downloadBook: $result")
                        } else {
                            view?.hideIndicator()
                            view?.showMessageError(result?.message().toString())
                            Log.d("TAG", "downloadBook: ${result?.message().toString()}")
                        }
                    }
                ) {
                    Log.d("TAG", "downloadBook: $it")
                    view?.hideIndicator()
                })
        } else {
            view?.showNetworkError()
        }
    }

    fun downloadSampleBook(context: Context?, productId: Int?) {
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.downloadSampleBook(Const.BEARER, productId)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: Response<ResponseBody>? ->
                        if (result?.isSuccessful == true) {
                            view?.hideIndicator()
                            view?.downloadSampleBookSuccess(result)
                            SWBookCacheManager.writeSampleToDisk(result.body(), bookId)
                            Log.d("TAG", "getDrmLicense: $result")
                        } else {
                            view?.hideIndicator()
                            view?.showMessageError(result?.message().toString())
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

    override fun attachView(view: SWReadingNowInterface?) {
        this.view = view
    }

}