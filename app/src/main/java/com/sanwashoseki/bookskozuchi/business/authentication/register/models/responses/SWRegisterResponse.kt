package com.sanwashoseki.bookskozuchi.business.authentication.register.models.responses

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWRegisterResponse : Entity(), Serializable {
    var success: Boolean? = null
    var messages: String? = null
    var data: String? = null
    var total = 0
}
