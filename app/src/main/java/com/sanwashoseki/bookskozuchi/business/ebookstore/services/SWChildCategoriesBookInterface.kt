package com.sanwashoseki.bookskozuchi.business.ebookstore.services

import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWChildCategoriesBookResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWChildCategoriesBookInterface: IRefreshable {

    fun getCategorySuccess(result: SWChildCategoriesBookResponse)

    fun onLoadMoreSuccess(result: SWChildCategoriesBookResponse)

    fun showMessageError(msg: String)
}