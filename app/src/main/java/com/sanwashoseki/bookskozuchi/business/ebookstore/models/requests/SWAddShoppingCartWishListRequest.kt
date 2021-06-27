package com.sanwashoseki.bookskozuchi.business.ebookstore.models.requests

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWAddShoppingCartWishListRequest(
    private var productId: Int?,
    private var shoppingCartTypeId: Int?
) : Entity(), Serializable