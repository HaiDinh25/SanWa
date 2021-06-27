package com.sanwashoseki.bookskozuchi.business.authentication.register.models.requests

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWRegisterRequest(
    var email: String?,
    var firstName: String?,
    var lastName: String?,
    var password: String?,
    var confirmPassword: String?,
    var deviceId: String?,
    var firebaseToken: String?,
    var clientId: String?,
    var clientSecret: String?
) : Entity(), Serializable