package com.sanwashoseki.bookskozuchi.business.more.services

import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWAddShoppingCartWishListResponse
import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWPushNotificationResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWPushNotificationInterface : IRefreshable {

    fun getStatusNotificationSuccess(result: SWPushNotificationResponse)

    fun pushStatusNotificationSuccess(result: SWAddShoppingCartWishListResponse)

    fun showMessageError(msg: String)
}