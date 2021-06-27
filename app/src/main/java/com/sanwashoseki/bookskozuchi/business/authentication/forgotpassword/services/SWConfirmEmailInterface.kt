package com.sanwashoseki.bookskozuchi.business.authentication.forgotpassword.services

import com.sanwashoseki.bookskozuchi.business.authentication.register.models.responses.SWRegisterResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWConfirmEmailInterface : IRefreshable {

    fun confirmSuccess(result: SWRegisterResponse?)

    fun showMessageError(msg: String)

    fun updateButton(valid: Boolean)
}