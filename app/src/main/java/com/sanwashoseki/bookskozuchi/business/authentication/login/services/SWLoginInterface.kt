package com.sanwashoseki.bookskozuchi.business.authentication.login.services

/*
 * Created by Dinh.Van.Hai on 16/11/2020
 * Mobile 0931670595
*/

import com.sanwashoseki.bookskozuchi.business.authentication.login.models.responses.SWLoginResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWLoginInterface: IRefreshable {

    fun loginSuccess(result: SWLoginResponse?)

    fun showMessageError(msg: String)

    fun updateButton(valid: Boolean)
}