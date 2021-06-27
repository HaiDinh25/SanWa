package com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.models

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

data class SWPhoneticRequestModel(
    var productId: Int? = null,
    var vocabulary: String? = null,
    var meaning: String? = null,
    var pronounce: String? = null,
    var iPA: String? = null,
    var customerId: Int? = null,
    var id: Int? = null
): Entity(), Serializable