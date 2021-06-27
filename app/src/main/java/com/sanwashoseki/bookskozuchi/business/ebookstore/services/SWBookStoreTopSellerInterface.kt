package com.sanwashoseki.bookskozuchi.business.ebookstore.services

import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookStoreResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWBookStoreTopSellerInterface: IRefreshable {

    fun getTopSellerSuccess(result: SWBookStoreResponse)

    fun showMessageError(msg: String)
}