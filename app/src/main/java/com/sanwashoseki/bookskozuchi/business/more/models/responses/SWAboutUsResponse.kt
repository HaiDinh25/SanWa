package com.sanwashoseki.bookskozuchi.business.more.models.responses

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWAboutUsResponse: Entity(), Serializable {
    var success: Boolean? = null
    var data: Data? = null
    var messages: String? = null
    var total: Int? = null

    inner class Data : Entity(), Serializable {
        var versionApp: VersionApp? = null
        var termAnPrivacy: List<TermAnPrivacy>? = null

        inner class VersionApp : Entity(), Serializable {
            var version: String? = null
            var name: String? = null
            var description: String? = null
        }

        inner class TermAnPrivacy : Entity(), Serializable  {
            var title: String? = null
            var body: String? = null
        }
    }
}