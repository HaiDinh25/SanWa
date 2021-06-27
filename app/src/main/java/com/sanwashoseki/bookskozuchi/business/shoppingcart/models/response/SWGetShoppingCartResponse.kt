package com.sanwashoseki.bookskozuchi.business.shoppingcart.models.response

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWGetShoppingCartResponse : Entity(), Serializable {
    var success: Boolean? = null
    var data: Data? = null
    var messages: String? = null
    var total: Int? = 0

    inner class Data : Entity(), Serializable {
        var items: List<Item>? = null

        inner class Item : Entity(), Serializable {
            var shoppingCartItemId: Int? = null
            var pictureMobileModel: PictureMobileModel? = null
            var authorName: String? = null
            var price: Double? = null
            var ratingSum: Int? = null
            var audioReading: Boolean? = null
            var bookName: String? = null
            var productId: Int? = null
            var isAvaiable: Boolean? = null
            var published: Boolean? = null
            var deleted: Boolean? = null

            inner class PictureMobileModel : Entity(), Serializable {
                var imageUrl: String? = null
            }
        }
    }
}