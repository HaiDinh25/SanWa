package com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses

import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookInfoResponse
import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWChildCategoriesBookResponse  : Entity(), Serializable {
    var success: Boolean? = null
    var data: Data? = null
    var messages: String? = null
    var total: Int? = 0

    inner class Data : Entity(), Serializable {
        var id: Int? = null
        var name: String? = null
        var subCategories: List<SubCategories>? = null
        var products: List<SWBookInfoResponse>? = null

        inner class SubCategories : Entity(), Serializable {
            var name: String? = null
            var id: Int? = null
            var products: List<SWBookInfoResponse>? = null
        }
    }
}