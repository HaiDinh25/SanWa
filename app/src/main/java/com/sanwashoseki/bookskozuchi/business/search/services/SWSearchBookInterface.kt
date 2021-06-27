package com.sanwashoseki.bookskozuchi.business.search.services

import com.sanwashoseki.bookskozuchi.business.search.models.responses.SWSearchBookResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWSearchBookInterface : IRefreshable {

    fun searchSuccess(result: SWSearchBookResponse)
    fun onLoadMoreSuccess(result: SWSearchBookResponse?)
    fun showMessageError(msg: String)
}