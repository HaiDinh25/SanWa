package com.sanwashoseki.bookskozuchi.business.more.services

import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWAddShoppingCartWishListResponse
import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWFAQsResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWHelpContactUsInterface : IRefreshable {

    fun getFQAsSuccess(result: SWFAQsResponse)

    fun sendContactSuccess(result: SWAddShoppingCartWishListResponse)

    fun showMessageError(msg: String)
}