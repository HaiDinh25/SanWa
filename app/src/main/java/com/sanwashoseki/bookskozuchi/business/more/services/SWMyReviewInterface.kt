package com.sanwashoseki.bookskozuchi.business.more.services

import com.sanwashoseki.bookskozuchi.business.authentication.register.models.responses.SWRegisterResponse
import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWMyReviewResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWMyReviewInterface : IRefreshable {

    fun getMyReviewSuccess(result: SWMyReviewResponse?)

    fun deleteReviewSuccess(result: SWRegisterResponse?)

    fun showMessageError(msg: String)
}