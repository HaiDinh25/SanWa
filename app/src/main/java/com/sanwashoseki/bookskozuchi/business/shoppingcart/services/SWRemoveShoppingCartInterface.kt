package com.sanwashoseki.bookskozuchi.business.shoppingcart.services

import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.response.SWRemoveShoppingCartResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWRemoveShoppingCartInterface : IRefreshable {

    fun removeItemSuccess(result: SWRemoveShoppingCartResponse?)

    fun removeAllSuccess(result: SWRemoveShoppingCartResponse?)

    fun showMessageError(msg: String)
}