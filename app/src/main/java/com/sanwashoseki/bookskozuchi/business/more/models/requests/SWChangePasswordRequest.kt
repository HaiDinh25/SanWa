package com.sanwashoseki.bookskozuchi.business.more.models.requests

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWChangePasswordRequest(
    var oldPassword: String?,
    var newPassword: String?,
    var confirmNewPassword: String?
) : Entity(), Serializable