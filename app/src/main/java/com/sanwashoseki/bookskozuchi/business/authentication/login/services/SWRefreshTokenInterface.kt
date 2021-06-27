package com.sanwashoseki.bookskozuchi.business.authentication.login.services

/*
 * Created by Dinh.Van.Hai on 16/11/2020
 * Mobile 0931670595
*/

import com.sanwashoseki.bookskozuchi.business.authentication.login.models.responses.SWLoginResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWAddShoppingCartWishListResponse
import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.response.SWGetShoppingCartResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWRefreshTokenInterface: IRefreshable {

    fun refreshSuccess(result: SWLoginResponse?)

    fun addShoppingCartSuccess(result: SWAddShoppingCartWishListResponse?)

    fun getShoppingCartSuccess(result: SWGetShoppingCartResponse)

    fun showMessageError(msg: String)
}