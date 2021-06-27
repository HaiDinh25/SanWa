package com.sanwashoseki.bookskozuchi.business.more.views

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.business.ebooklibrary.views.SWAddReviewBookFragment
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWBookDetailResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.services.SWBookDetailInterface
import com.sanwashoseki.bookskozuchi.business.ebookstore.services.SWBookDetailPresenter
import com.sanwashoseki.bookskozuchi.business.ebookstore.views.SWBookDetailFragment
import com.sanwashoseki.bookskozuchi.business.more.adpaters.SWPurchaseHistoryAdapter
import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWPurchaseHistoryResponse
import com.sanwashoseki.bookskozuchi.business.more.services.SWPurchaseHistoryInterface
import com.sanwashoseki.bookskozuchi.business.more.services.SWPurchaseHistoryPresenter
import com.sanwashoseki.bookskozuchi.databinding.FragmentPurchaseHistoryBinding
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.gson.Gson
import com.sanwashoseki.bookskozuchi.books.readbookpdf.SWPDFBookActivity
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWTextBookReaderActivity
import com.sanwashoseki.bookskozuchi.business.ebookreader.models.response.SWDrmLicenseResponse
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import com.sanwashoseki.bookskozuchi.utilities.SWBookCacheManager
import com.sanwashoseki.bookskozuchi.utilities.SWBookDecryption
import kotlinx.android.synthetic.main.fragment_my_book.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class SWPurchaseHistoryFragment : SWBaseFragment(), SWPurchaseHistoryInterface,
    SWPurchaseHistoryAdapter.OnItemClickListener, View.OnClickListener {

    private var binding: FragmentPurchaseHistoryBinding? = null
    private var adapter: SWPurchaseHistoryAdapter? = null
    private var presenter: SWPurchaseHistoryPresenter? = null
//    private var preDetail: SWBookDetailPresenter? = null

    private var purchases: SWPurchaseHistoryResponse? = null

    private var skeletonScreen: SkeletonScreen? = null

    companion object {
        @JvmStatic
        fun newInstance() =
            SWPurchaseHistoryFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_purchase_history,
            container,
            false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        (activity as SWMainActivity).hideBottomNavigationMenu(true)
        (activity as SWMainActivity).setHeaderInChildrenScreen(getString(R.string.more_purchase_history),
            isSearch = false,
            isFilter = false)

        presenter?.getPurchaseHistory(context)

        binding?.let { it ->
            it.edtSearchContent.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    val str = it.edtSearchContent.text.toString()
                    searchResult(str)
                }
            })
            it.btnRemoveContent.setOnClickListener(this)
            it.btnFindBook.setOnClickListener(this)
        }
    }

    private fun getPurchaseHistory() {
        binding?.let { it ->
            it.rcPurchaseHistory.layoutManager = LinearLayoutManager(context)
            it.rcPurchaseHistory.itemAnimator = DefaultItemAnimator()
            it.rcPurchaseHistory.adapter = adapter
        }

        adapter?.onCallBackClicked(this)
    }

    private fun searchResult(key: String?) {
        val list = ArrayList<SWPurchaseHistoryResponse.Data>()
        if (purchases?.data!!.isNotEmpty()) {
            for (i in purchases?.data!!.indices) {
                if (purchases?.data!![i].toString().toLowerCase(Locale.ROOT).contains(key.toString()
                        .toLowerCase(
                            Locale.ROOT))
                ) {
                    list.add(purchases?.data!![i])
                }
            }
        }
        adapter = SWPurchaseHistoryAdapter(list)
        getPurchaseHistory()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWPurchaseHistoryPresenter()
        presenter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    override fun getPurchaseHistorySuccess(result: SWPurchaseHistoryResponse?) {
        purchases = result
        binding?.let { it ->
            if (purchases?.data!!.isEmpty()) {
                it.layoutEmpty.visibility = View.VISIBLE
                it.layoutData.visibility = View.GONE
            } else {
                it.layoutEmpty.visibility = View.GONE
                it.layoutData.visibility = View.VISIBLE
            }
        }
        adapter = SWPurchaseHistoryAdapter(result?.data)
        getPurchaseHistory()
    }

//    override fun getBookDetailSuccess(result: SWBookDetailResponse?) {
//        TODO("Not yet implemented")
//    }

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

    private fun startReadingBookScreen() {
        if (presenter?.bookDetail?.contentType == SWConstants.IS_TEXT_FILE) {
            SWTextBookReaderActivity.start(requireActivity(), presenter?.bookDetail)
        } else {
//            SWBookDecryption.bookDecryptionPDF(presenter?.bookId)
            SWPDFBookActivity.start(requireActivity(), presenter?.bookDetail)
        }
    }

    override fun downloadBookSuccess(result: Response<ResponseBody>) {
        startReadingBookScreen()
    }

    override fun getBookDetailSuccess(result: SWBookDetailResponse?, isReadNow: Boolean?) {
        if (isReadNow == true) {
            presenter?.bookDetail = result?.data
        } else {
            replaceFragment(SWAddReviewBookFragment.newInstance(result, true),
                R.id.container,
                false,
                null)
        }
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun showIndicator() {
        binding?.let { it ->
            binding?.rcPurchaseHistory?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            skeletonScreen = Skeleton.bind(it.rcPurchaseHistory)
                .adapter(adapter)
                .shimmer(true)
                .angle(0)
                .frozen(false)
                .duration(1200)
                .count(10)
                .load(R.layout.skeleton_recycle_item_purchase_history)
                .show()
            }
//        showLoading(true, getString(R.string.more_purchase_history), getString(R.string.dialog_loading_content))
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

    override fun onWriteReviewListener(id: Int?) {
        presenter?.bookId = id
        presenter?.getBookDetail(context, false)
    }

    override fun onReadReviewListener(id: Int?) {
        presenter?.bookId = id
        replaceFragment(SWBookDetailFragment.newInstance(id, true), R.id.container, false, null)
    }

    override fun onReadNowListener(id: Int?) {
        presenter?.bookId = id
        presenter?.getBookDetail(context, true)
    }

    override fun onDetailListener(id: Int?) {
        presenter?.bookId = id
        replaceFragment(SWBookDetailFragment.newInstance(id, false), R.id.container, false, null)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnRemoveContent -> binding?.edtSearchContent?.text?.clear()
            R.id.btnFindBook -> (activity as SWMainActivity).replaceActivity(SWMainActivity::class.java)
        }
    }

}