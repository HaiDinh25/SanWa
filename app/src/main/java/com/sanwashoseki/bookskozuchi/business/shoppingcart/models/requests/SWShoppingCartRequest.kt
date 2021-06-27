package com.sanwashoseki.bookskozuchi.business.shoppingcart.models.requests

import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.response.SWGetShoppingCartResponse
import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWShoppingCartRequest(
    var book: SWGetShoppingCartResponse.Data.Item
) : Entity(), Serializable