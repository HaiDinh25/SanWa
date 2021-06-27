package com.sanwashoseki.bookskozuchi.business.ebooklibrary.services

import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWAddShoppingCartWishListResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWAddReviewInterface: IRefreshable {

    fun sendReviewSuccess(result: SWAddShoppingCartWishListResponse)

    fun showMessageError(msg: String)
}