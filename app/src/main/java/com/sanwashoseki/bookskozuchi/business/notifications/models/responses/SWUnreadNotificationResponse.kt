package com.sanwashoseki.bookskozuchi.business.notifications.models.responses

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWUnreadNotificationResponse : Entity(), Serializable {
    var code: Int? = null
    var success: Boolean? = null
    var data: Int? = null
    var messages: String? = null
    var total: Int? = null
}