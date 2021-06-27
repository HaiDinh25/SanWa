package com.sanwashoseki.bookskozuchi.business.authentication.logout.services

/*
 * Created by Dinh.Van.Hai on 16/11/2020
 * Mobile 0931670595
*/

import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWLogoutInterface: IRefreshable {

    fun logoutSuccess()

    fun showMessageError(e: String)
}