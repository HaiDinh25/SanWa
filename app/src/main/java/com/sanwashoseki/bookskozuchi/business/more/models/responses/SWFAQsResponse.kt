package com.sanwashoseki.bookskozuchi.business.more.models.responses

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWFAQsResponse : Entity(), Serializable {
    var success: Boolean? = null
    var data: Data? = null
    var messages: String? = null
    var total: Int? = null

    inner class Data : Entity(), Serializable {
        var readingBookModel: ReadingBookModel? = null
        var purchasedBookModel: PurchasedBookModel? = null

        inner class ReadingBookModel : Entity(), Serializable {
            var readingBookString: String? = null
            var readingBooks: List<Content>? = null
        }

        inner class PurchasedBookModel : Entity(), Serializable {
            var purchasedBookString: String? = null
            var purchasedBooks: List<Content>? = null
        }

        inner class Content : Entity(), Serializable {
            var title: String? = null
            var question: String? = null
        }
    }
}