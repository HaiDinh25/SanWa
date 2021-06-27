package com.sanwashoseki.bookskozuchi.books.models

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWBookReadHistoryResponse : Entity(), Serializable {
    var code: Int? = null
    var success: Boolean? = null
    var data: Data? = null
    var messages: String? = null

    inner class Data : Entity(), Serializable {
        var productId: Int? = null
        var readStatus: Int? = null
        var isReadingNow: Boolean? = null
        var currentPage: Int? = null
        var totalPage: Int? = null
        var location: Int? = null
    }
}