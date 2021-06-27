package com.sanwashoseki.bookskozuchi.business.notifications.models.responses

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWSetStatusNotification: Entity(), Serializable {
    var code: Int? = null
    var success: Boolean? = null
    var data: Data? = null
    var messages: String? = null
    var errors: String? = null

    inner class Data: Entity(), Serializable {
        var entityName: String? = null
        var entityId: Int? = null
    }
}