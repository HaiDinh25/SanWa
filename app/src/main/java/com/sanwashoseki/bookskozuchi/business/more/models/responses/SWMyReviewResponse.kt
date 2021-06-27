package com.sanwashoseki.bookskozuchi.business.more.models.responses

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWMyReviewResponse : Entity(), Serializable {
    var success: Boolean? = null
    var data: List<Data>? = null
    var messages: String? = null
    var total: Int? = null

    inner class Data : Entity(), Serializable {
        var bookName: String? = null
        var authorName: String? = null
        var rating: Float? = null
        var status: String? = null
        var createdOn: String? = null
        var reviewText: String? = null
        var isAudioBook: Boolean? = null
        var pictureModel: PictureModel? = null
        var productId: Int? = null
        var id: Int? = null

        inner class PictureModel : Entity(), Serializable {
            var imageUrl: String? = null
        }
    }
}