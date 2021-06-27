package com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWCategoriesResponse : Entity(), Serializable {
    var success: Boolean? = null
    var data: List<Data>? = null

    inner class Data : Entity(), Serializable {
        var name: String? = null
        var subCategories: List<SubCategories>? = null
        var iconUrl: String? = null
        var id: Int? = null

        inner class SubCategories : Entity(), Serializable {
            var name: String? = null
            var seName: String? = null
            var id: Int? = null
        }
    }
    var messages: String? = null
    var total: Int? = 0
}