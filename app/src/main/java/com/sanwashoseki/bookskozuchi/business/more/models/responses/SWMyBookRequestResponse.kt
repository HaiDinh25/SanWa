package com.sanwashoseki.bookskozuchi.business.more.models.responses

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWMyBookRequestResponse(
    var bookName: String,
    var code: String,
    var date: String,
    var status: String,
    var author: String
) : Entity(), Serializable