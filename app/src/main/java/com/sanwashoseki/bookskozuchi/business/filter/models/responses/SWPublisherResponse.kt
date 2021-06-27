package com.sanwashoseki.bookskozuchi.business.filter.models.responses

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWPublisherResponse : Entity(), Serializable {
    var success: Boolean? = null
    var data: List<Data>? = null
    var messages: String? = null
    var total: Int? = 0

    inner class Data : Entity(), Serializable {
        var id: Int? = null
        var vendorName: String? = null
    }
}