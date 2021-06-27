package com.sanwashoseki.bookskozuchi.business.more.models.responses

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWPushNotificationResponse : Entity(), Serializable {
    var success: Boolean? = null
    var data: Data? = null
    var messages: String? = null
    var total: Int? = null

    inner class Data : Entity(), Serializable {
        var isEmailNotificationEnable: Boolean? = null
        var isMobileAppNotificationEnable: Boolean? = null
    }
}