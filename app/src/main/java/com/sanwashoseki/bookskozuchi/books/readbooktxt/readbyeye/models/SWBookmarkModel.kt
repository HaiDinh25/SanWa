package com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.models

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

data class SWBookmarkModel(
    var productId: Int? = null,
    var customerId: Int? = null,
    var note: String? = null,
    var subtitle: String? = null,
    var chapterIndex: Int? = null,
    var paragraphIndex: Int? = null,
    var pageIndex: Int? = null,
    var locationIndex: Int? = null,
    var createdDate: String? = null,
    var updatedDate: Any? = null,
    var id: Int? = null,
) : Entity(), Serializable