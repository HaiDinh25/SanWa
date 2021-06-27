package com.sanwashoseki.bookskozuchi.business.ebooklibrary.models.requests

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWAddReviewRequest(
    var productId: Int?,
    var rating: Int?,
    var reviewText: String?,
) : Entity(), Serializable