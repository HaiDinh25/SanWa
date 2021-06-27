package com.sanwashoseki.bookskozuchi.business.notifications.services

import com.sanwashoseki.bookskozuchi.business.notifications.models.responses.SWNotificationResponse
import com.sanwashoseki.bookskozuchi.business.notifications.models.responses.SWSetStatusNotification
import com.sanwashoseki.bookskozuchi.business.notifications.models.responses.SWUnreadNotificationResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWNotificationsInterface : IRefreshable {

    fun getUnreadNotificationsSuccess(result: SWUnreadNotificationResponse?)

    fun getNotificationsSuccess(result: SWNotificationResponse?)

    fun setStatusSuccess(result: SWSetStatusNotification?)

    fun showMessageError(msg: String)

    fun expiredToken()
}