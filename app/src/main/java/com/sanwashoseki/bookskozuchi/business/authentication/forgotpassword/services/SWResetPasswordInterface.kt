package com.sanwashoseki.bookskozuchi.business.authentication.forgotpassword.services

import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWResetPasswordInterface :IRefreshable {

    fun resetSuccess()

    fun showMessageError(msg: String)

    fun updateButton(valid: Boolean)
}