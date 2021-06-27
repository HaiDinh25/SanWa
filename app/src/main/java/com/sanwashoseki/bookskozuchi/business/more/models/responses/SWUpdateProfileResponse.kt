package com.sanwashoseki.bookskozuchi.business.more.models.responses

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWUpdateProfileResponse : Entity(), Serializable {
    var success: Boolean? = null
    var data: SWProfileResponse.Data? = null
    var messages: String? = null
    var total: Int = 0
}