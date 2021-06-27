package com.sanwashoseki.bookskozuchi.business.search.models.responses

import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookInfoResponse
import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWSearchBookResponse : Entity(), Serializable {
    var success: Boolean? = null
    var data: List<SWBookInfoResponse>? = null
    var messages: String? = null
    var total: Int? = 0
}