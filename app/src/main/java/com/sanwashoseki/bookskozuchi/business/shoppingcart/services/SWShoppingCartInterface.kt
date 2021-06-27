package com.sanwashoseki.bookskozuchi.business.shoppingcart.services

import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.response.SWGetShoppingCartResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWShoppingCartInterface : IRefreshable {

    fun getShoppingCardSuccess(result: SWGetShoppingCartResponse?)

    fun showMessageError(msg: String)

    fun validationButton(isValid: Boolean)
}