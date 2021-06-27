package com.sanwashoseki.bookskozuchi.business.filter.services

import com.sanwashoseki.bookskozuchi.business.filter.models.responses.SWFilterBookResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWFilterBookInterface : IRefreshable {

    fun filterSuccess(result: SWFilterBookResponse)

    fun showMessageError(msg: String)
}