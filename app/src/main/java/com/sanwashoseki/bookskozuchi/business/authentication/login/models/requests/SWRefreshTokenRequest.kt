package com.sanwashoseki.bookskozuchi.business.authentication.login.models.requests

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWRefreshTokenRequest(
    var refreshToken: String?
) : Entity(), Serializable {
}