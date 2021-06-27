package com.sanwashoseki.bookskozuchi.business.ebookreader.services

import com.sanwashoseki.bookskozuchi.business.ebookreader.models.response.SWDrmLicenseResponse
import com.sanwashoseki.bookskozuchi.business.ebookreader.models.response.SWReadingNowResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWBookDetailResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable
import okhttp3.ResponseBody
import retrofit2.Response

interface SWReadingNowInterface: IRefreshable {

    fun getReadingNowSuccess(result: SWReadingNowResponse)

    fun getBookDetailSuccess(result: SWBookDetailResponse?)

    fun getDrmLicenseSuccess(result: SWDrmLicenseResponse)

    fun downloadBookSuccess(result: Response<ResponseBody>)

    fun downloadSampleBookSuccess(result: Response<ResponseBody>)

    fun showMessageError(msg: String)
}