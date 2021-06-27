package com.sanwashoseki.bookskozuchi.business.more.models.responses

/*
 * Created by HaiDinh on 16/11/2020
 * Mobile 0931670595
*/

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWLogoutResponse : Entity(), Serializable {
    var success: Boolean? = null
    var message: String? = null
    var total = 0
}
