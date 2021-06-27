package com.sanwashoseki.bookskozuchi.business.more.services

/*
 * Created by Dinh.Van.Hai on 14/11/2020
 * Mobile 0931670595
*/

import android.content.Context
import android.util.Log
import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWUploadAvatarResponse
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.MVP
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody

class SWUpdateAvatarPresenter : MVP<SWUpdateAvatarInterface?> {

    var view: SWUpdateAvatarInterface? = null
    var disposable = CompositeDisposable()

    fun updateAvatar(context: Context?, uploadedFile: MultipartBody.Part?) {
        view?.showIndicator()
        if (NetworkUtil.isNetworkConnected(context)) {
            disposable.add(RetrofitClient.client?.uploadAvatar(Const.BEARER, uploadedFile)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWUploadAvatarResponse? ->
                        Log.d("TAG", "updateAvatar: " + result?.success)
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.updateAvatarSuccess(result)
                        } else {
                            view?.hideIndicator()
                            view?.showMsgUpdateAvatarError(result?.messages.toString())
                        }
                    }
                ) {
                    Log.d("TAG", "updateAvatar: $it")
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

    override fun attachView(view: SWUpdateAvatarInterface?) {
        this.view = view
    }

}