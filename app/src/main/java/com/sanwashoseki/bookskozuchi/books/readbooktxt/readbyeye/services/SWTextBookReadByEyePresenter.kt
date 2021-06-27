package com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.services

import android.os.Handler
import android.util.Log
import com.google.gson.Gson
import com.sanwashoseki.bookskozuchi.books.models.SWBookReadHistoryResponse
import com.sanwashoseki.bookskozuchi.books.models.SWBookmarksResponse
import com.sanwashoseki.bookskozuchi.books.models.SWCurrentPageModel
import com.sanwashoseki.bookskozuchi.books.models.SWUpdateCurrentReadingModel
import com.sanwashoseki.bookskozuchi.books.readbooktxt.listener.services.SWTextBookListenerPresenter
import com.sanwashoseki.bookskozuchi.books.readbooktxt.models.SWTextSettingModel
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.models.SWBookmarkRequestModel
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.utils.SWCommonResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWBookDetailResponse
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import java.io.File

class SWTextBookReadByEyePresenter : MVP<SWTextBookReadByEyeInterface?> {

    companion object {
        val TAG: String = SWTextBookReadByEyePresenter::class.java.simpleName
    }

    var fontModel = SWTextSettingModel.FontSetting()
        private set
    var colorModel = SWTextSettingModel.ColorSetting()
        private set

    var chapters: ArrayList<SWBookmarksResponse.Data> = arrayListOf()
        private set

    var view: SWTextBookReadByEyeInterface? = null
    var disposable = CompositeDisposable()

    var book: SWBookDetailResponse.Data? = null
    var title: String = ""
        private set
    var subtitle: String = ""
        private set
    var content: String = ""
    var text: String = ""
    private val handler = Handler()
    private var isLoading = false

    var totalPage: Int = 0
    var currentPage: Int = 0
    var currentLocation: Int = 0
    var currentChapter: Int = 0
    var currentParagraph: Int = 0

    val request = SWUpdateCurrentReadingModel()

    var pageTexts = HashMap<Int, String>()
        private set

    override fun detachView() {
        view = null
        disposable.clear()
    }

    override fun attachView(view: SWTextBookReadByEyeInterface?) {
        this.view = view
    }

    // Font Settings Functions
    fun setVertical(isVertical: Boolean) {
        fontModel.isVertical = isVertical
        SWBookCacheManager.writeKey(fontModel.toString(), Const.FILE_SETTING_FONT, book?.id)
        Log.d(SWTextBookListenerPresenter.TAG, "setVertical: $isVertical")
    }

    fun setFontName(font: String) {
        fontModel.fontName = font
        SWBookCacheManager.writeKey(fontModel.toString(), Const.FILE_SETTING_FONT, book?.id)
        Log.d(TAG, "setFont: $fontModel")
    }

    fun setFontSize(size: Int) {
        when (size) {
            SWConstants.PROGRESS_TEXT_SIZE_1 -> fontModel.fontSize = SWConstants.FONT_SIZE_14
            SWConstants.PROGRESS_TEXT_SIZE_2 -> fontModel.fontSize = SWConstants.FONT_SIZE_16
            SWConstants.PROGRESS_TEXT_SIZE_3 -> fontModel.fontSize = SWConstants.FONT_SIZE_18
            SWConstants.PROGRESS_TEXT_SIZE_4 -> fontModel.fontSize = SWConstants.FONT_SIZE_20
            SWConstants.PROGRESS_TEXT_SIZE_5 -> fontModel.fontSize = SWConstants.FONT_SIZE_22
            SWConstants.PROGRESS_TEXT_SIZE_6 -> fontModel.fontSize = SWConstants.FONT_SIZE_24
            SWConstants.PROGRESS_TEXT_SIZE_7 -> fontModel.fontSize = SWConstants.FONT_SIZE_26
            SWConstants.PROGRESS_TEXT_SIZE_8 -> fontModel.fontSize = SWConstants.FONT_SIZE_28
            SWConstants.PROGRESS_TEXT_SIZE_9 -> fontModel.fontSize = SWConstants.FONT_SIZE_30
            SWConstants.PROGRESS_TEXT_SIZE_10 -> fontModel.fontSize = SWConstants.FONT_SIZE_32
        }
        SWBookCacheManager.writeKey(fontModel.toString(), Const.FILE_SETTING_FONT, book?.id)
        Log.d(TAG, "setFont: $fontModel")
    }

    fun setPadding(padding: Float) {
        fontModel.padding = padding
        SWBookCacheManager.writeKey(fontModel.toString(), Const.FILE_SETTING_FONT, book?.id)
        Log.d(TAG, "setFont: $fontModel")
    }

    fun setSpacing(spacing: Float) {
        fontModel.spacing = spacing
        SWBookCacheManager.writeKey(fontModel.toString(), Const.FILE_SETTING_FONT, book?.id)
        Log.d(TAG, "setFont: $fontModel")
    }

    fun setColor(backgroundColor: Int, textColor: Int, highlightColor: Int) {
        colorModel.backgroundColor = backgroundColor
        colorModel.textColor = textColor
        colorModel.highlightColor = highlightColor
        SWBookCacheManager.writeKey(colorModel.toString(), Const.FILE_SETTING_COLOR, book?.id)
    }

    //--------------------------------------------------------------------------------------------//

    fun isPurchased(): Boolean {
        return book?.isPurchased == true
    }

    fun processBook() {
        if (isLoading) {
            return
        }
        if (text.isNotEmpty()) {
            view?.onFinishedLoadingBook()
            return
        }
        isLoading = true
        view?.onStartedLoadingBook()
        view?.showIndicator()
        GlobalScope.async {
            if (content.isEmpty()) {
                book?.id.let { id ->
                    content = if (book?.isPurchased == false) {
                        SWBookDecryption.readFileTextSample("${Const.FOLDER_SANWA}/${Const.FOLDER_SAMPLE}/${book?.id}/${Const.FILE_SAMPLE}")
                    } else {
                        SWBookDecryption.bookDecryptionTXT(id)
                    }
                }
            }
            Log.d(TAG, "processBook: $content")
            val lines = content.split(SWConstants.SOURCE_NEW_LINE)
            text = ""
            chapters.clear()
            var isIgnoring = false
            var chapter: SWBookmarksResponse.Data? = null
            lines.forEachIndexed { offset, line ->
                when (offset) {
                    SWConstants.INDEX_TITLE -> {
                        title = line
                    }
                    SWConstants.INDEX_SUBTITLE -> {
                        subtitle = line
                    }
                    else -> {
                        if (!isIgnoring) {
                            if (line.startsWith(SWConstants.STYLE_PREFIX)) {
                                isIgnoring = true
                            } else if (isEnding(line)) {
                                isIgnoring = true
                            } else if (line.isNotEmpty()) {
                                if (isStartingChapter(line)) {
                                    chapter = SWBookmarksResponse.Data()
                                    chapter?.locationIndex = text.length
                                } else {
                                    chapter?.note = removedFurigana(line)
                                    chapter?.chapterIndex = chapters.size
                                    chapter?.let {
                                        chapters.add(it)
                                    }
                                    chapter = null
                                    text += SWConstants.PREFIX_NEW_LINE + line.trim() + SWConstants.TARGET_NEW_LINE
                                }
                            }
                        } else if (isIgnoring && line.endsWith(SWConstants.STYLE_SUFFIX)) {
                            isIgnoring = false
                        }
                    }
                }
            }
            MainScope().async {
                isLoading = false
                view?.hideIndicator()
                view?.onFinishedLoadingBook()
            }
        }
    }

    private fun isEnding(line: String): Boolean {
        var isEnding = false
        SWConstants.ENDING_PREFIXES.forEachIndexed { index, value ->
            if (line.startsWith(value)) {
                isEnding = true
            }
        }
        return isEnding
    }

    private fun isStartingChapter(line: String): Boolean {
        val isChapter = line.matches("［＃.+?］.+?［＃「.+?」.+?］".toRegex())
        return isChapter
    }

    private fun removedFurigana(line: String): String {
        val output = line.replace("《.+?》".toRegex(), "")
        return output
    }

    fun updateBookReadingNow() {
        request.totalPage = totalPage
        request.currentPage = currentPage
        request.location = currentLocation
        request.paragraph = 0
        request.chapter = 0
        request.isReadingNow = true
        updateCurrentReadingBook(book?.id, request)
    }

    fun getReadHistory() {
        if (NetworkUtil.isNetworkConnected(SWApplication.context)) {
            view?.showIndicator()
            disposable.add(RetrofitClient.client!!.getReadHistory(Const.BEARER, book?.id)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWBookReadHistoryResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            currentLocation = result.data?.location ?: 0
                            SWBookCacheManager.writeKey(result.data?.toString(),
                                Const.FILE_CURRENT_PAGE,
                                book?.id)
                            view?.getReadHistorySuccess(result)
                            Log.d(TAG, "getReadHistory: $result")
                        } else {
                            view?.hideIndicator()
                            view?.showMessageError(result?.messages.toString())
                        }
                    }
                ) {
                    Log.d(TAG, "getReadHistory: $it")
                    view?.hideIndicator()
                })
        } else {
            getLocalHistory()
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    private fun getLocalHistory() {
        val currentFile = File(SWApplication.cacheDir,
            "${Const.FOLDER_SANWA}/${Sharepref.getUserEmail(SWApplication.context)}/${book?.id}/${Const.FILE_CURRENT_PAGE}")
        if (currentFile.exists()) {
            val currentDetail = Gson().fromJson(SWBookCacheManager.readFileCache(currentFile),
                SWCurrentPageModel::class.java)
            currentLocation = currentDetail.location ?: 0
        }
    }

    private fun updateCurrentReadingBook(productId: Int?, request: SWUpdateCurrentReadingModel) {
        if (NetworkUtil.isNetworkConnected(SWApplication.context)) {
            disposable.add(RetrofitClient.client!!.updateCurrentReadingBook(Const.BEARER,
                productId,
                request)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWCommonResponse<Any>? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.updateCurrentReadingBookSuccess(result)
                        } else {
                            view?.hideIndicator()
                            view?.showMessageError(result?.messages.toString())
                        }
                        Log.d(TAG, "updateCurrentReadingBook: $result")
                    }
                ) {
                    Log.d(TAG, "updateCurrentReadingBook: $it")
                })
        } else {
            view?.hideIndicator()
//            view?.showNetworkError()
        }
    }

    fun addBookMark(name: String) {
        if (NetworkUtil.isNetworkConnected(SWApplication.context)) {
            view?.showIndicator()
            val request = SWBookmarkRequestModel()
            request.note = name
            request.productId = book?.id
            request.pageIndex = currentPage
            request.locationIndex = currentLocation
            disposable.add(RetrofitClient.client!!.addBookmark(Const.BEARER, request)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWCommonResponse<Any>? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            view?.addBookMarkSuccess(result)
                        } else {
                            view?.hideIndicator()
                            view?.showMessageError(result?.messages.toString())
                        }
                    }
                ) {
                    view?.hideIndicator()
                })
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

}