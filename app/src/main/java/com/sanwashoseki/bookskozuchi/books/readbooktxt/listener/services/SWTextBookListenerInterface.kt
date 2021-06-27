package com.sanwashoseki.bookskozuchi.books.readbooktxt.listener.services

import android.app.Activity
import android.content.Context
import com.sanwashoseki.bookskozuchi.books.models.SWBookReadHistoryResponse
import com.sanwashoseki.bookskozuchi.books.models.SWEditDictionaryResponse
import com.sanwashoseki.bookskozuchi.books.readbookpdf.services.SWPDFTextElement
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.models.SWPhoneticModel
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.utils.SWCommonResponse
import com.sanwashoseki.bookskozuchi.utilities.IRefreshable

interface SWTextBookListenerInterface : IRefreshable {

    fun onStartedLoadingBook()

    fun onFinishedLoadingBook()

    fun getReadHistorySuccess(result: SWBookReadHistoryResponse)

    fun updateCurrentReadingBookSuccess(result: SWCommonResponse<Any>)

    fun addBookMarkSuccess(result: SWCommonResponse<Any>)

    fun getListDictionarySuccess(result: SWCommonResponse<MutableList<SWPhoneticModel>>)

    fun addDictionarySuccess(result: SWCommonResponse<Any>)

    fun editDictionarySuccess(result: SWEditDictionaryResponse)

    fun deleteDictionarySuccess(result: SWCommonResponse<Any>)

    fun onInitializedVoiceSetting(isMale: Boolean, speed: Int, pitch: Int)

    fun showMessageError(msg: String)

    fun onUpdatedPlaying(isPlaying: Boolean)

    fun getContext(): Context

    fun getActivity(): Activity

    fun onStartedSpeaking(sentenceIndex: Int, sentenceString: String)
    fun onSpeaking(sentenceIndex: Int, sentenceString: String, deltaIndex: Int, currentPosition: Int, totalLength: Int)
    fun onStoppedSpeaking()

}