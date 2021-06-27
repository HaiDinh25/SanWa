package com.sanwashoseki.bookskozuchi.business.ebookreader.services

import com.sanwashoseki.bookskozuchi.business.ebookreader.models.response.SWDrmLicenseResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable
import okhttp3.ResponseBody
import retrofit2.Response

interface SWDownloadBookInterface: IRefreshable {

    fun getDrmLicenseSuccess(result: SWDrmLicenseResponse)

    fun downloadBookSuccess(result: Response<ResponseBody>)

    fun downloadSampleBookSuccess(result: Response<ResponseBody>)

    fun showDrmMessageError(msg: String)

    fun showDownloadMessageError(msg: String)

    fun showDownloadSampleMessageError(msg: String)
}