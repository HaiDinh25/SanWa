package com.sanwashoseki.bookskozuchi.business.authentication.register.services

import com.sanwashoseki.bookskozuchi.business.authentication.login.models.responses.SWLoginResponse
import com.sanwashoseki.bookskozuchi.business.authentication.register.models.responses.SWRegisterResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWRegisterInterface :IRefreshable {

    fun registerSuccess(result: SWRegisterResponse?)

    fun loginSuccess(result: SWLoginResponse?)

    fun showMessageError(msg: String)

    fun updateButton(valid: Boolean)
}