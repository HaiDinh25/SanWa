package com.sanwashoseki.bookskozuchi.business.ebooklibrary.services

import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.response.SWGetShoppingCartResponse
import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.response.SWRemoveShoppingCartResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWWishListInterface: IRefreshable {

    fun getWishListSuccess(result: SWGetShoppingCartResponse)

    fun unWishListSuccess(result: SWRemoveShoppingCartResponse)

    fun showMessageError(msg: String)
}