package com.sanwashoseki.bookskozuchi.business.ebookstore.services

import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookStoreResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWBookStoreLatestInterface: IRefreshable {

    fun getLatestSuccess(result: SWBookStoreResponse)

    fun showMessageError(msg: String)

    fun onLoadMoreSuccess(result: SWBookStoreResponse)
}