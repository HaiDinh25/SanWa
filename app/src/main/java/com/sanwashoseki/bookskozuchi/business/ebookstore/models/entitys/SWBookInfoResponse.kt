package com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWBookInfoResponse : Entity(), Serializable {
    var id: Int? = null
    var productName: String? = null
    var authorName: String? = null
    var priceValue: String? = null
    var ratingSum: Float? = null
    var audioReading: Boolean? = null
    var publisherName: String? = null
    var fullDescription: String? = null
    var defaultPictureModel: DefaultPictureModel? = null
    var myProductReview: List<MyProductReview>? = null

    inner class DefaultPictureModel : Entity(), Serializable {
        var imageUrl: String? = null
    }

    inner class MyProductReview : Entity(), Serializable {
        var rating: Float? = null
        var reviewText: String? = null
    }
}