package com.sanwashoseki.bookskozuchi.business.ebookreader.models.response

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWDrmLicenseResponse: Entity(), Serializable {
    val code: Int? = null
    val success: Boolean? = null
    val data: Data? = null
    val messages: String? = null

    inner class Data: Entity(), Serializable {
        val customerId: Int? = null
        val contentId: Int? = null
        val encryptionKey: String? = null
        val decryptionKey: String? = null
        val id: String? = null
        val entityCacheKey: String? = null
    }
}