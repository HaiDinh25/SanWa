package com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWAddShoppingCartWishListResponse : Entity(), Serializable {
    var success: Boolean? = null
    var data: String? = null
    var messages: String? = null
    var total: Int? = 0
}