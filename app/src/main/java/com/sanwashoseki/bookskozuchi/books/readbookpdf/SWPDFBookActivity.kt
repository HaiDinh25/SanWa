package com.sanwashoseki.bookskozuchi.books.readbookpdf

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.SeekBar
import androidx.databinding.library.BuildConfig
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.books.base.SWBaseBookActivity
import com.sanwashoseki.bookskozuchi.books.dialogs.SWDialogConfirmPurchase
import com.sanwashoseki.bookskozuchi.books.models.SWBookReadHistoryResponse
import com.sanwashoseki.bookskozuchi.books.models.SWBookmarksResponse
import com.sanwashoseki.bookskozuchi.books.models.SWEditDictionaryResponse
import com.sanwashoseki.bookskozuchi.books.models.SWSettingVoiceReaderModel
import com.sanwashoseki.bookskozuchi.books.readbookpdf.services.SWPDFBookInterface
import com.sanwashoseki.bookskozuchi.books.readbookpdf.services.SWPDFBookPresenter
import com.sanwashoseki.bookskozuchi.books.readbookpdf.services.SWPDFTextDetector
import com.sanwashoseki.bookskozuchi.books.readbookpdf.services.SWPDFTextElement
import com.sanwashoseki.bookskozuchi.books.readbooktxt.adapters.SWPhoneticsAdapter
import com.sanwashoseki.bookskozuchi.books.readbooktxt.listener.SWTextBookListenerActivity
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.*
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.models.SWPhoneticModel
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.models.SWPhoneticRequestModel
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.navigation.SWBookNavigationActivity
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.utils.OnLoadMoreListener
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.utils.SWCommonResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWBookDetailResponse
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import com.sanwashoseki.bookskozuchi.utilities.SWApplication
import com.sanwashoseki.bookskozuchi.utilities.SWBookCacheManager
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import kotlinx.android.synthetic.main.activity_pdf.*
import kotlinx.android.synthetic.main.activity_pdf.bottomToolbar
import kotlinx.android.synthetic.main.activity_pdf.bt_hide
import kotlinx.android.synthetic.main.activity_pdf.bt_tool_1
import kotlinx.android.synthetic.main.activity_pdf.bt_tool_2
import kotlinx.android.synthetic.main.activity_pdf.progressSeekBar
import kotlinx.android.synthetic.main.activity_pdf.progressTextView
import kotlinx.android.synthetic.main.activity_text_reader.*
import kotlinx.android.synthetic.main.bottom_sheet_adjust_reading_create.*
import kotlinx.android.synthetic.main.bottom_sheet_adjust_reading_list.*
import kotlinx.android.synthetic.main.bottom_sheet_text_size.*
import kotlinx.android.synthetic.main.bottom_sheet_voice_reader.*
import kotlinx.android.synthetic.main.layout_toolbar_reading_pdf.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class SWPDFBookActivity : SWBaseBookActivity(), SWPDFBookInterface, View.OnClickListener, SWDialogConfirmPurchase.OnPurchaseNowListener {

    class SWPurchaseEvent(val book: SWBookDetailResponse.Data?) { /* Additional fields if needed */ }

    companion object {

        val TAG: String = SWPDFBookActivity::class.java.simpleName
        private const val INPUT_BOOK = "INPUT_BOOK"

        fun start(activity: Activity, book: SWBookDetailResponse.Data?) {
            val intent = Intent(activity, SWPDFBookActivity::class.java)
            intent.putExtra(INPUT_BOOK, book)
            activity.startActivity(intent)
        }
    }

    private var bottomVoice: BottomSheetBehavior<*>? = null
    private var bottomSize: BottomSheetBehavior<*>? = null
    private var bottomAdjustCreate: BottomSheetBehavior<*>? = null
    private var bottomAdjustList: BottomSheetBehavior<*>? = null

    private var book: SWBookDetailResponse.Data? = null
    private var presenter: SWPDFBookPresenter? = null
    private var chapters: ArrayList<SWBookmarksResponse.Data> = arrayListOf()
    private var textElements: ArrayList<SWPDFTextElement> = arrayListOf()
    private var dictionaries: MutableList<SWPhoneticModel> = arrayListOf()

    private var isShowDir = false
    val isVertical: Boolean
        get() {
            return book?.orientationId == 1
        }

    private var isShowingTool: Boolean = false
        set(value) {
            field = value
            performShowToolbar(field)
        }

    private val paint = Paint()

    @SuppressLint("StaticFieldLeak")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)
        presenter = SWPDFBookPresenter()
        presenter?.attachView(this)
        initParams()
        initViews()
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: SWBookNavigationActivity.SWBookMarkEvent) {
        event.book.pageIndex?.let { pageIndex ->
            // Log.d(TAG, "onMessageEvent: $pageIndex")
            progressSeekBar.progress = if (isVertical) progressSeekBar.max - pageIndex else pageIndex
            presenter?.currentPage = pageIndex
            presenter?.updateBookReadingNow()
            displayCurrentPageDocument()
            displayCurrentPageLabel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
        EventBus.getDefault().unregister(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter?.engine?.onActivityResult(requestCode, resultCode, data)
    }

    private fun initParams() {
        book = intent.getSerializableExtra(INPUT_BOOK) as SWBookDetailResponse.Data?
        presenter?.book = book
        isVerticalBook = isVertical
//         Log.d(TAG, "initParams $book")
    }

    private fun initViews() {
        if (book?.isPurchased == true) {
            initDictionary()
            presenter?.getReadHistory()
            if (NetworkUtil.isNetworkConnected(this)) {
                presenter?.getListDictionary(book?.id, page)
            } else {
                if (SWBookCacheManager.checkExistFile(book?.id, Const.FILE_DICTIONARY)) {
                    val json = SWBookCacheManager.readFileCache(fileDictionary())
                    val objectType = object : TypeToken<ArrayList<SWPhoneticModel>>() {}.type
                    val array = Gson().fromJson<ArrayList<SWPhoneticModel>>(json, objectType)
                    dictionaries.clear()
                    dictionaries.addAll(array)
                    presenter?.updateDictionaries(dictionaries)
                    // Log.d(TAG, "initViews: $dictionaries")
                }
            }
        } else {
            saveBookmarkButton.visibility = View.GONE
            menuButton.visibility = View.GONE
            editDictionaryButton.visibility = View.GONE
            presenter?.loadBook(book?.id)
        }
        bottomVoice = BottomSheetBehavior.from(btsVoice)
        bottomSize = BottomSheetBehavior.from(btsSize)
        bottomAdjustCreate = BottomSheetBehavior.from(btsAdjustCreate)
        bottomAdjustList = BottomSheetBehavior.from(btsAdjustList)

        if(!NetworkUtil.isNetworkConnected(this)) {
            editDictionaryButton.visibility = View.GONE
        }

        btnBack.setOnClickListener(this)
        saveBookmarkButton.setOnClickListener(this)
        menuButton.setOnClickListener(this)
        voiceSettingsButton.setOnClickListener(this)
        editDictionaryButton.setOnClickListener(this)
        topToolbar.setOnClickListener(this)

        addDictionaryButton.setOnClickListener(this)
        backCreateButton.setOnClickListener(this)
        backListButton.setOnClickListener(this)
        btsAdjustList.setOnClickListener(this)
        layoutBtsAdjustList.setOnClickListener(this)
        btsAdjustCreate.setOnClickListener(this)
        layoutBtsAdjustCreate.setOnClickListener(this)

        btsVoice.setOnClickListener(this)
        layoutBtsVoice.setOnClickListener(this)
        btsSize.setOnClickListener(this)
        layoutBtsSize.setOnClickListener(this)

        bottomToolbar.setOnClickListener(this)
        bt_tool_1.setOnClickListener(this)
        bt_tool_2.setOnClickListener(this)
        bt_hide.setOnClickListener(this)

        playButton.setOnClickListener(this)

        btnVoiceWord.setOnClickListener {
            speakText(wordDictionaryEditText.text.toString())
        }
        btnVoiceFurigana.setOnClickListener {
            speakText(furiganaDictionaryEditText.text.toString())
        }
        btnVoiceRomaji.setOnClickListener {
            speakText(romanjiDictionaryEditText.text.toString())
        }

        settingVoiceReader()
    }

    private fun fileDictionary(): File {
        return File(SWApplication.cacheDir, "${Const.FOLDER_SANWA}/${
            Sharepref.getUserEmail(
                this)
        }/${book?.id}/${Const.FILE_DICTIONARY}")
    }


    private fun settingVoiceReader() {
        seekBarVoiceSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                presenter?.updateVoiceSpeed(SWSettingVoiceReaderModel.speedFromIndex(progress))
                presenter?.voiceSetting?.let { settingModel ->
                    SWBookCacheManager.writeKey(settingModel.toString(),
                        Const.FILE_SETTING_VOICE,
                        book?.id)
                    // Log.d(SWTextBookListenerActivity.TAG, "voiceSpeed: $settingModel")
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBarVoiceTone.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                presenter?.updateVoicePitch(SWSettingVoiceReaderModel.pitchFromIndex(progress))
                presenter?.voiceSetting?.let { settingModel ->
                    SWBookCacheManager.writeKey(settingModel.toString(),
                        Const.FILE_SETTING_VOICE,
                        book?.id)
                    // Log.d(SWTextBookListenerActivity.TAG, "voicePitch: $settingModel")
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        progressSeekBar.progressDrawable = getDrawable(if (isVertical) R.drawable.bg_seekbar_vertical else R.drawable.bg_seekbar_horizontal)
        progressSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val page = progressSeekBar.progress
                Log.e(TAG, "onProgressChanged $page")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                val page = progressSeekBar.progress
                Log.e(TAG, "onStartTrackingTouch $page")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                presenter?.stopSpeakingCurrentPage()
                textElements.clear()
                val progress = progressSeekBar.progress
                val pageIndex = if (isVertical) progressSeekBar.max - progress else progress
                Log.e(TAG, "onStopTrackingTouch $progress $pageIndex")
                presenter?.currentPage = pageIndex
                displayCurrentPageDocument()
                displayCurrentPageLabel()
            }
        })

        radioGroupVoiceType.setOnCheckedChangeListener { _, checkedId ->
            presenter?.updateVoiceGender(checkedId == R.id.radioButtonMale)
            presenter?.voiceSetting?.let { settingModel ->
                SWBookCacheManager.writeKey(settingModel.toString(),
                    Const.FILE_SETTING_VOICE,
                    book?.id)
                // Log.d(SWTextBookListenerActivity.TAG, "isMale: $settingModel")
            }
        }
    }

    private fun performShowToolbar(isShown: Boolean) {
        if (presenter?.isSpeaking == true) {
            return
        }
        val visibility = if (isShown) View.VISIBLE else View.INVISIBLE
        bottomToolbar.visibility = visibility
        topToolbar.visibility = visibility
    }

    private fun displayCurrentPageDocument() {
        var currentPage = presenter?.currentPage ?: 0
        if (isVertical) {
            currentPage = (presenter?.totalPage ?: 0) - 1 - currentPage
        }
        pdfView.jumpTo(currentPage)
    }

    private fun generateTableContents(): ArrayList<SWBookmarksResponse.Data> {
        val arrays = ArrayList<SWBookmarksResponse.Data>()
        pdfView.tableOfContents.forEachIndexed { chapterIndex, level0 ->
            val item0 = SWBookmarksResponse.Data()
            item0.note = level0.title
            item0.chapterIndex = chapterIndex
            item0.pageIndex = level0.pageIdx.toInt()
            arrays.add(item0)
            if (level0.children != null) {
                level0.children.forEach { level1 ->
                    val item1 = SWBookmarksResponse.Data()
                    item1.note = level1.title
                    item1.chapterIndex = chapterIndex
                    item1.pageIndex = level1.pageIdx.toInt()
                    arrays.add(item1)
                }
            }
        }
        return arrays
    }

    private fun configPDFView() {
        displayCurrentPageDocument()
        displayCurrentPageLabel()
        val totalPages = presenter?.getTotalPages() ?: 0
        var currentPage = presenter?.currentPage ?: 0
        if (isVertical) {
            currentPage = totalPages - 1 - currentPage
        }
        val inputStream = if (book?.isPurchased == true) presenter?.inputFile(book?.id) else presenter?.inputSample(
            book?.id)
        pdfView.fromStream(inputStream).pages(*SWPDFPagesGenerator.generatePages(totalPages, isVertical))
            .swipeHorizontal(true)
            .defaultPage(currentPage)
            .onDraw { canvas, pageWidth, pageHeight, displayedPage ->
                Log.e(TAG, "onDraw $canvas $pageWidth $pageHeight $displayedPage")
                if (textElements.isNotEmpty()) {
                    val paperSize = textElements.first().paperSize
                    val scale = pageWidth / paperSize.x
                    Log.e(TAG, "paperSize = $paperSize")
                    paint.strokeWidth = scale
                    if (BuildConfig.DEBUG) {
                        val paperBounds = SWPDFTextDetector.getPaddingRect(PointF(pageWidth, pageHeight))
                        paint.style = Paint.Style.STROKE
                        paint.color = SWPDFTextElement.randomColor()
                        canvas.drawRect(paperBounds, paint)
                        paint.color = SWPDFTextElement.randomColor()
                        canvas.drawRect(RectF(0f, 0f, pageWidth, pageHeight), paint)
                    }
                    paint.style = Paint.Style.FILL
                    // Log.d(TAG, "* Draw ${textElements.size} boxes")
                    textElements.forEach {
                        if (it.text.trim().isEmpty()) {
                            return@forEach
                        }
                        val boxRect = it.scaledTextBounds(scale)
                        if (!isVertical) {
                            boxRect.bottom += it.size / 3 //issue high light is skewed upwards
                        }
                        paint.color = it.color
                        // Log.d(TAG, "  + ${it.text} color = ${it.color}, boxRect $boxRect")
                        canvas.drawRect(boxRect, paint)
                    }
                }
            }
            .onLoad {
//                Log.e(TAG, "onLoad $it")
                progressSeekBar.min = 0
                progressSeekBar.max = it - 1
                presenter?.totalPage = it
                displayCurrentPageLabel()
                chapters = generateTableContents()
            }
            .onPageChange { page, pageCount ->
                val pageIndex = if (isVertical) pageCount - 1 - page else page
                Log.e(TAG, "onPageChange $page, $pageCount")
                presenter?.stopSpeakingCurrentPage()
                textElements.clear()
                progressSeekBar.progress = page
                presenter?.currentPage = pageIndex
                presenter?.updateBookReadingNow()
                presenter?.parseTextFromCurrentPage()
                displayCurrentPageLabel()
                if (book?.isPurchased == false) {
                    if (presenter?.currentPage == pageCount - 1) {
                        val dialog = SWDialogConfirmPurchase(this, book)
                        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
                        dialog.show()
                        dialog.onCallbackListener(this)
                    }
                }
            }
            .onPageScroll { page, positionOffset ->
//                Log.e(TAG, "onPageScroll $page, $positionOffset")
            }
            .onError {
//                Log.e(TAG, "onError ${it.localizedMessage}")
            }
            .onPageError { page, error ->
//                Log.e(TAG, "onPageError $page ${error.localizedMessage}")
            }
            .onRender { nbPages ->
//                Log.e(TAG, "onRender $nbPages $pageWidth $pageHeight")
            }
            .enableAnnotationRendering(false)
            .enableAntialiasing(true)
            .enableDoubletap(true)
            .enableSwipe(true)
            .password(null)
            .scrollHandle(null)
            .enableAntialiasing(true)
            .pageFitPolicy(FitPolicy.WIDTH)
            .fitEachPage(true)
            .spacing(0)
            .pageSnap(true)
            .pageFling(true)
            .load()

        playButton.invisible()
    }

    override fun getContext(): Context {
        return this
    }

    override fun getActivity(): Activity {
        return this
    }

    //Dic
    private var isLast = false
    private var page: Int = 0
    private lateinit var phoneticsAdapter: SWPhoneticsAdapter
    private var currentDic: SWPhoneticModel? = null

    private fun initDictionary() {
        phoneticsAdapter = SWPhoneticsAdapter(arrayListOf(), dictionariesRecyclerView,
            object : OnLoadMoreListener {
                override fun onLoadMore() {
//                    if (!isLast) {
//                        loadMore()
//                    }
                }
            },
            object : SWPhoneticsAdapter.Callback {

                override fun onItemClicked(dic: SWPhoneticModel) {
                    deleteDictionaryButton.visible()
                    wordDictionaryEditText.setText(dic.vocabulary)
                    furiganaDictionaryEditText.setText(dic.pronounce)
                    romanjiDictionaryEditText.setText(dic.meaning)
                    currentDic = dic

                    bottomAdjustList?.state = BottomSheetBehavior.STATE_HIDDEN
                    bottomAdjustCreate?.state = BottomSheetBehavior.STATE_EXPANDED
                    bottomSize?.state = BottomSheetBehavior.STATE_HIDDEN
                    bottomVoice?.state = BottomSheetBehavior.STATE_HIDDEN
                }

                override fun onVoidOriginalClicked(text: String) {
                    speakText(text)
                }

                override fun onVoidFurigana(text: String) {
                    speakText(text)
                }

                override fun onScrolled(pos: Int) {}
            })

        dictionariesRecyclerView.layoutManager = LinearLayoutManager(this)
        dictionariesRecyclerView.adapter = phoneticsAdapter

        doneDictionaryButton.setOnClickListener {
            it.blocking()
            addDictionary()
        }

        editDictionaryButton.setOnClickListener {
            it.blocking()
            listDictionary()
        }

        addDictionaryButton.setOnClickListener {
            it.blocking()
            deleteDictionaryButton.gone()
            wordDictionaryEditText.text = null
            furiganaDictionaryEditText.text = null
            romanjiDictionaryEditText.text = null
            currentDic = null

            bottomAdjustList?.state = BottomSheetBehavior.STATE_HIDDEN
            bottomAdjustCreate?.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSize?.state = BottomSheetBehavior.STATE_HIDDEN
            bottomVoice?.state = BottomSheetBehavior.STATE_HIDDEN
        }

        backCreateButton.setOnClickListener {
            bottomAdjustCreate?.state = BottomSheetBehavior.STATE_HIDDEN
            bottomAdjustList?.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSize?.state = BottomSheetBehavior.STATE_HIDDEN
            bottomVoice?.state = BottomSheetBehavior.STATE_HIDDEN
        }

        backListButton.setOnClickListener {
            bottomAdjustList?.state = BottomSheetBehavior.STATE_HIDDEN
        }

        deleteDictionaryButton.setOnClickListener(this)
    }

//    private fun loadMore() {
//        page++
//        phoneticsAdapter.list.add(null)
//        phoneticsAdapter.notifyItemInserted(phoneticsAdapter.list.size - 1)
//    }

    private fun listDictionary() {
        phoneticsAdapter.list.clear()
        phoneticsAdapter.notifyDataSetChanged()
        page = 0
        isLast = false

        phoneticsAdapter.setLoaded()
        phoneticsAdapter.list.addAll(dictionaries)
        phoneticsAdapter.notifyDataSetChanged()

        bottomAdjustList?.state = BottomSheetBehavior.STATE_EXPANDED
        bottomVoice?.state = BottomSheetBehavior.STATE_HIDDEN
        bottomAdjustCreate?.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSize?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun addDictionary() {
//        if (wordDictionaryEditText.text.toString().isBlank() || furiganaDictionaryEditText.text.toString().isBlank() || romanjiDictionaryEditText.text.toString().isBlank()) return
        if (wordDictionaryEditText.text.toString().isBlank() || furiganaDictionaryEditText.text.toString().isBlank()) return

        val request = SWPhoneticRequestModel()
        request.productId = book?.id
        request.vocabulary = wordDictionaryEditText.text.toString()
        request.pronounce = furiganaDictionaryEditText.text.toString()
        request.meaning = romanjiDictionaryEditText.text.toString()

        if (deleteDictionaryButton.isGone()) {
            presenter?.addDictionary(request)
        } else {
            request.id = currentDic?.id
            presenter?.editDictionary(request)
        }
    }

    private fun scrollToNextPage() {
        Log.i(TAG, "scrollToNextPage")
        presenter?.let {
            val page = it.currentPage + 1
            if (page < it.totalPage) {
                Log.e(TAG, "onScrollToNextPage $page")
                progressSeekBar.progress = if (isVertical) progressSeekBar.max - page else page
                presenter?.currentPage = page
                presenter?.updateBookReadingNow()
                displayCurrentPageDocument()
                displayCurrentPageLabel()
                if (presenter?.isAutoPlaying == true) {
                    presenter?.parseTextFromCurrentPage()
                }
            }
        }
    }

    private fun speakText(text: String) {
        presenter?.let { presenter ->
            val voiceSetting = presenter.voiceSetting
            presenter.engine?.start(
                text,
                voiceSetting.isMale,
                voiceSetting.voiceSpeed,
                voiceSetting.voicePitch,
                onStarted = {

                },
                onSpeaking = { text ->

                },
                onFinished = {

                })
        }
    }

    public override fun onPause() {
        super.onPause()
    }

    public override fun onResume() {
        super.onResume()
    }

    @SuppressLint("SetTextI18n")
    private fun displayCurrentPageLabel() {
        progressTextView.text =
            "${(presenter?.currentPage ?: 0) + 1} / ${presenter?.totalPage ?: 0}"
        tvPage.text = "${(presenter?.currentPage ?: 0) + 1} / ${presenter?.totalPage ?: 0}"
    }

    override fun onInitializedVoiceSetting(isMale: Boolean, speed: Int, pitch: Int) {
        seekBarVoiceSpeed.progress = speed
        seekBarVoiceTone.progress = pitch
        radioButtonMale.isChecked = isMale
        radioButtonFemale.isChecked = !isMale
    }

    override fun onStartedSpeaking() {
        textElements = arrayListOf()
        pdfView.postInvalidate()
    }

    override fun onSpeaking(text: String, textElements: ArrayList<SWPDFTextElement>) {
        this.textElements = textElements
        pdfView.postInvalidate()
        Log.e(TAG, "onSpeaking ${textElements.size}")
    }

    override fun onStoppedSpeaking() {
        textElements = arrayListOf()
        pdfView.postInvalidate()
    }

    override fun onStartedLoadingBook() {
        showIndicator()
    }

    override fun onFinishedLoadingBook() {
        hideIndicator()
        configPDFView()
    }

    override fun onStartedParsingPage(page: Int) {
        Log.e(TAG, "onStartedParsingPage page = $page")
    }

    override fun onFinishedParsingPage(page: Int, textElements: ArrayList<SWPDFTextElement>) {
        Log.e(TAG, "onFinishedParsingPage page = $page ${presenter?.currentPage}")
        pdfView.invalidate()
        if (presenter?.currentPage == page) {
            if (presenter?.isSpeakable == false) {
                playButton.invisible()
                if (presenter?.isAutoPlaying == true) {
                    scrollToNextPage()
                }
            } else {
                playButton.visible()
                if (presenter?.isAutoPlaying == true) {
                    presenter?.toggleSpeaking()
                }
            }
        }
    }

    override fun onDisplayPage(page: Int) {

    }

    override fun onUpdatedPlaying(isPlaying: Boolean) {
        pdfView.isSwipeEnabled = !isPlaying
        val backgroundId = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        playButton.setBackgroundResource(backgroundId)
    }

    override fun onScrollToNextPage(isAutoPlaying: Boolean) {
        scrollToNextPage()
    }

    override fun getReadHistorySuccess(result: SWBookReadHistoryResponse) {
        // Log.d(TAG, "getReadHistorySuccess: ${result.data?.currentPage}")
    }

    override fun updateCurrentReadingBookSuccess(result: SWCommonResponse<Any>) {
        TODO("Not yet implemented")
    }

    override fun addBookMarkSuccess(result: SWCommonResponse<Any>) {
        SWBookNavigationActivity.start(this, chapters, book?.id, true)
    }

    override fun getListDictionarySuccess(result: SWCommonResponse<MutableList<SWPhoneticModel>>) {
        if (result.data?.size!! < SWConstants.LIMIT) {
            isLast = true
        }
        dictionaries = result.data!!
        val json = Gson().toJson(dictionaries)
        SWBookCacheManager.writeKey(json, Const.FILE_DICTIONARY, book?.id)
        if (isShowDir) {
            listDictionary()
        }
    }

    override fun addDictionarySuccess(result: SWCommonResponse<Any>) {
        presenter?.getListDictionary(book?.id, page)
        isShowDir = true
//        hideLoading()
//        listDictionary()
    }

    override fun editDictionarySuccess(result: SWEditDictionaryResponse) {
        presenter?.getListDictionary(book?.id, page)
        isShowDir = true
//        listDictionary()
    }

    override fun deleteDictionarySuccess(result: SWCommonResponse<Any>) {
        presenter?.getListDictionary(book?.id, page)
        isShowDir = true
//        listDictionary()
    }

    override fun showMessageError(msg: String) {
        TODO("Not yet implemented")
    }

    override fun showIndicator() {
        showLoading(true, "", "")
    }

    override fun hideIndicator() {
        hideLoading()
    }

    override fun showNetworkError() {
        showLoading(
            false,
            getString(R.string.dialog_error_network_title),
            getString(R.string.dialog_error_network_content)
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.saveBookmarkButton -> {
                SWCreateNewBookmarkDialog(this, object : SWCreateNewBookmarkDialog.Callback {
                    override fun onPositiveClicked(name: String) {
                        presenter?.addBookMark(name, book?.id)
                    }
                }).show()
            }
            R.id.menuButton -> {
                SWBookNavigationActivity.start(this, chapters, book?.id, false)
            }
            R.id.voiceSettingsButton -> {
                bottomVoice?.state = BottomSheetBehavior.STATE_EXPANDED
                bottomSize?.state = BottomSheetBehavior.STATE_HIDDEN
                bottomAdjustCreate?.state = BottomSheetBehavior.STATE_HIDDEN
                bottomAdjustList?.state = BottomSheetBehavior.STATE_HIDDEN
            }
            R.id.addDictionaryButton -> {
                bottomAdjustList?.state = BottomSheetBehavior.STATE_HIDDEN
                bottomAdjustCreate?.state = BottomSheetBehavior.STATE_EXPANDED
                bottomSize?.state = BottomSheetBehavior.STATE_HIDDEN
                bottomVoice?.state = BottomSheetBehavior.STATE_HIDDEN
            }
            R.id.backCreateButton -> {
                bottomAdjustCreate?.state = BottomSheetBehavior.STATE_HIDDEN
                bottomAdjustList?.state = BottomSheetBehavior.STATE_EXPANDED
                bottomSize?.state = BottomSheetBehavior.STATE_HIDDEN
                bottomVoice?.state = BottomSheetBehavior.STATE_HIDDEN
            }
            R.id.backListButton -> bottomAdjustList?.state = BottomSheetBehavior.STATE_HIDDEN
            R.id.btsVoice -> bottomVoice?.state = BottomSheetBehavior.STATE_HIDDEN
            R.id.layoutBtsVoice -> {
            }
            R.id.btsSize -> bottomSize?.state = BottomSheetBehavior.STATE_HIDDEN
            R.id.layoutBtsSize -> {
            }
            R.id.btsAdjustList -> bottomAdjustList?.state = BottomSheetBehavior.STATE_HIDDEN
            R.id.layoutBtsAdjustList -> {
            }
            R.id.btsAdjustCreate -> bottomAdjustCreate?.state = BottomSheetBehavior.STATE_HIDDEN
            R.id.layoutBtsAdjustCreate -> {
            }
            R.id.btnBack -> {
                onBackPressed()
                Sharepref.setBookmarkIndex(SWApplication.context, 0)
            }
            R.id.editDictionaryButton -> listDictionary()
            R.id.deleteDictionaryButton -> presenter?.deleteDictionary(currentDic?.id)
            R.id.topToolbar -> {
                isShowingTool = !isShowingTool
            }
            R.id.bottomToolbar -> {
            }
            R.id.bt_tool_1 -> {
                isShowingTool = !isShowingTool
            }
            R.id.bt_tool_2 -> {
                isShowingTool = !isShowingTool
            }
            R.id.playButton -> {
                playButton.blocking()
                isShowingTool = false
                presenter?.toggleSpeaking()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        presenter?.stopSpeakingCurrentPage()
    }

    override fun onPurchaseListener(book: SWBookDetailResponse.Data?) {
        EventBus.getDefault().post(SWPurchaseEvent(book))
        finish()
    }
}