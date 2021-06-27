package com.sanwashoseki.bookskozuchi.business.more.services

import com.sanwashoseki.bookskozuchi.business.filter.models.responses.SWFilterBookResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWSameCategoryBookInterface : IRefreshable {

    fun getSameCategoryBookSuccess(result: SWFilterBookResponse)

    fun getSamePublisherBookSuccess(result: SWFilterBookResponse)

    fun showMessageError(msg: String)
}