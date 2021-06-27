package com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.services

import android.app.Activity
import android.content.Context
import com.sanwashoseki.bookskozuchi.books.models.SWBookReadHistoryResponse
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.utils.SWCommonResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWTextBookReadByEyeInterface : IRefreshable {

    fun onStartedLoadingBook()

    fun onFinishedLoadingBook()

    fun getContext(): Context

    fun getActivity(): Activity

    fun onDisplayPage(page: Int)

    fun onUpdatedPlaying(isPlaying: Boolean)

    fun onScrollToNextPage(isAutoPlaying: Boolean)

    fun getReadHistorySuccess(result: SWBookReadHistoryResponse)

    fun updateCurrentReadingBookSuccess(result: SWCommonResponse<Any>)

    fun addBookMarkSuccess(result: SWCommonResponse<Any>)

    fun showMessageError(msg: String)

    fun onInitializedVoiceSetting(isMale: Boolean, speed: Int, pitch: Int)

}