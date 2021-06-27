package com.sanwashoseki.bookskozuchi.business.ebookstore.services

import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWBookDetailResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWBookDetailInterface: IRefreshable {

    fun getBookDetailSuccess(result: SWBookDetailResponse)

    fun showMessageError(msg: String)
}