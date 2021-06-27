package com.sanwashoseki.bookskozuchi.business.more.models.requests

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWSendContactRequest(
    var subject: String?,
    var body: String?,
    var name: String?,
    var email: String?,
    var languageId: Int?
) : Entity(), Serializable