package com.sanwashoseki.bookskozuchi.business.ebooklibrary.models.responses

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWMyBookResponse : Entity(), Serializable {
    var success: Boolean? = null
    var data: List<Data>? = null
    var messages: String? = null
    var total: Int? = null

    inner class Data : Entity(), Serializable {
        var id: Int? = null
        var defaultPictureModel: DefaultPictureModel? = null
        var name: String? = null
        var authors: List<Authors>? = null
        var price: Double? = null
        var ratingSum: Int? = null
        var audioReading: Boolean? = null

        inner class DefaultPictureModel: Entity(), Serializable {
            var imageUrl: String? = null
        }

        inner class Authors: Entity(), Serializable {
            var name: String? = null
        }
    }
}