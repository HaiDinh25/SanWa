package com.sanwashoseki.bookskozuchi.business.more.services

/*
 * Created by Dinh.Van.Hai on 16/11/2020
 * Mobile 0931670595
*/

import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWChangePasswordInterface: IRefreshable {

    fun changeSuccess()

    fun showMessageError(msg: String)

    fun updateButton(valid: Boolean)
}