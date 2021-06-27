package com.sanwashoseki.bookskozuchi.business.ebookrequest.services

import com.sanwashoseki.bookskozuchi.business.ebookrequest.models.responses.SWRequestBookLibraryResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWAddShoppingCartWishListResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWRequestBookInterface: IRefreshable {

    fun getRequestSuccess(result: SWRequestBookLibraryResponse)

    fun getMyRequestSuccess(result: SWRequestBookLibraryResponse)

    fun deleteRequestSuccess(result: SWAddShoppingCartWishListResponse)

    fun showMessageError(msg: String)
}