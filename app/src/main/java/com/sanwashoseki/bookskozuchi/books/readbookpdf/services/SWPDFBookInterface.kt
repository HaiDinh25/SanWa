package com.sanwashoseki.bookskozuchi.books.readbookpdf.services

/*
 * Created by Dinh.Van.Hai on 16/11/2020
 * Mobile 0931670595
*/

import android.app.Activity
import android.content.Context
import com.sanwashoseki.bookskozuchi.books.models.SWBookReadHistoryResponse
import com.sanwashoseki.bookskozuchi.books.models.SWEditDictionaryResponse
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.models.SWPhoneticModel
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.utils.SWCommonResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWPDFBookInterface : IRefreshable {

    fun onStartedLoadingBook()

    fun onFinishedLoadingBook()

    fun onStartedParsingPage(page: Int)

    fun onFinishedParsingPage(page: Int, textElements: ArrayList<SWPDFTextElement>)

    fun getContext(): Context

    fun getActivity(): Activity

    fun onDisplayPage(page: Int)

    fun onUpdatedPlaying(isPlaying: Boolean)

    fun onScrollToNextPage(isAutoPlaying: Boolean)

    fun getReadHistorySuccess(result: SWBookReadHistoryResponse)

    fun updateCurrentReadingBookSuccess(result: SWCommonResponse<Any>)

    fun addBookMarkSuccess(result: SWCommonResponse<Any>)

    fun getListDictionarySuccess(result: SWCommonResponse<MutableList<SWPhoneticModel>>)

    fun addDictionarySuccess(result: SWCommonResponse<Any>)

    fun editDictionarySuccess(result: SWEditDictionaryResponse)

    fun deleteDictionarySuccess(result: SWCommonResponse<Any>)

    fun showMessageError(msg: String)

    fun onInitializedVoiceSetting(isMale: Boolean, speed: Int, pitch: Int)

    fun onStartedSpeaking()
    fun onSpeaking(text: String, textElements: ArrayList<SWPDFTextElement>)
    fun onStoppedSpeaking()

}