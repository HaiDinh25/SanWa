package com.sanwashoseki.bookskozuchi.business.more.models.requests

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWUpdateProfileRequest(
    var firstName: String?,
    var lastName: String?,
    var phoneNumber: String?,
    var address: String?,
    var gender: String?,
    var birthday: String?
) : Entity(), Serializable