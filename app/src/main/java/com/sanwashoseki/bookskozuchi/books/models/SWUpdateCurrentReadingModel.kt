package com.sanwashoseki.bookskozuchi.books.models

import com.sanwashoseki.bookskozuchi.models.Entity
import retrofit2.http.Field
import java.io.Serializable

class SWUpdateCurrentReadingModel : Entity(), Serializable {
    var totalPage: Int? = null
    var currentPage: Int? = null
    var location: Int? = null
    var paragraph: Int? = null
    var chapter: Int? = null
    var isReadingNow: Boolean? = null
}