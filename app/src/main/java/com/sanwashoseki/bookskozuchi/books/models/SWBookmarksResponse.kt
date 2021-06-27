package com.sanwashoseki.bookskozuchi.books.models

import android.os.Parcel
import android.os.Parcelable
import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWBookmarksResponse : Entity(), Serializable {
    var code: Int? = null
    var success: Boolean? = null
    var data: List<Data>? = null
    var messages: String? = null
    var total: Int? = null
    var errors: String? = null

    public class Data() : Entity(), Serializable {
        var productId: Int? = null
        var customerId: Int? = null
        var note: String? = null
        var subtitle: String? = null
        var chapterIndex: Int? = null
        var paragraphIndex: Int? = null
        var pageIndex: Int? = null
        var locationIndex: Int? = null
        var createdDate: String? = null
        var updatedDate: Any? = null
        var id: Int? = null
    }
}