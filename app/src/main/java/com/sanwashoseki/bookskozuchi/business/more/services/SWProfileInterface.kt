package com.sanwashoseki.bookskozuchi.business.more.services

import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWProfileResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWProfileInterface : IRefreshable {

    fun getInfoSuccess(result: SWProfileResponse)

    fun showMessageInfoError(msg: String)

    fun showIndicateGetInfo()
}