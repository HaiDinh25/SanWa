package com.sanwashoseki.bookskozuchi.business.more.models.responses

import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookInfoResponse
import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWPurchaseHistoryResponse : Entity(), Serializable {
    var success: Boolean? = null
    var data: List<Data>? = null
    var messages: String? = null
    var total: Int? = null

    inner class Data : Entity(), Serializable {
        var orderId: Int? = null
        var customOrderNumber: String? = null
        var createdOn: String? = null
        var paymentMethod: String? = null
        var orderStatus: String? = null
        var amount: String? = null
        var products: List<SWBookInfoResponse>? = null
    }
}

