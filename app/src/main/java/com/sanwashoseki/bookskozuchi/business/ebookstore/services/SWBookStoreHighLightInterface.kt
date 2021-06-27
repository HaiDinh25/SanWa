package com.sanwashoseki.bookskozuchi.business.ebookstore.services

import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookStoreResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWBookStoreHighLightInterface: IRefreshable {

    fun getHighLightSuccess(result: SWBookStoreResponse)

    fun onLoadMoreSuccess(result: SWBookStoreResponse)

    fun showMessageError(msg: String)
}