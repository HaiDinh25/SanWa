package com.sanwashoseki.bookskozuchi.business.ebooklibrary.services

import com.sanwashoseki.bookskozuchi.business.ebooklibrary.models.responses.SWMyBookResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWMyBookInterface: IRefreshable {

    fun getMyBookSuccess(result: SWMyBookResponse)

    fun loadMoreSuccess(result: SWMyBookResponse)

    fun showMessageError(msg: String)
}