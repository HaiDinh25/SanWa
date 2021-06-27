package com.sanwashoseki.bookskozuchi.business.ebookrequest.models.responses

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWRequestBookLibraryResponse : Entity(), Serializable {
    var success: Boolean? = null
    var data: List<Data>? = null
    var messages: String? = null
    var total: Int? = null

    inner class Data : Entity(), Serializable {
        var id: Int? = null
        var name: String? = null
        var createdOnUtcFormated: String? = null
        var authorName: String? = null
        var body: String? = null
        var status: String? = null
        var statusId: Int? = null
        var numPosts: Int? = null
        var note: String? = null
        var customer: Customer? = null
        var bookRequestPosts: List<Data>? = null
        var pictures: List<Pictures>? = null

        inner class Customer : Entity(), Serializable {
            var nameFormated: String? = null
            var email: String? = null
            var avatarUrl: String? = null
        }

        inner class Pictures : Entity(), Serializable {
            var imageUrl: String? = null
            var fullSizeImageUrl: String? = null
        }
    }
}