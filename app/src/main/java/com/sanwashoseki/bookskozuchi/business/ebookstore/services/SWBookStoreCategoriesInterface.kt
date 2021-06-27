package com.sanwashoseki.bookskozuchi.business.ebookstore.services

/*
 * Created by Dinh.Van.Hai on 16/11/2020
 * Mobile 0931670595
*/

import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWCategoriesResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWBookStoreCategoriesInterface: IRefreshable {

    fun getCategoriesSuccess(result: SWCategoriesResponse)

    fun showMessageError(msg: String)
}