package com.sanwashoseki.bookskozuchi.business.authentication.logout.models.requests

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWLogoutRequest(
    var deviceId: String?,
    var clientId: String?,
    var secrect: String?
) : Entity(), Serializable