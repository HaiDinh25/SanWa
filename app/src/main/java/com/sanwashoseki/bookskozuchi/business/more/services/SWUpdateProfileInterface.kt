package com.sanwashoseki.bookskozuchi.business.more.services

/*
 * Created by Dinh.Van.Hai on 11/12/2020
 * Mobile 0931670595
*/

import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWUpdateProfileResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWUpdateProfileInterface: IRefreshable {

    fun updateSuccess(result: SWUpdateProfileResponse)

    fun updateButton(isValid: Boolean)

    fun showMessageError(msg: String)
}