package com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.utils

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

data class SWCommonResponse<Any> (
    var code: Int? = null,
    var success: Boolean? = null,
    var data: Any? = null,
    var messages: String? = null,
    var total: Int? = null,
    var errors: Any? = null
): Entity(), Serializable