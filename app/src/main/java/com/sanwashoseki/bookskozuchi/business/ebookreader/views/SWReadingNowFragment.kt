package com.sanwashoseki.bookskozuchi.business.ebookreader.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.business.ebookreader.adapters.SWExploreBooksAdapter
import com.sanwashoseki.bookskozuchi.business.ebookreader.models.response.SWReadingNowResponse
import com.sanwashoseki.bookskozuchi.business.ebookreader.services.SWReadingNowInterface
import com.sanwashoseki.bookskozuchi.business.ebookreader.services.SWReadingNowPresenter
import com.sanwashoseki.bookskozuchi.business.ebookstore.views.SWBookDetailFragment
import com.sanwashoseki.bookskozuchi.databinding.FragmentReadingNowBinding
import com.bumptech.glide.Glide
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.gson.Gson
import com.sanwashoseki.bookskozuchi.books.dialogs.SWDialogProgressDownload
import com.sanwashoseki.bookskozuchi.books.readbookpdf.SWPDFBookActivity
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWTextBookReaderActivity
import com.sanwashoseki.bookskozuchi.business.ebookreader.models.response.SWDrmLicenseResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWBookDetailResponse
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.utilities.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File

class SWReadingNowFragment : SWBaseFragment(), SWReadingNowInterface, View.OnClickListener,
    SWExploreBooksAdapter.OnBookDetailListener {

    private var mIsInternalMove = false
    private var binding: FragmentReadingNowBinding? = null
    private var presenter: SWReadingNowPresenter? = null
    private var readingNowData: SWReadingNowResponse?= null
    private var skeletonScreen: SkeletonScreen? = null
    private var idBook: Int? = null
    private var isLoading = true
    private var isSwipe = false
    private lateinit var recentlyBook: SWReadingNowResponse.Data

    companion object {
        @JvmStatic
        fun newInstance() =
            SWReadingNowFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reading_now, container, false)

        initUI()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        readingNowData?.let { setupData(it) }
        if (isLoading) {
            showIndicator()
        }
    }

    override fun onResume() {
        super.onResume()
        if(mIsInternalMove){
            mIsInternalMove =  false
            presenter?.getReadingNow(context)
        }
    }

    private fun initUI() {
        (activity as SWMainActivity?)?.hideBottomNavigationMenu(false)
        (activity as SWMainActivity).setHeaderInMainMenu(getString(R.string.navigation_reading_now),
            true)

        binding?.let { it ->
            it.swipeRefresh?.setOnRefreshListener {
                isSwipe = true
                presenter?.getReadingNow(context)
            }
            it.imageView.setOnClickListener(this)
            it.btnFindNow.setOnClickListener(this)
            it.btnSample.setOnClickListener(this)
            it.imageViewRead.setOnClickListener(this)
        }
    }

    override fun onBackPressed(): Boolean {
        requireActivity().finish()
        return false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWReadingNowPresenter()
        presenter?.attachView(this)
        presenter?.getReadingNow(context)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    @SuppressLint("SetTextI18n")
    override fun getReadingNowSuccess(result: SWReadingNowResponse) {
        isLoading = false
        recentlyBook = result.data?.removeFirst()!!
        setupData(result)
        readingNowData = result
        SWBookCacheManager.cacheBookHome(result.toString(), Const.FOLDER_CACHE, Const.FILE_READING_NOW)
        if (isSwipe) {
            isSwipe = false
            binding?.swipeRefresh?.isRefreshing = false
        }
    }

    override fun getBookDetailSuccess(result: SWBookDetailResponse?) {
        Log.d("TAG", "getBookDetailSuccess: $result")
        presenter?.bookDetail = result?.data
    }

    override fun getDrmLicenseSuccess(result: SWDrmLicenseResponse) {
        if (NetworkUtil.isNetworkConnected(context)) {
            if (SWBookCacheManager.checkUpdateTime(presenter?.bookId, presenter?.bookDetail?.updatedOnUtc)) {
                if (SWBookCacheManager.checkExistFile(presenter?.bookId, Const.FILE_DATA)) {
                    if (SWBookCacheManager.checkExistFile(presenter?.bookId, Const.FILE_BOOK_DETAIL)) {
                        Log.d("TAG", "getDrmLicenseSuccess: ${presenter?.bookId}")
                        val detail: SWBookDetailResponse = Gson().fromJson(
                            SWBookCacheManager.readFileCache(SWBookCacheManager.fileConfigText(presenter?.bookId, Const.FILE_BOOK_DETAIL)),
                            SWBookDetailResponse::class.java)
                        presenter?.bookDetail = detail.data
                    }
                    startReadingBookScreen()
                } else {
                    showLoading()
                    presenter?.downloadBook(context, presenter?.bookId)
                }
            } else {
                showLoading()
                presenter?.downloadBook(context, presenter?.bookId)
            }
        } else {
            if (SWBookCacheManager.checkExistFile(presenter?.bookId, Const.FILE_DATA)) {
                val detail: SWBookDetailResponse =
                    Gson().fromJson(SWBookCacheManager.readFileCache(SWBookCacheManager.fileConfigText(presenter?.bookId, Const.FILE_BOOK_DETAIL)),
                        SWBookDetailResponse::class.java)
                presenter?.bookDetail = detail.data
                startReadingBookScreen()
            }
        }
    }

    override fun downloadBookSuccess(result: Response<ResponseBody>) {
        startReadingBookScreen()
    }

    private fun startReadingBookScreen() {
        if (presenter?.bookDetail?.contentType == SWConstants.IS_TEXT_FILE) {
            SWTextBookReaderActivity.start(requireActivity(), presenter?.bookDetail)
        } else {
//            SWBookDecryption.bookDecryptionPDF(presenter?.bookId)
            SWPDFBookActivity.start(requireActivity(), presenter?.bookDetail)
        }
    }

    override fun downloadSampleBookSuccess(result: Response<ResponseBody>) {
        val isTextFile: Boolean = presenter?.bookDetail?.contentType.equals(SWConstants.IS_TEXT_FILE)
        if (isTextFile) {
            SWTextBookReaderActivity.start(requireActivity(), presenter?.bookDetail)
        } else {
            SWPDFBookActivity.start(requireActivity(), presenter?.bookDetail)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupData(result: SWReadingNowResponse) {
        binding?.let { it ->
            if (result.data?.size == 0) {
                skeletonScreen?.hide()
            } else {
                if (result.data!!.size == 1) {
                    it.imageNullBook.visibility = View.VISIBLE
                } else {
                    it.imageNullBook.visibility = View.GONE
                }
                idBook = recentlyBook.id
                skeletonScreen?.hide()
                if (recentlyBook.isSample == false) {
                    it.layoutRead.visibility = View.VISIBLE
                    it.layoutSample.visibility = View.GONE

                    it.tvLastRead.text =
                        getString(R.string.reading_now_today_sub) + recentlyBook.lastActivityAtFormated
                    it.tvNameReading.text = recentlyBook.name
                    it.tvDescriptionReading.text = recentlyBook.shortDescription
                    it.tvAuthorReading?.text = getString(R.string.by_author) + recentlyBook.authors!![0].name
                    Glide.get(requireContext()).clearMemory()
                    Glide.with(this)
                        .load(recentlyBook.defaultPictureModel?.imageUrl)
                        .into(it.imageViewRead)

                    if (recentlyBook.audioReading == false) {
                        it.icListenerRead.visibility = View.GONE
                    }
                } else {
                    it.layoutRead.visibility = View.GONE
                    it.layoutSample.visibility = View.VISIBLE

                    it.tvName.text = recentlyBook.name
                    it.tvDescription.text = recentlyBook.shortDescription
                    it.tvAuthor?.text = getString(R.string.by_author) + recentlyBook.authors!![0].name
                    Glide.get(requireContext()).clearMemory()
                    Glide.with(this)
                        .load(recentlyBook.defaultPictureModel?.imageUrl)
                        .into(it.imageView)

                    if (recentlyBook.audioReading == false) {
                        it.icListener.visibility = View.GONE
                    }
                }

                if (result.data?.size!! > 1) {
                    it.btnFindNow.visibility = View.GONE
                }

                val manager = LinearLayoutManager(context)
                val adapter = SWExploreBooksAdapter(result.data)
                it.rcExplore.layoutManager = manager
                it.rcExplore.itemAnimator = DefaultItemAnimator()
                it.rcExplore.adapter = adapter
                adapter.onCallBackDetailBook(this)
            }
        }
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun showIndicator() {
        if (!isSwipe) {
            skeletonScreen = Skeleton.bind(binding?.rootView)
                .load(R.layout.skeleton_fragment_reading_now)
                .duration(1200)
                .color(R.color.colorSkeleton)
                .angle(0)
                .show()
        }
        showLoading(true, "", getString(R.string.dialog_loading_content))
    }

    override fun hideIndicator() {
        hideLoading()
    }

    override fun showNetworkError() {
        val fReading = File(SWApplication.cacheDir, "${Const.FOLDER_SANWA}/${Const.FOLDER_CACHE}/${Const.FILE_READING_NOW}")
        if (fReading.exists()) {
            val myBookModel: SWReadingNowResponse = Gson().fromJson(SWBookCacheManager.readFileCache(fReading), SWReadingNowResponse::class.java)
            setupData(myBookModel)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imageView -> {
                replaceFragment(SWBookDetailFragment.newInstance(idBook, false), R.id.container, false, null)
            }
            R.id.btnFindNow -> {
                (activity as SWMainActivity).replaceActivity(SWMainActivity::class.java)
                (activity as SWMainActivity).finish()
            }
            R.id.btnSample -> {
                showLoading()
                presenter?.downloadSampleBook(context, idBook)
            }
            R.id.imageViewRead -> {
                showLoading()
                presenter?.bookId = idBook
                presenter?.getBookDetail(context)
            }
        }
    }

    override fun onDetailBookListener(id: Int?) {
        mIsInternalMove = true
        showLoading()
        presenter?.bookId = id
        presenter?.getBookDetail(context)
    }

}