package com.sanwashoseki.bookskozuchi.business.ebookrequest.services

import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWAddShoppingCartWishListResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWAddRequestBookInterface: IRefreshable {

    fun addRequestSuccess(result: SWAddShoppingCartWishListResponse)

    fun showMessageError(msg: String)
}