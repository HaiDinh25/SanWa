package com.sanwashoseki.bookskozuchi.business.filter.models.requests

import com.sanwashoseki.bookskozuchi.business.filter.models.responses.SWPublisherResponse
import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWFilterBookRequest(
    var categoryIds: List<Int>?,
    var bookType: Boolean?,
    var priceMin: String?,
    var priceMax: String?,
    var publishers: ArrayList<SWPublisherResponse.Data?>?
) : Entity(), Serializable