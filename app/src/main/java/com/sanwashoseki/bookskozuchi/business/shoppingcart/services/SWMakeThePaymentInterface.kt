package com.sanwashoseki.bookskozuchi.business.shoppingcart.services

import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWAddShoppingCartWishListResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWMakeThePaymentInterface : IRefreshable {

    fun makePaymentSuccess(result: SWAddShoppingCartWishListResponse?)

    fun updateButton(isValid: Boolean)

    fun showMessageError(msg: String)
}