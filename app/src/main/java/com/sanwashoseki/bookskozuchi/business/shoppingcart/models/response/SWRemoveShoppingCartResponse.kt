package com.sanwashoseki.bookskozuchi.business.shoppingcart.models.response

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWRemoveShoppingCartResponse : Entity(), Serializable {
    var success: Boolean? = null
    var data: String? = null
    var messages: String? = null
    var total: Int? = 0
}