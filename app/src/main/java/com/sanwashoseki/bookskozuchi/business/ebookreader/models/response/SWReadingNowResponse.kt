package com.sanwashoseki.bookskozuchi.business.ebookreader.models.response

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWReadingNowResponse : Entity(), Serializable {
    var code: Int? = null
    var success: Boolean? = null
    var data: ArrayList<Data>? = null
    var messages: String? = null
    var total: Int? = null
    var errors: String? = null

    inner class Data : Entity(), Serializable {
        var lastActivityAt: String? = null
        var lastActivityAtFormated: String? = null
        var defaultPictureModel: DefaultPictureModel? = null
        var isSample: Boolean? = null
        var id: Int? = null
        var name: String? = null
        var shortDescription: String? = null
        var audioReading: Boolean? = null
        var vendor: Vendor? = null
        var ratingSum: Float? = null
        var price: String? = null
        var authors: List<Authors>? = null

        inner class DefaultPictureModel : Entity(), Serializable {
            var imageUrl: String? = null
        }

        inner class Vendor : Entity(), Serializable {
            var name: String? = null
            var id: Int? = null
        }

        inner class Authors : Entity(), Serializable {
            var name: String? = null
        }
    }
}