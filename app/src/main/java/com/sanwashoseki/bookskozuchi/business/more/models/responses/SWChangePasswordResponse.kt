package com.sanwashoseki.bookskozuchi.business.more.models.responses

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWChangePasswordResponse : Entity(), Serializable {
    var success: Boolean? = null
    var messages: String? = null
    var total: Int? = 0
}