package com.sanwashoseki.bookskozuchi.books.readbooktxt.listener.services

import android.os.Handler
import android.util.Log
import com.google.gson.Gson
import com.sanwashoseki.bookskozuchi.books.models.*
import com.sanwashoseki.bookskozuchi.books.readbooktxt.models.SWTextSettingModel
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWTextToSpeechEngine
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.models.SWBookmarkRequestModel
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.models.SWPhoneticModel
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.models.SWPhoneticRequestModel
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.utils.SWCommonResponse
import com.sanwashoseki.bookskozuchi.books.readbooktxt.views.VTextView
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
import java.util.concurrent.Executor

class SWTextBookListenerPresenter : MVP<SWTextBookListenerInterface?> {

    companion object {
        val TAG: String = SWTextBookListenerPresenter::class.java.simpleName
    }

    var fontModel = SWTextSettingModel.FontSetting()
        private set
    var colorModel = SWTextSettingModel.ColorSetting()
        private set
    var book: SWBookDetailResponse.Data? = null

    var view: SWTextBookListenerInterface? = null
    var disposable = CompositeDisposable()

    private var dictionaries: HashMap<String, String> = HashMap()
    private var isLoading = false

    var voiceSetting = SWSettingVoiceReaderModel()
    private var mainExecutor: Executor? = null

    var engine: SWTextToSpeechEngine? = null
        private set
    var isSpeaking = false
        private set(value) {
            field = value
            view?.onUpdatedPlaying(isPlaying = field)
        }
    var speakingWords = 0

    var content = ""
        private set
    var text = ""
        private set
    var title = ""
        private set
    var subtitle = ""
        private set
    var currentLocation: Int = 0
    var currentPage: Int = 0
    var totalPage: Int = 0

    var totalSentence: Int = 0
    var currentSentence: Int = 0
    var chapters: ArrayList<SWBookmarksResponse.Data> = arrayListOf()
        private set

    var isAutoPlaying = false
        private set

    val request = SWUpdateCurrentReadingModel()

    override fun detachView() {
        view = null
        disposable.clear()
        engine?.stop()
        engine = null
    }

    override fun attachView(view: SWTextBookListenerInterface?) {
        this.view = view
        engine = SWTextToSpeechEngine(view!!.getActivity())
    }

    fun setVertical(isVertical: Boolean) {
        fontModel.isVertical = isVertical
        SWBookCacheManager.writeKey(fontModel.toString(), Const.FILE_SETTING_FONT, book?.id)
        Log.d(TAG, "setVertical: $isVertical")
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

    fun updateVoiceGender(isMale: Boolean) {
        voiceSetting.isMale = isMale
    }

    fun updateVoiceSpeed(speed: Float) {
        voiceSetting.voiceSpeed = speed
    }

    fun updateVoicePitch(pitch: Float) {
        voiceSetting.voicePitch = pitch
    }

    private fun updateTTSEngine() {
        engine?.update(voiceSetting.isMale, voiceSetting.voiceSpeed, voiceSetting.voicePitch)
    }

    fun settingVoiceReader(bookId: Int?) {
        val file = File(SWApplication.cacheDir,
            "${Const.FOLDER_SANWA}/${Sharepref.getUserEmail(SWApplication.context)}/${bookId}/${Const.FILE_SETTING_VOICE}")
        voiceSetting = if (file.exists()) {
            val localVoiceSetting = Gson().fromJson(SWBookCacheManager.readFileCache(file),
                SWSettingVoiceReaderModel::class.java)
            localVoiceSetting
        } else {
            SWSettingVoiceReaderModel()
        }
        view?.onInitializedVoiceSetting(voiceSetting.isMale,
            voiceSetting.voiceSpeedIndex,
            voiceSetting.voicePitchIndex)
    }

    fun updateBookReadingNow() {
        request.totalPage = totalPage
        request.currentPage = currentPage
        request.location = currentLocation
        request.paragraph = 0
        request.chapter = 0
        updateCurrentReadingBook(book?.id, request)
    }

    fun isPurchased(): Boolean {
        return book?.isPurchased == true
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
            currentDetail?.let { cp ->
                currentLocation = cp.location ?: 0
            }
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
        }
    }

    fun addBookMark(name: String, productId: Int?) {
        if (NetworkUtil.isNetworkConnected(SWApplication.context)) {
            view?.showIndicator()
            val request = SWBookmarkRequestModel()
            request.note = name
            request.productId = productId
            request.pageIndex = currentPage
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

    private fun updateDictionaries(phoneticData: MutableList<SWPhoneticModel>) {
        dictionaries.clear()
        phoneticData.forEach { phoneticItem ->
            val vocabulary = (phoneticItem.vocabulary ?: "").trim()
            val meaning = (phoneticItem.pronounce ?: "").trim()
            if (vocabulary.isNotEmpty() && meaning.isNotEmpty()) {
                dictionaries[vocabulary] = meaning
            }
        }
    }

    fun getListDictionary(page: Int?) {
        if (NetworkUtil.isNetworkConnected(SWApplication.context)) {
            view?.showIndicator()
            disposable.add(RetrofitClient.client!!.listDictionary(Const.BEARER, book?.id, page,
                SWConstants.LIMIT)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result: SWCommonResponse<MutableList<SWPhoneticModel>>? ->
                    if (result?.success == true) {
                        result.data?.let { phoneticData ->
                            updateDictionaries(phoneticData)
                            view?.hideIndicator()
                            view?.getListDictionarySuccess(result)
                        }
                    } else {
                        view?.hideIndicator()
                        view?.showMessageError(result?.messages.toString())
                    }
                }) {
                    view?.hideIndicator()
                })
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    fun addDictionary(request: SWPhoneticRequestModel) {
        if (NetworkUtil.isNetworkConnected(SWApplication.context)) {
            view?.showIndicator()
            disposable.add(RetrofitClient.client!!.addDictionary(Const.BEARER, request)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result: SWCommonResponse<Any>? ->
                    if (result?.success == true) {
                        view?.hideIndicator()
                        view?.addDictionarySuccess(result)
                    } else {
                        view?.hideIndicator()
                        view?.showMessageError(result?.messages.toString())
                    }
                }) {
                    view?.hideIndicator()
                })
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    fun editDictionary(request: SWPhoneticRequestModel) {
        if (NetworkUtil.isNetworkConnected(SWApplication.context)) {
            view?.showIndicator()
            disposable.add(RetrofitClient.client!!.editDictionary(Const.BEARER,
                request.id,
                request)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result: SWEditDictionaryResponse? ->
                    if (result?.success == true) {
                        view?.hideIndicator()
                        view?.editDictionarySuccess(result)
                    } else {
                        view?.hideIndicator()
                        view?.showMessageError(result?.messages.toString())
                    }
                    Log.d(TAG, "editDictionary: $result")
                }) {
                    Log.d(TAG, "editDictionary: $it")
                    view?.hideIndicator()
                })
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    fun deleteDictionary(dictionaryId: Int?) {
        if (NetworkUtil.isNetworkConnected(SWApplication.context)) {
            view?.showIndicator()
            disposable.add(RetrofitClient.client!!.deleteDictionary(Const.BEARER, dictionaryId)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result: SWCommonResponse<Any>? ->
                    if (result?.success == true) {
                        view?.hideIndicator()
                        view?.deleteDictionarySuccess(result)
                    } else {
                        view?.hideIndicator()
                        view?.showMessageError(result?.messages.toString())
                    }
                }) {
                    view?.hideIndicator()
                })
        } else {
            view?.hideIndicator()
            view?.showNetworkError()
        }
    }

    // Load books
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
        val output = line.replace("《.+?》".toRegex(), "").trim()
        return output
    }

    fun toggleSpeaking(vTextView: VTextView) {
        Log.e(TAG, "toggleSpeaking isSpeaking = $isSpeaking")
        if (isSpeaking) {
            isAutoPlaying = false
            stopSpeakingCurrentPage()
        } else {
            isAutoPlaying = true
            startSpeakingCurrentPage(vTextView)
        }
    }

    private fun startSpeakingCurrentPage(vTextView: VTextView) {
        var textContent = vTextView.setCurrentSentenceIndex(currentSentence)
        textContent = removedFurigana(textContent)
        Log.e(TAG,
            "startSpeakingCurrentPage currentSentence = $currentSentence totalSentence = $totalSentence textContent = $textContent")
        if (textContent.isEmpty()) {
            if (currentSentence < totalSentence) {
                currentSentence += 1
                textContent = vTextView.setCurrentSentenceIndex(currentSentence)
                textContent = removedFurigana(textContent)
                Log.e(TAG,
                    "startSpeakingCurrentPage currentSentence = $currentSentence totalSentence = $totalSentence textContent = $textContent")
            }
            return
        }
        Log.e(TAG, "${this::startSpeakingCurrentPage.name} $textContent")
        var replacedText = textContent
        dictionaries.forEach { (key, value) ->
            replacedText = replacedText.replace(key, value)
        }
        engine?.start(replacedText,
            voiceSetting.isMale,
            voiceSetting.voiceSpeed,
            voiceSetting.voicePitch,
            onStarted = {
                speakingWords = 0
                isSpeaking = true
                view?.onStartedSpeaking(currentSentence, textContent)
            },
            onSpeaking = { text ->
                speakingWords += text.length
                Log.e(TAG, "onSpeaking $speakingWords / ${textContent.length}")
                view?.onSpeaking(currentSentence,
                    textContent,
                    text.length,
                    speakingWords,
                    textContent.length)
            },
            onFinished = {
                if (currentSentence < totalSentence - 1) {
                    currentSentence += 1
                    startSpeakingCurrentPage(vTextView)
                } else {
                    speakingWords = 0
                    isSpeaking = false
                    view?.onStoppedSpeaking()
                }
            })
    }

    private fun stopSpeakingCurrentPage() {
        Log.e(TAG, this::stopSpeakingCurrentPage.name)
        engine?.stop()
        isSpeaking = false
    }
}