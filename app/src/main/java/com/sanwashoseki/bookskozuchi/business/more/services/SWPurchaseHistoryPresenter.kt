package com.sanwashoseki.bookskozuchi.business.more.services

import android.content.Context
import android.util.Log
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants
import com.sanwashoseki.bookskozuchi.business.ebookreader.models.response.SWDrmLicenseResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWBookDetailResponse
import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWPurchaseHistoryResponse
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

class SWPurchaseHistoryPresenter : MVP<SWPurchaseHistoryInterface> {

    var view: SWPurchaseHistoryInterface? = null
    var disposable = CompositeDisposable()
    var bookDetail: SWBookDetailResponse.Data? = null
    var bookId: Int? = null

    fun getPurchaseHistory(context: Context?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client?.getPurchaseHistory(Const.BEARER, 0, SWConstants.MAX_VALUE)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                    { result: SWPurchaseHistoryResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.getPurchaseHistorySuccess(result)
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

    fun getBookDetail(context: Context?, isReadNow: Boolean?) {
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client!!.getDetailBook(Const.BEARER, bookId)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWBookDetailResponse? ->
                        if (result?.success == true) {
                            view?.getBookDetailSuccess(result, isReadNow)
                            if (isReadNow == true) {
                                SWBookCacheManager.writeKey(result.toString(), Const.FILE_BOOK_DETAIL, bookId)
                                getDrmLicense(context, bookId)
                            }
                        } else {
                            view?.showMessageError(result?.messages.toString())
                        }
                    }
                ) { })
        } else {
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
            view?.showNetworkError()
        }
    }

    fun downloadBook(context: Context?, productId: Int?) {
        if (NetworkUtil.isNetworkConnected(context)) {
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

    override fun attachView(view: SWPurchaseHistoryInterface) {
        this.view = view
    }

    override fun detachView() {
        view = null
        disposable.clear()
    }
}