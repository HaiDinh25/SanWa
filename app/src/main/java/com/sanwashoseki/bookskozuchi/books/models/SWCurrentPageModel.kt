package com.sanwashoseki.bookskozuchi.books.models

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWCurrentPageModel: Entity(), Serializable {
    var productId: Int? = null
    var readStatus: Int? = null
    var isReadingNow: Boolean? = null
    var currentPage: Int? = null
    var totalPage: Int? = null
    var location: Int? = null
}