package com.sanwashoseki.bookskozuchi.business.authentication.login.models.entitys

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWLoginOutput(
    var name: String?,
    var email: String?
) : Entity(), Serializable