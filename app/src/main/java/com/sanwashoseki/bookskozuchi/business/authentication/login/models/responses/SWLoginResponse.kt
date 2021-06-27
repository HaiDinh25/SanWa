package com.sanwashoseki.bookskozuchi.business.authentication.login.models.responses

/*
 * Created by Dinh.Van.Hai on 16/11/2020
 * Mobile 0931670595
*/

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWLoginResponse: Entity(), Serializable {

    var success: Boolean? = null
    var messages: String? = null
    var data: Data? = null
    var total = 0

    inner class Data: Entity(), Serializable {
        var customer: Customer? = null
        var accessToken: AccessToken? = null
        var refreshToken: RefreshToken? = null

        inner class Customer : Entity(), Serializable {
            var fullName: String? = null
            var email: String? = null
        }

        inner class AccessToken : Entity(), Serializable {
            var tokenString: String? = null
            var expireAt: String? = null
        }

        inner class RefreshToken : Entity(), Serializable {
            var tokenString: String? = null
            var expireAt: String? = null
        }
    }
}