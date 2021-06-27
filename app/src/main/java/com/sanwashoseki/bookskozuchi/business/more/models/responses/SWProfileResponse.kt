package com.sanwashoseki.bookskozuchi.business.more.models.responses

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWProfileResponse : Entity(), Serializable {
    var success: Boolean? = null
    var data: Data? = null
    var messages: String? = null
    var total: Int? = 0

    inner class Data : Entity(), Serializable {
        var firstName: String? = null
        var lastName: String? = null
        var nameFormated: String? = null
        var email: String? = null
        var phoneNumber: String? = null
        var address: String? = null
        var gender: String? = null
        var birthday: String? = null
        var avatarUrl: String? = null
    }
}