package com.sanwashoseki.bookskozuchi.business.more.services

import com.sanwashoseki.bookskozuchi.business.ebookreader.models.response.SWDrmLicenseResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWBookDetailResponse
import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWPurchaseHistoryResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable
import okhttp3.ResponseBody
import retrofit2.Response

interface SWPurchaseHistoryInterface : IRefreshable {

    fun getPurchaseHistorySuccess(result: SWPurchaseHistoryResponse?)

    fun getBookDetailSuccess(result: SWBookDetailResponse?, isReadNow: Boolean?)

    fun getDrmLicenseSuccess(result: SWDrmLicenseResponse)

    fun downloadBookSuccess(result: Response<ResponseBody>)

    fun showMessageError(msg: String)
}