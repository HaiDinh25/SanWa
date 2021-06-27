package com.sanwashoseki.bookskozuchi.books.readbookpdf.services

import android.os.Handler
import android.util.Log
import com.google.gson.Gson
import com.sanwashoseki.bookskozuchi.books.models.*
import com.sanwashoseki.bookskozuchi.books.readbookpdf.SWPDFBookActivity
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWTextToSpeechEngine
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.models.SWBookmarkRequestModel
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.models.SWPhoneticModel
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.models.SWPhoneticRequestModel
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.utils.SWCommonResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWBookDetailResponse
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.services.RetrofitClient
import com.sanwashoseki.bookskozuchi.utilities.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class SWPDFBookPresenter : MVP<SWPDFBookInterface?> {

    companion object {
        val TAG: String = SWPDFBookPresenter::class.java.simpleName
    }

    var view: SWPDFBookInterface? = null
    var disposable = CompositeDisposable()

    var book: SWBookDetailResponse.Data? = null
    private lateinit var parser: SWPDFBookParser
    private val handler = Handler()
    private var isLoading = false
    var isSpeaking = false
        private set(value) {
            field = value
            view?.onUpdatedPlaying(isPlaying = field)
        }
    var engine: SWTextToSpeechEngine? = null
        private set

    var voiceSetting = SWSettingVoiceReaderModel()

    var totalPage: Int = 0
    var currentPage: Int = 0

    val request = SWUpdateCurrentReadingModel()

    var isSpeakable = false
        private set

    var pageTextElements = HashMap<Int, ArrayList<SWPDFTextElement>>()
        private set

    var isAutoPlaying = false
        private set

    // Dictionaries
    private var dictionaries: HashMap<String, String> = HashMap()
    private var sentences: ArrayList<String> = arrayListOf()
    private var textElementsSentences: ArrayList<ArrayList<SWPDFTextElement>> = arrayListOf()
    private var currentSentenceIndex = 0

    private fun currentSentence(): String {
        if (currentSentenceIndex >= 0 && currentSentenceIndex < sentences.size) {
            return sentences[currentSentenceIndex]
        }
        return ""
    }

    //return text array of sentence are speaking
    private fun currentSentenceTextElements(): ArrayList<SWPDFTextElement> {
        if (currentSentenceIndex >= 0 && currentSentenceIndex < textElementsSentences.size) {
            return getSentenceTextByLine()
        }
        return arrayListOf()
    }

    // Merge multi text in same line to one item.
    // [line1, line1, line1, line2, line2] will be merged to [line1line1line1, line2line2]
    private fun getSentenceTextByLine():  ArrayList<SWPDFTextElement> {
        val sentenceTextByLine = ArrayList<SWPDFTextElement>()
        var currentLineIndex = 0  //index of current line in sentenceTextByLine array
        var baseLine = 0F

        for (textElement in textElementsSentences[currentSentenceIndex]) {
            val item = SWPDFTextElement(
                    textElement.text,
                    textElement.textBounds,
                    textElement.pageIndex,
                    textElement.paperSize,
                    textElement.font,
                    textElement.size,
                    textElement.color
                    )
            if (sentenceTextByLine.isEmpty()) { // add the first text to the output array to make the base of the line
                baseLine = if ((view as SWPDFBookActivity).isVertical) item.textBounds.right else item.textBounds.bottom
                if ((view as SWPDFBookActivity).isVertical ) {
                    if (item.text.equals("「"))
                        item.textBounds.top += item.size / 4
                }
                sentenceTextByLine.add(item)
                continue
            }
            with(item) {
                if ((view as SWPDFBookActivity).isVertical) {
                    if (Math.abs(textBounds.right - baseLine) < size / 2) {
                        val currentTextInLine = sentenceTextByLine.get(currentLineIndex)
                        currentTextInLine.text = currentTextInLine.text.plus(text)
                        currentTextInLine.textBounds.bottom = textBounds.bottom
                    } else {//next line
                        baseLine = textBounds.right
                        sentenceTextByLine.add(this)
                        if (text.equals("「"))this.textBounds.top += size / 3
                        currentLineIndex++
                    }
                } else {
                    if (Math.abs(textBounds.bottom - baseLine) < size / 2) {
                        val currentTextInLine = sentenceTextByLine.get(currentLineIndex)
                        currentTextInLine.text = currentTextInLine.text.plus(text)
                        currentTextInLine.textBounds.right = textBounds.right
                    } else { //next line
                        baseLine = textBounds.bottom
                        sentenceTextByLine.add(this)
                        currentLineIndex++
                    }
                }
            }
        }
        return sentenceTextByLine
    }

    fun updateBookReadingNow() {
        request.totalPage = totalPage
        request.currentPage = currentPage
        request.isReadingNow = true
        updateCurrentReadingBook(request)
    }

    override fun detachView() {
        view = null
        disposable.clear()
        engine?.stop()
        engine = null
    }

    override fun attachView(view: SWPDFBookInterface?) {
        this.view = view
        engine = SWTextToSpeechEngine(view!!.getActivity())
    }

    fun inputSample(idBook: Int?): InputStream? {
        var inputStream: InputStream? = null
        val file = File(SWApplication.cacheDir.toString(), "${Const.FOLDER_SANWA}/${Const.FOLDER_SAMPLE}/${idBook}/${Const.FILE_SAMPLE}")
        if (file.exists()) {
            inputStream = FileInputStream(file)
        }
        return inputStream
    }

    fun inputFile(idBook: Int?): InputStream? {
        var inputStream: InputStream? = null
        val file = File(SWApplication.cacheDir.toString(), "${Const.FOLDER_SANWA}/${Sharepref.getUserEmail(SWApplication.context)}/${idBook}/${Const.FILE_BOOK_PDF}")
        if (file.exists()) {
            inputStream = FileInputStream(file)
        }
        return inputStream
    }

    fun loadBook(idBook: Int?) {
        if (isLoading) {
            return
        }
        val isPurchased = book?.isPurchased ?: false
        val isVertical = book?.orientationId == 1
        Log.e(TAG, "loadBook ${book?.name} $isVertical")

        parser = SWPDFBookParser(book?.orientationId == 1)
//        Log.d(TAG, "loadBook: ${inputFile(idBook)}")
        settingVoiceReader(book?.id)
        val inputStream = if (isPurchased) inputFile(idBook) else inputSample(idBook)
        parser.loadFile(
            inputStream,
            onStarted = {
                isLoading = true
                view?.onStartedLoadingBook()
                },
            onFinished = {
                isLoading = false
                view?.onFinishedLoadingBook()
            }
        )
    }

    fun getTotalPages(): Int {
        return parser.getTotalPages()
    }

    private fun parseTextOf(
        page: Int,
        onStarted: ((Int) -> Unit),
        onFinished: ((Int, ArrayList<SWPDFTextElement>) -> Unit),
    ) {
        if (pageTextElements.containsKey(page)) {
            val pageText = pageTextElements[page] ?: arrayListOf()
            processCurrentPageTextElements()
            onFinished(page, pageText)
            return
        }
        parser.getTextRectOf(page + 1, onStarted = { page ->
            onStarted(page - 1)
        }, onFinished = { page, textElements ->
            if (textElements.isNotEmpty()) {
                pageTextElements[page - 1] = textElements
            }
            processCurrentPageTextElements()
            onFinished(page - 1, textElements)
        })
    }

    private fun processCurrentPageTextElements() {
        val currentTextElements = getCurrentPageTextElements()
        textElementsSentences.clear()
        sentences.clear()
        var sentenceTextElements: ArrayList<SWPDFTextElement> = arrayListOf()
        var sentence = ""
        currentTextElements.forEach { textElement ->
            val text = textElement.text
            if (SWPDFBookParser.SENTENCE_SEPARATOR.contains(text)) {
                sentence += text
                sentenceTextElements.add(textElement)
                if (sentence.isNotEmpty() && sentenceTextElements.isNotEmpty()) {
                    sentences.add(sentence)
                    textElementsSentences.add(sentenceTextElements)
                }
                Log.i(TAG, "Parsed! $sentence - ${sentenceTextElements.size}")
                sentence = ""
                sentenceTextElements = arrayListOf()
            } else {
                sentence += text
                sentenceTextElements.add(textElement)
            }
        }
        if (sentence.isNotEmpty() && sentenceTextElements.isNotEmpty()) {
            sentences.add(sentence)
            textElementsSentences.add(sentenceTextElements)
        }
        currentSentenceIndex = 0
        Log.v(TAG, "Sentences! ${sentences.size} - ${textElementsSentences.size}")
    }

    fun parseTextFromCurrentPage() {
        if (isSpeaking) {
            return
        }
        Log.e(TAG, this::parseTextFromCurrentPage.name)
        parseTextOf(currentPage, { page ->
            view?.onStartedParsingPage(page)
        }, { page, textElements ->
            if (textElements.isNotEmpty()) {
                pageTextElements[page] = textElements
            }
            isSpeakable = getCurrentPageTextContent().isNotEmpty()
            view?.onFinishedParsingPage(page, textElements)
        })
    }

    fun toggleSpeaking() {
        Log.e(TAG, "toggleSpeaking isSpeaking = $isSpeaking")
        if (isSpeaking) {
            isAutoPlaying = false
            stopSpeakingCurrentPage()
        } else {
            isAutoPlaying = true
            startSpeakingCurrentPage()
        }
    }

    fun updateVoiceGender(isMale: Boolean) {
        voiceSetting.isMale = isMale
        //updateTTSEngine()
    }

    fun updateVoiceSpeed(speed: Float) {
        voiceSetting.voiceSpeed = speed
        //updateTTSEngine()
    }

    fun updateVoicePitch(pitch: Float) {
        voiceSetting.voicePitch = pitch
        //updateTTSEngine()
    }

    private fun updateTTSEngine() {
        engine?.update(voiceSetting.isMale, voiceSetting.voiceSpeed, voiceSetting.voicePitch)
    }

    private fun settingVoiceReader(bookId: Int?) {
        val file = File(SWApplication.cacheDir,
            "${Const.FOLDER_SANWA}/${Sharepref.getUserEmail(SWApplication.context)}/${bookId}/${Const.FILE_SETTING_VOICE}")
        if (file.exists()) {
            val localVoiceSetting = Gson().fromJson(SWBookCacheManager.readFileCache(file),
                SWSettingVoiceReaderModel::class.java)
            voiceSetting = localVoiceSetting
        } else {
            voiceSetting = SWSettingVoiceReaderModel()
        }
        view?.onInitializedVoiceSetting(voiceSetting.isMale,
            voiceSetting.voiceSpeedIndex,
            voiceSetting.voicePitchIndex)
    }

    fun getCurrentPageTextElements(): ArrayList<SWPDFTextElement> {
        return pageTextElements[currentPage] ?: arrayListOf()
    }

    fun getCurrentPageTextContent(): String {
        val currentText = getCurrentPageTextElements()
        if (currentText.isEmpty()) {
            return ""
        }
        val textContent = currentText.map { it.text }.reduce { acc, s ->
            acc + "" + s
        }
        return textContent
    }


    fun startSpeakingCurrentPage() {
        Log.e(TAG, this::startSpeakingCurrentPage.name)
        if (!pageTextElements.containsKey(currentPage)) {
            return
        }
        val textContent = currentSentence()
        if (textContent.isEmpty()) {
            return
        }
        Log.e(TAG, "${this::startSpeakingCurrentPage.name} $textContent ${currentSentenceTextElements().size}")
        var replacedText = textContent
        dictionaries.forEach { (key, value) ->
            replacedText = replacedText.replace(key, value)
        }
        engine?.start(replacedText,
            voiceSetting.isMale,
            voiceSetting.voiceSpeed,
            voiceSetting.voicePitch,
            onStarted = {
                isSpeaking = true
                handler.post {
                    view?.onStartedSpeaking()
                }
            },
            onSpeaking = { text ->
                Log.e(TAG, "startSpeakingCurrentPage: $text")
                val currentTextElements = currentSentenceTextElements()
                Log.e(TAG,
                    "onSpeaking text.length = ${text.length} textElements.size = ${currentTextElements.size}")
                handler.post {
                    view?.onSpeaking(text, currentTextElements)
                }
            },
            onFinished = {
                Log.d(TAG, "startSpeakingCurrentPage: onFinish")
                if (currentSentenceIndex < sentences.size - 1) {
                    currentSentenceIndex += 1
                    startSpeakingCurrentPage()
                } else {
                    isSpeaking = false
                    if (isAutoPlaying) {
                        handler.post {
                            view?.onScrollToNextPage(isAutoPlaying)
                        }
                    }
                    handler.post {
                        view?.onStoppedSpeaking()
                    }
                }
            })
    }

    fun stopSpeakingCurrentPage() {
        Log.e(TAG, this::stopSpeakingCurrentPage.name)
        engine?.stop()
        isSpeaking = false
    }

    fun getReadHistory() {
        if (NetworkUtil.isNetworkConnected(SWApplication.context)) {
//            view?.showIndicator()
            Log.d(TAG, "getReadHistory: ${book?.id}")
            disposable.add(RetrofitClient.client!!.getReadHistory(Const.BEARER, book?.id)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWBookReadHistoryResponse? ->
                        if (result?.success == true) {
                            view?.hideIndicator()
                            currentPage = result.data?.currentPage ?: 0
                            SWBookCacheManager.writeKey(result.data?.toString(),
                                Const.FILE_CURRENT_PAGE,
                                book?.id)
                            view?.getReadHistorySuccess(result)
                            SWBookDecryption.bookDecryptionPDF(book?.id)
                            if (SWBookCacheManager.checkExistFile(book?.id, Const.FILE_BOOK_PDF)) {
                                loadBook(book?.id)
                            }
                            Log.d(TAG, "getReadHistory: $result")
                        } else {
//                            view?.hideIndicator()
                            view?.showMessageError(result?.messages.toString())
                        }
                    }
                ) {
                    Log.d(TAG, "getReadHistory: $it")
//                    view?.hideIndicator()
                })
        } else {
            val currentFile = File(SWApplication.cacheDir,
                "${Const.FOLDER_SANWA}/${Sharepref.getUserEmail(SWApplication.context)}/${book?.id}/${Const.FILE_CURRENT_PAGE}")
            if (currentFile.exists()) {
                val currentDetail = Gson().fromJson(SWBookCacheManager.readFileCache(currentFile),
                    SWCurrentPageModel::class.java)
                currentDetail.currentPage?.let { cp ->
                    currentPage = cp
                }
            }
            loadBook(book?.id)
//            view?.hideIndicator()
//            view?.showNetworkError()
        }
    }

    fun updateCurrentReadingBook(request: SWUpdateCurrentReadingModel) {
        if (NetworkUtil.isNetworkConnected(SWApplication.context)) {
//            view?.showIndicator()
            disposable.add(RetrofitClient.client!!.updateCurrentReadingBook(Const.BEARER,
                book?.id,
                request)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result: SWCommonResponse<Any>? ->
                        if (result?.success == true) {
//                            view?.hideIndicator()
                            view?.updateCurrentReadingBookSuccess(result)
                        } else {
//                            view?.hideIndicator()
//                            view?.showMessageError(result?.messages.toString())
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

    fun updateDictionaries(phoneticData: MutableList<SWPhoneticModel>) {
        dictionaries.clear()
        phoneticData.forEach { phoneticItem->
            val vocabulary = (phoneticItem.vocabulary ?: "").trim()
            val meaning = (phoneticItem.pronounce ?: "").trim()
            if (vocabulary.isNotEmpty() && meaning.isNotEmpty()) {
                dictionaries[vocabulary] = meaning
            }
        }
    }

    fun getListDictionary(productId: Int?, page: Int?) {
        if (NetworkUtil.isNetworkConnected(SWApplication.context)) {
            view?.showIndicator()
            disposable.add(RetrofitClient.client!!.listDictionary(Const.BEARER, productId, page,
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
            disposable.add(RetrofitClient.client!!.editDictionary(Const.BEARER, request.id, request)!!
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
                }) {
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


}