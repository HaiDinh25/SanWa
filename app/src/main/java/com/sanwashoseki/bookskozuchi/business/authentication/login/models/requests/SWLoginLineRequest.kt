package com.sanwashoseki.bookskozuchi.business.authentication.login.models.requests

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWLoginLineRequest(
    var accessToken: String?,
    var idToken: String?,
    var deviceId: String?,
    var firebaseToken: String?,
    var clientId: String?,
    var clientSecret: String?
) : Entity(), Serializable