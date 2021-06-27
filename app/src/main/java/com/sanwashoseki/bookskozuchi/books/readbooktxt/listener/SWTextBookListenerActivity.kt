package com.sanwashoseki.bookskozuchi.books.readbooktxt.listener

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.books.base.SWBaseBookActivity
import com.sanwashoseki.bookskozuchi.books.models.SWBookReadHistoryResponse
import com.sanwashoseki.bookskozuchi.books.models.SWEditDictionaryResponse
import com.sanwashoseki.bookskozuchi.books.models.SWSettingVoiceReaderModel
import com.sanwashoseki.bookskozuchi.books.readbooktxt.adapters.SWPhoneticsAdapter
import com.sanwashoseki.bookskozuchi.books.readbooktxt.listener.services.SWTextBookListenerInterface
import com.sanwashoseki.bookskozuchi.books.readbooktxt.listener.services.SWTextBookListenerPresenter
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.*
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.fragments.SWTextColorSettingsFragment
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.fragments.SWTextFontSettingsFragment
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.models.SWPhoneticModel
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.models.SWPhoneticRequestModel
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.navigation.SWBookNavigationActivity
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.utils.OnLoadMoreListener
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.utils.SWCommonResponse
import com.sanwashoseki.bookskozuchi.books.readbooktxt.views.VTextView
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWBookDetailResponse
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import com.sanwashoseki.bookskozuchi.utilities.SWBookCacheManager
import kotlinx.android.synthetic.main.activity_text_listener.*
import kotlinx.android.synthetic.main.bottom_sheet_adjust_reading_create.*
import kotlinx.android.synthetic.main.bottom_sheet_adjust_reading_list.*
import kotlinx.android.synthetic.main.bottom_sheet_text_size.*
import kotlinx.android.synthetic.main.bottom_sheet_voice_reader.*
import kotlinx.android.synthetic.main.layout_toolbar_reading_by_voice.*
import okhttp3.internal.toHexString
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SWTextBookListenerActivity : SWBaseBookActivity(), SWTextBookListenerInterface,
    View.OnClickListener, SWTextBookPageListener {

    companion object {

        private const val KEY_BOOK_DATA = "KEY_BOOK_DATA"

        val TAG: String = SWTextBookListenerActivity::class.java.simpleName

        fun start(activity: Activity, book: SWBookDetailResponse.Data?) {
            val intent = Intent(activity, SWTextBookListenerActivity::class.java)
            intent.putExtra(KEY_BOOK_DATA, book)
            activity.startActivityForResult(intent, SWTextBookReaderActivity.REQUEST_CODE)
        }
    }

    private var bottomVoice: BottomSheetBehavior<*>? = null
    private var bottomSize: BottomSheetBehavior<*>? = null
    private var bottomAdjustCreate: BottomSheetBehavior<*>? = null
    private var bottomAdjustList: BottomSheetBehavior<*>? = null

    private var presenter: SWTextBookListenerPresenter? = null
    private lateinit var adapter: ViewPagerAdapter
    private lateinit var vTextView: VTextView
    private var isShowDictionary = false
    private var dictionaries: MutableList<SWPhoneticModel> = arrayListOf()

    private lateinit var textFontSettingsFragment: SWTextFontSettingsFragment
    private lateinit var textColorSettingsFragment: SWTextColorSettingsFragment

    private val scrollListener = object : OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            Log.e(TAG, "onScrollStateChanged newState = $newState")
            if (presenter?.isSpeaking == true) {
                return
            }
            if (newState == 0) {
                val layoutManager = (recyclerView.layoutManager as? LinearLayoutManager)
                val progress =
                    (if (vTextView.isVertical) layoutManager?.findFirstVisibleItemPosition() else layoutManager?.findLastVisibleItemPosition())
                        ?: 0
                Log.e(TAG, "onScrollStateChanged progress = $progress")
                val pageIndex = if (vTextView.isVertical) {
                    vTextView.totalPage - 1 - progress
                } else {
                    progress
                }
                if (pageIndex != presenter?.currentPage && pageIndex >= 0) {
                    val progressByPageIndex =
                        if (vTextView.isVertical) (progressPagesSeekBar.max - pageIndex) else pageIndex
                    progressPagesSeekBar.progress = progressByPageIndex
                    displayCurrentPage()
                    onTextBookChanged(pageIndex)
                    updateCurrentLocationBy(pageIndex)
//                    displayPageBook(pageIndex, progress)
//                    if (pageIndex == vTextView.totalPage - 1) {
//                        onTextBookFinished(pageIndex)
//                    }
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            Log.e(TAG, "onScrolled dx = $dx dy = $dy")
            if (vTextView.totalPage < 1) {
                return
            }

        }

    }

    private fun updateCurrentLocationBy(pageIndex: Int) {
        val location = vTextView.getCurrentTextIndexFromPageIndex(pageIndex)
        val sentenceIndex = vTextView.getSentenceIndexFromLocation(location)
        presenter?.currentSentence = sentenceIndex
        presenter?.currentPage = pageIndex
        presenter?.currentLocation = location
        presenter?.updateBookReadingNow()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: SWBookNavigationActivity.SWBookMarkEvent) {
        event.book.locationIndex?.let { textIndex ->
            Log.d(SWTextBookReaderActivity.TAG, "onMessageEvent: $textIndex")
            val pageIndex = vTextView.getCurrentPageIndexFromTextIndex(textIndex)
            Log.e(SWTextBookReaderActivity.TAG,
                "onTextBookLoaded textIndex = $textIndex pageIndex = $pageIndex")
            val progress =
                if (vTextView.isVertical) (vTextView.totalPage - 1 - pageIndex) else pageIndex
            progressPagesSeekBar.progress = progress
            onTextBookChanged(pageIndex)
            displayPageBook(pageIndex, progress)
            updateCurrentLocationBy(pageIndex)
        }
    }

    @SuppressLint("StaticFieldLeak")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_listener)
        EventBus.getDefault().register(this)
        presenter = SWTextBookListenerPresenter()
        presenter?.attachView(this)
        presenter?.book = intent.getSerializableExtra(KEY_BOOK_DATA) as? SWBookDetailResponse.Data
        initViews()
        isVerticalBook = vTextView.isVertical
        loadData()
    }

    override fun onDestroy() {
        presenter?.detachView()
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter?.engine?.onActivityResult(requestCode, resultCode, data)
    }

    private fun loadData() {
        if (presenter?.isPurchased() == true) {
            if (NetworkUtil.isNetworkConnected(this)) {
                presenter?.getReadHistory()
            } else {
                presenter?.processBook()
            }
        } else {
            presenter?.processBook()
            menuButton.visibility = View.GONE
            editDictionaryButton.visibility = View.GONE
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun updateProgressBarColor() {
        progressPagesSeekBar.progressDrawable =
            getDrawable(if (vTextView.isVertical) R.drawable.bg_seekbar_vertical else R.drawable.bg_seekbar_horizontal)
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

    private fun initViews() {
        vTextView = VTextView(this)
        vTextView.setOnPageCalculatorListener(object : VTextView.OnPageCalculatorListener {
            override fun onStartedCalculatingPages(textLength: Int) {
                Log.e(TAG, "onStartedCalculatingPages textLength = $textLength")
                showIndicator()
                progressPagesSeekBar.min = 0
                progressPagesSeekBar.max = 0
                progressPagesSeekBar.progress = 0
                displayCurrentPage()
            }

            override fun onFinishedCalculatingPages(totalPage: Int) {
                if (totalPage < 1) {
                    return
                }
                Log.e(TAG, "onFinishedCalculatingPages totalPage = $totalPage")
                hideIndicator()
                onTextBookLoaded(totalPage)
            }

        })
        pagesContainer.addView(vTextView, 0)

        val layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        layoutManager.isSmoothScrollbarEnabled = false
//        pagesRecyclerView.isNestedScrollingEnabled = false
        pagesRecyclerView.layoutManager = layoutManager
        pagesRecyclerView.adapter =
            SWTextPageAdapter(vTextView, object : SWTextPageAdapter.OnClickListener {

                override fun onClickedItem(vTextView: VTextView, pageIndex: Int) {
                    if (presenter?.isSpeaking == true) {
                        presenter?.toggleSpeaking(vTextView)
                    }
                    val sentenceIndex = vTextView.getSentenceIndexFromPageIndex(pageIndex)
                    presenter?.currentSentence = sentenceIndex
                    presenter?.toggleSpeaking(vTextView)
                }

                override fun onDisplayedItem(vTextView: VTextView, pageIndex: Int) {
                    Log.e(TAG, "onDisplayedItem pageIndex = $pageIndex")
                }

            })

        progressPagesSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    val progress = it.progress
                    val pageIndex =
                        if (vTextView.isVertical) (it.max - progress) else progress
                    Log.e(TAG,
                        "onStopTrackingTouch pageIndex = $pageIndex")
                    onTextBookChanged(pageIndex)
                    displayPageBook(pageIndex, progress)
                    updateCurrentLocationBy(pageIndex)
                    if (pageIndex == vTextView.totalPage - 1) {
                        onTextBookFinished(pageIndex)
                    }
                }
            }
        })

        presenter?.getListDictionary(0)
        updateProgressBarColor()
        bottomVoice = BottomSheetBehavior.from(btsVoice)
        bottomSize = BottomSheetBehavior.from(btsSize)
        bottomAdjustCreate = BottomSheetBehavior.from(btsAdjustCreate)
        bottomAdjustList = BottomSheetBehavior.from(btsAdjustList)

        btnBack.setOnClickListener(this)
        voiceSettingsButton.setOnClickListener(this)
        btnTextSize.setOnClickListener(this)
        btnEye.setOnClickListener(this)
        btnEye1.setOnClickListener(this)
        menuButton.setOnClickListener(this)
        btnRotate.setOnClickListener(this)

        btsVoice.setOnClickListener(this)
        layoutBtsVoice.setOnClickListener(this)

        btsSize.setOnClickListener(this)
        layoutBtsSize.setOnClickListener(this)

        btsAdjustList.setOnClickListener(this)
        layoutBtsAdjustList.setOnClickListener(this)

        btsAdjustCreate.setOnClickListener(this)
        layoutBtsAdjustCreate.setOnClickListener(this)

        playButton.setOnClickListener(this)

        toolbar_reading_by_eye.setOnClickListener(this)
        bt_tool_1.setOnClickListener(this)
        bt_tool_2.setOnClickListener(this)
        bt_hide.setOnClickListener(this)

        settingVoiceReader()
        initDictionary()

        try {
            adapter = ViewPagerAdapter(supportFragmentManager)
            textFontSettingsFragment = SWTextFontSettingsFragment.newInstance(presenter?.book?.id)
            textColorSettingsFragment = SWTextColorSettingsFragment.newInstance(presenter?.book?.id)
            adapter.addFragmentWithTitle(textFontSettingsFragment,
                getString(R.string.reading_word))
            adapter.addFragmentWithTitle(textColorSettingsFragment,
                getString(R.string.reading_color))

            viewPageTextSize.adapter = adapter
            tabs.setupWithViewPager(viewPageTextSize)
        } catch (e: Exception) {
        }

        presenter?.settingVoiceReader(presenter?.book?.id)
    }

    private fun displayPageBook(pageIndex: Int, position: Int) {
        presenter?.currentPage = pageIndex
        progressPagesSeekBar.progress = position
        pagesRecyclerView.scrollToPosition(position)
        // pagesRecyclerView.smoothScrollToPosition(position)
        displayCurrentPage()
    }

    @SuppressLint("SetTextI18n")
    private fun displayCurrentPage() {
        val pageIndex =
            if (vTextView.isVertical) (progressPagesSeekBar.max - progressPagesSeekBar.progress) else progressPagesSeekBar.progress
        val text = "${pageIndex + 1}/${progressPagesSeekBar.max + 1}"
        progressPagesTextView.text = text
    }

    private fun settingVoiceReader() {
        seekBarVoiceSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                presenter?.updateVoiceSpeed(SWSettingVoiceReaderModel.speedFromIndex(progress))
                presenter?.voiceSetting?.let { settingModel ->
                    SWBookCacheManager.writeKey(settingModel.toString(),
                        Const.FILE_SETTING_VOICE,
                        presenter?.book?.id)
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
                        presenter?.book?.id)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        radioGroupVoiceType.setOnCheckedChangeListener { _, checkedId ->
            presenter?.updateVoiceGender(checkedId == R.id.radioButtonMale)
            presenter?.voiceSetting?.let { settingModel ->
                SWBookCacheManager.writeKey(settingModel.toString(),
                    Const.FILE_SETTING_VOICE,
                    presenter?.book?.id)
            }
        }
    }

    private fun processTools() {
        bottomToolbar.visibility = if (bottomToolbar.isVisible()) View.GONE else View.VISIBLE
        playButton.visibility = if (playButton.isVisible()) View.GONE else View.VISIBLE
        toolbar_reading_by_eye.visibility =
            if (toolbar_reading_by_eye.isVisible()) View.GONE else View.VISIBLE
        bt_hide.visibility = if (bt_hide.isVisible()) View.GONE else View.VISIBLE
    }

    fun setVertical(isVertical: Boolean) {
        presenter?.setVertical(isVertical)
        presenter?.fontModel?.isVertical?.let { isVertical ->
            vTextView.isVertical = isVertical
        }
    }

    fun setFontName(fontName: String) {
        presenter?.setFontName(fontName)
        presenter?.fontModel?.fontName?.let { fontName ->
            vTextView.setFontName(SWFontsManager.instance.getTypeface(fontName))
        }
    }

    fun setFontSize(size: Int) {
        presenter?.setFontSize(size)
        Log.e(TAG, "size $size")
        presenter?.fontModel?.fontSize?.let { fontSize ->
            vTextView.setFontSize(fontSize.spTopx)
        }
    }

    fun setPadding(padding: Float) {
        presenter?.setPadding(padding)
        Log.e(TAG, "padding $padding")
        presenter?.fontModel?.padding?.let { padding ->
            vTextView.setPadding(padding)
        }
    }

    fun setSpacing(spacing: Float) {
        presenter?.setSpacing(spacing)
        Log.e(TAG, "spacing $spacing")
        presenter?.fontModel?.spacing?.let { spacing ->
            vTextView.setSpacing(spacing)
        }
    }

    fun setColor(backgroundColor: Int, textColor: Int, highlightColor: Int) {
        presenter?.setColor(backgroundColor, textColor, highlightColor)
        Log.e(TAG,
            "backgroundColor ${backgroundColor.toHexString()} textColor ${textColor.toHexString()}  highlightColor ${highlightColor.toHexString()}")
        presenter?.colorModel?.let {
            vTextView.setColor(it.backgroundColor, it.textColor, it.highlightColor)
            pagesRecyclerView.setBackgroundColor(it.backgroundColor)
        }
    }

    private fun toggleOrientation() {
        pagesRecyclerView.removeOnScrollListener(scrollListener)
        val isVertical = !vTextView.isVertical
        (pagesRecyclerView.layoutManager as? LinearLayoutManager)?.orientation =
            if (isVertical) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL
        vTextView.isVertical = isVertical
        updateProgressBarColor()
        presenter?.setVertical(isVertical)
    }

    private lateinit var phoneticsAdapter: SWPhoneticsAdapter
    private var currentDic: SWPhoneticModel? = null

    private fun initDictionary() {
        phoneticsAdapter =
            SWPhoneticsAdapter(arrayListOf(),
                dictionariesRecyclerView,
                object : OnLoadMoreListener {
                    override fun onLoadMore() {

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

                    override fun onScrolled(pos: Int) {

                    }

                })

        dictionariesRecyclerView.layoutManager = LinearLayoutManager(this)
        dictionariesRecyclerView.adapter = phoneticsAdapter

        doneDictionaryButton.setOnClickListener {
            addDictionary()
        }

        editDictionaryButton.setOnClickListener {
            listDictionary()
        }

        addDictionaryButton.setOnClickListener {
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

        deleteDictionaryButton.setOnClickListener {
            presenter?.deleteDictionary(currentDic?.id)
        }

        btnVoiceWord.setOnClickListener {
            speakText(wordDictionaryEditText.text.toString())
        }
        btnVoiceFurigana.setOnClickListener {
            speakText(furiganaDictionaryEditText.text.toString())
        }
        btnVoiceRomaji.setOnClickListener {
            speakText(romanjiDictionaryEditText.text.toString())
        }
    }

    private fun listDictionary() {
        phoneticsAdapter.list.clear()
        phoneticsAdapter.notifyDataSetChanged()

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
        if (wordDictionaryEditText.text.toString()
                .isBlank() || furiganaDictionaryEditText.text.toString().isBlank()
        ) return

        val request = SWPhoneticRequestModel()
        request.productId = presenter?.book?.id
        request.vocabulary = wordDictionaryEditText.text.toString()
        request.pronounce = furiganaDictionaryEditText.text.toString()
        request.meaning = romanjiDictionaryEditText.text.toString()
        request.iPA = "iPA"

        if (deleteDictionaryButton.isGone()) {
            presenter?.addDictionary(request)
        } else {
            request.id = currentDic?.id
            presenter?.editDictionary(request)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.playButton -> {
                v.blocking()
                presenter?.toggleSpeaking(vTextView)
            }

            R.id.voiceSettingsButton -> {
                bottomVoice?.state = BottomSheetBehavior.STATE_EXPANDED
                bottomSize?.state = BottomSheetBehavior.STATE_HIDDEN
                bottomAdjustCreate?.state = BottomSheetBehavior.STATE_HIDDEN
                bottomAdjustList?.state = BottomSheetBehavior.STATE_HIDDEN
            }

            R.id.btnTextSize -> {
                bottomSize?.state = BottomSheetBehavior.STATE_EXPANDED
                bottomVoice?.state = BottomSheetBehavior.STATE_HIDDEN
                bottomAdjustCreate?.state = BottomSheetBehavior.STATE_HIDDEN
                bottomAdjustList?.state = BottomSheetBehavior.STATE_HIDDEN
            }

            R.id.btnBack -> {
                setResult(Activity.RESULT_OK)
                finish()
            }

            R.id.btnEye, R.id.btnEye1 -> {
                setResult(Activity.RESULT_OK)
                finish()
            }

            R.id.menuButton -> {
                presenter?.let { presenter ->
                    presenter.chapters.forEach { chapter ->
                        chapter.pageIndex =
                            vTextView.getCurrentPageIndexFromTextIndex(chapter.locationIndex
                                ?: 0)
                    }
                    SWBookNavigationActivity.start(this,
                        presenter.chapters,
                        presenter.book?.id,
                        false)
                }
            }

            R.id.btnRotate -> {
                toggleOrientation()
                runTutorialAnim()
            }

            R.id.btsVoice -> {
                bottomVoice?.state = BottomSheetBehavior.STATE_HIDDEN
            }

            R.id.layoutBtsVoice -> {

            }

            R.id.btsSize -> {
                bottomSize?.state = BottomSheetBehavior.STATE_HIDDEN
            }

            R.id.layoutBtsSize -> {

            }

            R.id.btsAdjustList -> {
                bottomAdjustList?.state = BottomSheetBehavior.STATE_HIDDEN
            }

            R.id.layoutBtsAdjustList -> {

            }

            R.id.btsAdjustCreate -> {
                bottomAdjustCreate?.state = BottomSheetBehavior.STATE_HIDDEN
            }

            R.id.layoutBtsAdjustCreate -> {

            }

            R.id.toolbar_reading_by_eye -> {

            }

            R.id.bt_tool_1 -> {
                processTools()
            }

            R.id.bt_tool_2 -> {
                processTools()
            }

            R.id.bt_hide -> {
                processTools()
            }
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        super.onBackPressed()
    }

    override fun onStartedLoadingBook() {

    }

    override fun onFinishedLoadingBook() {
        presenter?.fontModel?.let { fontModel ->
            (pagesRecyclerView.layoutManager as? LinearLayoutManager)?.orientation =
                if (fontModel.isVertical) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL
            vTextView.isVertical = fontModel.isVertical
            vTextView.setPadding(fontModel.padding)
            vTextView.setSpacing(fontModel.spacing)
            vTextView.setFontName(SWFontsManager.instance.getTypeface(fontModel.fontName))
            vTextView.setFontSize(fontModel.fontSize.spTopx)
        }
        presenter?.colorModel?.let {
            vTextView.setColor(it.backgroundColor, it.textColor, it.highlightColor)
            pagesRecyclerView.setBackgroundColor(it.backgroundColor)
        }
        presenter?.text?.let { text ->
            vTextView.setText(text)
        }
    }

    override fun getReadHistorySuccess(result: SWBookReadHistoryResponse) {
        Log.d(TAG, "getReadHistorySuccess: $result")
        presenter?.processBook()
    }

    override fun updateCurrentReadingBookSuccess(result: SWCommonResponse<Any>) {
        Log.d(TAG, "updateCurrentReadingBookSuccess: $result")
    }

    override fun addBookMarkSuccess(result: SWCommonResponse<Any>) {
        Log.d(TAG, "addBookMarkSuccess: $result")
    }

    override fun getListDictionarySuccess(result: SWCommonResponse<MutableList<SWPhoneticModel>>) {
        result.data?.let { it ->
            dictionaries = result.data!!
            val json = Gson().toJson(dictionaries)
            SWBookCacheManager.writeKey(json, Const.FILE_DICTIONARY, presenter?.book?.id)
            if (isShowDictionary) {
                listDictionary()
            }
        }
        Log.d(TAG, "getListDictionarySuccess: $result")
    }

    override fun addDictionarySuccess(result: SWCommonResponse<Any>) {
        presenter?.getListDictionary(0)
        isShowDictionary = true
    }

    override fun editDictionarySuccess(result: SWEditDictionaryResponse) {
        presenter?.getListDictionary(0)
        isShowDictionary = true
    }

    override fun deleteDictionarySuccess(result: SWCommonResponse<Any>) {
        presenter?.getListDictionary(0)
        isShowDictionary = true
    }

    override fun onInitializedVoiceSetting(isMale: Boolean, speed: Int, pitch: Int) {
        seekBarVoiceSpeed.progress = speed
        seekBarVoiceTone.progress = pitch
        radioButtonMale.isChecked = isMale
        radioButtonFemale.isChecked = !isMale
    }

    override fun showMessageError(msg: String) {
        showMessageError(msg)
    }

    override fun getContext(): Context {
        return this
    }

    override fun getActivity(): Activity {
        return this
    }

    override fun onUpdatedPlaying(isPlaying: Boolean) {
        Log.e(TAG, "onUpdatedPlaying $isPlaying")
        val backgroundId = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        playButton.setBackgroundResource(backgroundId)
    }

    override fun onStartedSpeaking(sentenceIndex: Int, sentenceString: String) {
        val pageIndex = vTextView.getStartPageIndex(sentenceIndex)
        val position = if (vTextView.isVertical) vTextView.totalPage - 1 - pageIndex else pageIndex
        Log.e(TAG, "onStartedSpeaking $position")
        if (vTextView.isVertical) {
            pagesRecyclerView.scrollToPosition(position)
        } else {
            pagesRecyclerView.scrollToPosition(position)
        }
        // pagesRecyclerView.smoothScrollToPosition(position)
        pagesRecyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onSpeaking(
        sentenceIndex: Int,
        sentenceString: String,
        deltaIndex: Int,
        currentPosition: Int,
        totalLength: Int,
    ) {
        val deltaPosition =
            deltaIndex.spTopx + (vTextView.bodyStyle.fontSpace.toInt() / (vTextView.bodyStyle.paint.textSize.toInt() * deltaIndex.spTopx))
        Log.e(TAG, "onSpeaking deltaPosition = $deltaPosition deltaIndex" +
                " = $deltaIndex currentPosition = $currentPosition totalLength = $totalLength")
        if (vTextView.isVertical) {
            pagesRecyclerView.smoothScrollBy(-deltaPosition, 0)
        } else {
            pagesRecyclerView.smoothScrollBy(0, deltaPosition)
        }
    }

    override fun onStoppedSpeaking() {

    }

    override fun showIndicator() {
        showLoading(false, "", "")
    }

    override fun hideIndicator() {
        hideLoading()
    }

    override fun showNetworkError() {

    }

    override fun onTextBookLoaded(totalPage: Int) {
        presenter?.totalSentence = vTextView.totalSentence
        val currentLocation = presenter?.currentLocation ?: 0
        val currentPageIndex = vTextView.getCurrentPageIndexFromTextIndex(currentLocation)
        presenter?.currentSentence = vTextView.getSentenceIndexFromPageIndex(currentPageIndex)
        (pagesRecyclerView.adapter as? SWTextPageAdapter)?.updateData(vTextView)
        val position =
            if (vTextView.isVertical) totalPage - 1 - currentPageIndex else currentPageIndex
        Log.e(TAG,
            "onTextBookLoaded totalPage = $totalPage currentLocation = $currentLocation currentPageIndex = $currentPageIndex position = $position")
        progressPagesSeekBar.min = 0
        progressPagesSeekBar.max = totalPage - 1
        displayPageBook(currentPageIndex, position)
        displayCurrentPage()
        pagesRecyclerView.addOnScrollListener(scrollListener)
    }

    override fun onTextBookChanged(pageIndex: Int) {

    }

    override fun onTextBookFinished(pageIndex: Int) {

    }

}