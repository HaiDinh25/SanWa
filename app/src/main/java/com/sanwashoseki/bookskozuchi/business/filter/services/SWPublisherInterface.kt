package com.sanwashoseki.bookskozuchi.business.filter.services

import com.sanwashoseki.bookskozuchi.business.filter.models.responses.SWPublisherResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWPublisherInterface : IRefreshable {

    fun getPublisherSuccess(result: SWPublisherResponse)

    fun showMessageError(msg: String)

    fun validButton(isValid: Boolean)
}