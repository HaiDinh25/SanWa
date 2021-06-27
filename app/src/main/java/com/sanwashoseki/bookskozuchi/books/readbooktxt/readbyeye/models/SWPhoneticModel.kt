package com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.models

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWPhoneticModel : Entity(), Serializable {
    var customerId: Int? = null
    var vocabulary: String? = null
    var meaning: String? = null
    var pronounce: String? = null
    var ipa: String? = null
    var productId: Int? = null
    var id: Int? = null
    var entityCacheKey: String? = null
}