package com.sanwashoseki.bookskozuchi.business.authentication.forgotpassword.services

import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWConfirmCodeInterface : IRefreshable {

    fun confirmSuccess()

    fun showMessageError(msg: String)

    fun updateButton(valid: Boolean)
}