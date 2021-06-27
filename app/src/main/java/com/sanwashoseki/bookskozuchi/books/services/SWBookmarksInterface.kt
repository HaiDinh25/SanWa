package com.sanwashoseki.bookskozuchi.books.services

/*
 * Created by Dinh.Van.Hai on 16/11/2020
 * Mobile 0931670595
*/

import com.sanwashoseki.bookskozuchi.books.models.SWBookmarksResponse
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.utils.SWCommonResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWBookmarksInterface: IRefreshable {

    fun getBookMarkSuccess(result: SWBookmarksResponse)

    fun deleteBookMarkSuccess(result: SWCommonResponse<Any>)

    fun showMessageError(msg: String)
}