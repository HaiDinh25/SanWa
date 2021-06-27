package com.sanwashoseki.bookskozuchi.business.ebookstore.services

import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWAddShoppingCartWishListResponse
import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.response.SWRemoveShoppingCartResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWAddShoppingCartWishListInterface: IRefreshable {

    fun addShoppingCartWishListSuccess(result: SWAddShoppingCartWishListResponse)

    fun unWishListSuccess(result: SWRemoveShoppingCartResponse)

    fun showMessageError(msg: String)
}