package com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.SeekBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.books.base.SWBaseBookActivity
import com.sanwashoseki.bookskozuchi.books.dialogs.SWDialogConfirmPurchase
import com.sanwashoseki.bookskozuchi.books.models.SWBookReadHistoryResponse
import com.sanwashoseki.bookskozuchi.books.readbookpdf.SWPDFBookActivity
import com.sanwashoseki.bookskozuchi.books.readbooktxt.listener.SWTextBookListenerActivity
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.fragments.SWTextColorSettingsFragment
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.fragments.SWTextFontSettingsFragment
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.navigation.SWBookNavigationActivity
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.services.SWTextBookReadByEyeInterface
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.services.SWTextBookReadByEyePresenter
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.utils.SWCommonResponse
import com.sanwashoseki.bookskozuchi.books.readbooktxt.views.CurlPage
import com.sanwashoseki.bookskozuchi.books.readbooktxt.views.CurlView
import com.sanwashoseki.bookskozuchi.books.readbooktxt.views.VTextView
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWBookDetailResponse
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import kotlinx.android.synthetic.main.activity_text_reader.*
import kotlinx.android.synthetic.main.bottom_sheet_text_size.*
import kotlinx.android.synthetic.main.layout_toolbar_reading_by_eye.*
import okhttp3.internal.toHexString
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class SWTextBookReaderActivity : SWBaseBookActivity(), View.OnClickListener,
    SWTextBookReadByEyeInterface, SWTextBookPageListener, CurlView.PageProvider,
    CurlView.PageChangedObserver,
    SWDialogConfirmPurchase.OnPurchaseNowListener {

    companion object {

        val TAG: String = SWTextBookReaderActivity::class.java.simpleName

        const val REQUEST_CODE = 1234

        private const val KEY_BOOK_DATA = "KEY_BOOK_DATA"

        fun start(activity: Activity, book: SWBookDetailResponse.Data?) {
            val intent = Intent(activity, SWTextBookReaderActivity::class.java)
            intent.putExtra(KEY_BOOK_DATA, book)
            activity.startActivity(intent)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: SWBookNavigationActivity.SWBookMarkEvent) {
        event.book.locationIndex?.let { textIndex ->
            Log.d(TAG, "onMessageEvent: $textIndex")
            val pageIndex = vTextView.getCurrentPageIndexFromTextIndex(textIndex)
            Log.e(TAG, "onTextBookLoaded textIndex = $textIndex pageIndex = $pageIndex")
            progressSeekBar.progress = if (vTextView.isVertical) (vTextView.totalPage - 1 - pageIndex) else pageIndex
            onTextBookChanged(pageIndex)
            displayPageBook(pageIndex)
        }
    }

    // Variables
    private var bottomSize: BottomSheetBehavior<*>? = null
    private var adapter: ViewPagerAdapter? = null
    private var presenter: SWTextBookReadByEyePresenter? = null
    private lateinit var vTextView: VTextView
    private lateinit var curlView: CurlView
    private lateinit var textFontSettingsFragment: SWTextFontSettingsFragment
    private lateinit var textColorSettingsFragment: SWTextColorSettingsFragment

    @SuppressLint("StaticFieldLeak")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_reader)
        EventBus.getDefault().register(this)
        Log.d(TAG, "onCreate: TextBookReaderActivity")
        presenter = SWTextBookReadByEyePresenter()
        presenter?.attachView(this)
        presenter?.book = intent.getSerializableExtra(KEY_BOOK_DATA) as? SWBookDetailResponse.Data
        initViews()
        loadData()
        isVerticalBook = presenter?.book?.orientationId == 1
    }

    public override fun onPause() {
        super.onPause()
        curlView.onPause()
    }

    public override fun onResume() {
        super.onResume()
        curlView.onResume()
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        presenter?.detachView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            textFontSettingsFragment.loadData()
            textColorSettingsFragment.loadData()
            loadData()
        }
    }

    // ------------------------------------------------------------------------------------------ //
    private fun initViews() {
        vTextView = VTextView(this)
        curlView = CurlView(this, vTextView.isVertical)
        bottomSize = BottomSheetBehavior.from(btsSize)

        btnBack.setOnClickListener(this)
        btnRotate.setOnClickListener(this)
        btnTextSize.setOnClickListener(this)
        saveBookmarkButton.setOnClickListener(this)
        btnVoice.setOnClickListener(this)
        btnVoice1.setOnClickListener(this)
        menuButton.setOnClickListener(this)

        layoutBtsSize.setOnClickListener(this)
        btsSize.setOnClickListener(this)

        toolbarReadingByEye.setOnClickListener(this)
        bottomToolbar.setOnClickListener(this)

        bt_tool_1.setOnClickListener(this)
        bt_tool_2.setOnClickListener(this)
        bt_hide.setOnClickListener(this)

        updateProgressBarColor()
        progressSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    val pageIndex =
                        if (vTextView.isVertical) (it.max - it.progress) else it.progress
                    Log.e(TAG, "onStopTrackingTouch pageIndex = $pageIndex")
                    onTextBookChanged(pageIndex)
                    displayPageBook(pageIndex)
                }
            }
        })

        try {
            adapter = ViewPagerAdapter(supportFragmentManager)
            textFontSettingsFragment = SWTextFontSettingsFragment.newInstance(presenter?.book?.id)
            textColorSettingsFragment = SWTextColorSettingsFragment.newInstance(presenter?.book?.id)
            adapter?.addFragmentWithTitle(textFontSettingsFragment,
                getString(R.string.reading_word))
            adapter?.addFragmentWithTitle(textColorSettingsFragment,
                getString(R.string.reading_color))

            viewPageTextSize.adapter = adapter
            tabs.setupWithViewPager(viewPageTextSize)
        } catch (e: Exception) {
        }

        initBookContentView()
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
            saveBookmarkButton.visibility = View.GONE
            menuButton.visibility = View.GONE
        }
    }

    private fun displayPageBook(pageIndex: Int) {
        presenter?.currentPage = pageIndex
        curlView.currentIndex = pageIndex
        displayCurrentPage()
    }

    @SuppressLint("SetTextI18n")
    private fun displayCurrentPage() {
        val pageIndex = if (vTextView.isVertical) (progressSeekBar.max - progressSeekBar.progress) else progressSeekBar.progress
        val text = "${pageIndex + 1}/${progressSeekBar.max + 1}"
        progressTextView.text = text
    }

    // Business Functions
    private fun processTools() {
        bottomToolbar.visibility = if (bottomToolbar.isVisible()) View.GONE else View.VISIBLE
        toolbarReadingByEye.visibility =
            if (toolbarReadingByEye.isVisible()) View.GONE else View.VISIBLE
        bt_hide.visibility = if (bt_hide.isVisible()) View.GONE else View.VISIBLE
    }

    private fun initBookContentView() {
        vTextView.setOnPageCalculatorListener(object : VTextView.OnPageCalculatorListener {
            override fun onStartedCalculatingPages(textLength: Int) {
                Log.e(TAG, "onStartedCalculatingPages textLength = $textLength")
                showIndicator()
                progressSeekBar.min = 0
                progressSeekBar.max = 0
                progressSeekBar.progress = 0
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
        bookContainer.addView(vTextView)
        curlView.setBackgroundColor(Color.WHITE)
        curlView.setAllowLastPageCurl(false)
        curlView.setPageProvider(this)
        curlView.setPageChangedObserver(this)
        bookContainer.addView(curlView)
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
            curlView.setBackgroundColor(it.backgroundColor)
        }
    }

    private fun toggleOrientation() {
        val isVertical = !vTextView.isVertical
        vTextView.isVertical = isVertical
        curlView.isVertical = isVertical
        updateProgressBarColor()
        presenter?.setVertical(isVertical)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun updateProgressBarColor() {
        progressSeekBar.progressDrawable = getDrawable(if (vTextView.isVertical) R.drawable.bg_seekbar_vertical else R.drawable.bg_seekbar_horizontal)
    }

    override fun onStartedLoadingBook() {

    }

    override fun onFinishedLoadingBook() {
        presenter?.fontModel?.let { fontModel ->
            curlView.isVertical = fontModel.isVertical
            vTextView.isVertical = fontModel.isVertical
            vTextView.setPadding(fontModel.padding)
            vTextView.setSpacing(fontModel.spacing)
            vTextView.setFontName(SWFontsManager.instance.getTypeface(fontModel.fontName))
            vTextView.setFontSize(fontModel.fontSize.spTopx)
        }
        presenter?.colorModel?.let {
            vTextView.setColor(it.backgroundColor, it.textColor, it.highlightColor)
            curlView.setBackgroundColor(it.backgroundColor)
        }
        presenter?.text?.let { text ->
            vTextView.setText(text)
        }
        isVerticalBook = vTextView.isVertical
    }

    override fun getContext(): Context {
        return this
    }

    override fun getActivity(): Activity {
        return this
    }

    override fun onDisplayPage(page: Int) {

    }

    override fun onUpdatedPlaying(isPlaying: Boolean) {

    }

    override fun onScrollToNextPage(isAutoPlaying: Boolean) {

    }

    @SuppressLint("SetTextI18n")
    override fun getReadHistorySuccess(result: SWBookReadHistoryResponse) {
        Log.d(TAG, "getReadHistorySuccess: $result")
        presenter?.processBook()
    }

    override fun updateCurrentReadingBookSuccess(result: SWCommonResponse<Any>) {

    }

    override fun addBookMarkSuccess(result: SWCommonResponse<Any>) {
        presenter?.let { presenter ->
            presenter.chapters.forEach { chapter ->
                chapter.pageIndex = vTextView.getCurrentPageIndexFromTextIndex(chapter.locationIndex
                    ?: 0)
            }
            SWBookNavigationActivity.start(this, presenter.chapters, presenter.book?.id, true)
        }
    }

    override fun showMessageError(msg: String) {

    }

    override fun onInitializedVoiceSetting(isMale: Boolean, speed: Int, pitch: Int) {

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
            R.id.btnBack -> {
                onBackPressed()
            }
            R.id.btnRotate -> {
                toggleOrientation()
                isVerticalBook = !isVerticalBook
                runTutorialAnim()
            }
            R.id.btsSize -> {
                bottomSize?.state = BottomSheetBehavior.STATE_HIDDEN
            }
            R.id.btnTextSize -> {
                bottomSize?.state = BottomSheetBehavior.STATE_EXPANDED
            }
            R.id.btnVoice, R.id.btnVoice1 -> {
                presenter?.let {
                    SWTextBookListenerActivity.start(this, it.book)
                }
            }
            R.id.saveBookmarkButton -> {
                if (NetworkUtil.isNetworkConnected(this)) {
                    SWCreateNewBookmarkDialog(this, object : SWCreateNewBookmarkDialog.Callback {
                        override fun onPositiveClicked(name: String) {
                            presenter?.addBookMark(name)
                        }
                    }).show()
                } else {
                    showNetworkError()
                }
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
            R.id.toolbarReadingByEye -> {
            }
            R.id.bottomToolbar -> {
            }
            R.id.bt_tool_1 -> processTools()
            R.id.bt_tool_2 -> processTools()
            R.id.bt_hide -> processTools()
        }
    }

    override fun getPageCount(): Int {
        return vTextView.totalPage
    }

    override fun updatePage(page: CurlPage, width: Int, height: Int, index: Int) {
        val pageIndex = minOf(index, vTextView.totalPage - 1)
        Log.e(TAG, "updatePage $index pageIndex $pageIndex")
        val bitmap = createTextureFrom(pageIndex, width, height)
        page.setTexture(bitmap, CurlPage.SIDE_FRONT)
        page.setTexture(bitmap, CurlPage.SIDE_BACK)
        presenter?.colorModel?.let { color ->
            val shadowColor = Color.argb(63,
                Color.red(color.backgroundColor),
                Color.green(color.backgroundColor),
                Color.blue(
                    color.backgroundColor))
            page.setColor(shadowColor, CurlPage.SIDE_BACK)
        }
    }

    private fun createTextureFrom(pageIndex: Int, width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        presenter?.colorModel?.let {
            canvas.drawColor(it.backgroundColor)
        }
        canvas.save()
        canvas.translate(0f, 0f)
        vTextView.drawPage(canvas, pageIndex)
        val pageText = "${pageIndex + 1}/${vTextView.totalPage}"
        val pageWidth = vTextView.rubyStyle.paint.measureText(pageText, 0, pageText.length)
        val pageHeight = vTextView.rubyStyle.paint.textSize
        canvas.drawText(pageText,
            (width - pageWidth) / 2,
            (height - pageHeight),
            vTextView.rubyStyle.paint)
        canvas.restore()
        return bitmap
    }

    override fun onPageChanged(current: Int, total: Int) {
        onTextBookChanged(current)
        if (current == total - 1) {
            onTextBookFinished(current)
        }
    }

    override fun onTextBookLoaded(totalPage: Int) {
        presenter?.totalPage = totalPage
        progressSeekBar.min = 0
        progressSeekBar.max = totalPage - 1
        val textIndex = presenter?.currentLocation ?: 0
        val pageIndex = vTextView.getCurrentPageIndexFromTextIndex(textIndex)
        Log.e(TAG, "onTextBookLoaded textIndex = $textIndex pageIndex = $pageIndex")
        progressSeekBar.progress = if (vTextView.isVertical) (vTextView.totalPage - 1 - pageIndex) else pageIndex
        displayPageBook(pageIndex)
    }

    override fun onTextBookChanged(pageIndex: Int) {
        runOnUiThread {
            progressSeekBar.progress = if (vTextView.isVertical) (vTextView.totalPage - 1 - pageIndex) else pageIndex
            displayCurrentPage()
        }
        if ((presenter?.totalPage ?: 0) > 0) {
            presenter?.currentLocation = vTextView.getCurrentTextIndexFromPageIndex(pageIndex)
            presenter?.currentPage = pageIndex
            presenter?.updateBookReadingNow()
        }
    }

    override fun onTextBookFinished(pageIndex: Int) {
        if (presenter?.book?.isPurchased == false) {
            presenter?.totalPage?.let {
                if (pageIndex == (it - 1)) {
                    runOnUiThread {
                        val dialog = SWDialogConfirmPurchase(this, presenter?.book)
                        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
                        dialog.show()
                        dialog.onCallbackListener(this)
                    }
                }
            }
        }
    }

    override fun onPurchaseListener(book: SWBookDetailResponse.Data?) {
        Log.d(TAG, "onPurchaseListener: $book")
        finish()
        EventBus.getDefault().post(SWPDFBookActivity.SWPurchaseEvent(book))
    }

}