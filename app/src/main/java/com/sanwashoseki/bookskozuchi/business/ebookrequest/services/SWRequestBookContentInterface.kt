package com.sanwashoseki.bookskozuchi.business.ebookrequest.services

import com.sanwashoseki.bookskozuchi.business.ebookrequest.models.responses.SWRequestBookContentLibraryResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWAddShoppingCartWishListResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWRequestBookContentInterface: IRefreshable {

    fun getRequestContentSuccess(result: SWRequestBookContentLibraryResponse)

    fun replyRequestSuccess(result: SWAddShoppingCartWishListResponse)

    fun deleteRequestSuccess(result: SWAddShoppingCartWishListResponse)

    fun showMessageError(msg: String)
}