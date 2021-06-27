package com.sanwashoseki.bookskozuchi.business.filter.models.responses

import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookInfoResponse
import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWFilterBookResponse : Entity(), Serializable {
    var success: Boolean? = null
    var data: List<SWBookInfoResponse>? = null
    var messages: String? = null
    var total: Int? = null
}