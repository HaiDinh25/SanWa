package com.sanwashoseki.bookskozuchi.business.authentication.forgotpassword.models.requests

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWConfirmEmailRequest(
    var email: String?,
    var clientId: String?,
    var clientSecret: String?
) : Entity(), Serializable