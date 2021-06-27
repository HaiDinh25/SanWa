package com.sanwashoseki.bookskozuchi.books.models

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWEditDictionaryResponse: Entity(), Serializable {
    var code: Int? = null
    var success: Boolean? = null
    var data: Data? = null
    var messages: String? = null
    var total: Int? = null
    var errors: String? = null

    inner class Data: Entity(), Serializable {

    }
}