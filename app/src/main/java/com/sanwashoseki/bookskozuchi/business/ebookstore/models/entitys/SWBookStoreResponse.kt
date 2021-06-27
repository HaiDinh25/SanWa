package com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWBookStoreResponse : Entity(), Serializable {
    var success: Boolean? = null
    var data: List<SWBookInfoResponse>? = null
    var messages: String? = null
    var total: Int? = 0
}