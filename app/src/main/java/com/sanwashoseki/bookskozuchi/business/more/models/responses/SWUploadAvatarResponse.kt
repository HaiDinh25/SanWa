package com.sanwashoseki.bookskozuchi.business.more.models.responses

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWUploadAvatarResponse : Entity(), Serializable {
    var success: Boolean? = null
    var data: String? = null
    var messages: String? = null
    var total: Int = 0
}