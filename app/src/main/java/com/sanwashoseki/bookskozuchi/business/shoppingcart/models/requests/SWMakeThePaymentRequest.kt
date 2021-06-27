package com.sanwashoseki.bookskozuchi.business.shoppingcart.models.requests

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWMakeThePaymentRequest(
    var creditCardName: String?,
    var creditCardNumber: String?,
    var creditCardExpireMonth: String?,
    var creditCardExpireYear: String?,
    var creditCardCvv2: String?,
    var shoppingCartItemIds: ArrayList<Int?>,
): Entity(), Serializable