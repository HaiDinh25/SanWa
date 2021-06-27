package com.sanwashoseki.bookskozuchi.business.more.services

import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWAboutUsResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWAboutUsInterface : IRefreshable {

    fun getAboutUsSuccess(result: SWAboutUsResponse)

    fun showMessageError(msg: String)
}