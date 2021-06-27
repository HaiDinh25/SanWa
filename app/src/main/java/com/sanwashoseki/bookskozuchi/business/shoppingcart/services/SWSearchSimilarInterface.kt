package com.sanwashoseki.bookskozuchi.business.shoppingcart.services

import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookStoreResponse
import com.sanwashoseki.bookskozuchi.business.filter.models.responses.SWPublisherResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWSearchSimilarInterface : IRefreshable {

    fun getSearchSimilarSuccess(result: SWBookStoreResponse?)

    fun getPublisherSuccess(result: SWPublisherResponse)

    fun showMessageError(msg: String)
}