package com.sanwashoseki.bookskozuchi.business.notifications.models.responses

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWNotificationResponse : Entity(), Serializable {
    var code: Int? = null
    var success: Boolean? = null
    var data: List<Data>? = null
    var messages: String? = null
    var total: Int? = null

    inner class Data : Entity(), Serializable {
        var title: String? = null
        var body: String? = null
        var id: Int? = null
        var entityName: String? = null
        var entityId: Int? = null
        var createdDateFormated: String? = null
        var isRead: Boolean? = null
    }
}