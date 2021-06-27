package com.sanwashoseki.bookskozuchi.business.ebookstore.views

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants
import com.sanwashoseki.bookskozuchi.business.ebookstore.adapters.SWBookInfoInDetailBookAdapter
import com.sanwashoseki.bookskozuchi.business.ebookstore.adapters.SWHighlightBooksAdapter
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookInfoResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookStoreResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWBookDetailResponse
import com.sanwashoseki.bookskozuchi.business.filter.models.requests.SWFilterBookRequest
import com.sanwashoseki.bookskozuchi.business.filter.models.responses.SWPublisherResponse
import com.sanwashoseki.bookskozuchi.business.more.views.SWSameCategoryBookFragment
import com.sanwashoseki.bookskozuchi.business.shoppingcart.services.SWSearchSimilarInterface
import com.sanwashoseki.bookskozuchi.business.shoppingcart.services.SWSearchSimilarPresenter
import com.sanwashoseki.bookskozuchi.databinding.FragmentOverViewBookBinding
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.utilities.SWApplication
import com.sanwashoseki.bookskozuchi.utilities.SWBookCacheManager
import com.sanwashoseki.bookskozuchi.utilities.SWUIHelper
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import java.io.File

class SWOverViewBookFragment : SWBaseFragment(), SWSearchSimilarInterface,
    SWHighlightBooksAdapter.OnItemClickListener, View.OnClickListener,
    SWBookInfoInDetailBookAdapter.OnItemClickedListener {

    private var binding: FragmentOverViewBookBinding? = null
    private var detail: SWBookDetailResponse? = null
    private var adapterInfo: SWBookInfoInDetailBookAdapter? = null
    private var adapterSimilar: SWHighlightBooksAdapter? = null

    private var presenter: SWSearchSimilarPresenter? = null
    private var publishers: List<SWPublisherResponse.Data?>? = null

    private var seeMoreAuthor = false
    private var seeMoreDescription = false

    companion object {
        @JvmStatic
        fun newInstance(bookDetail: SWBookDetailResponse?) =
            SWOverViewBookFragment().apply {
                arguments = Bundle().apply {
                    detail = bookDetail
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_over_view_book,
            container,
            false
        )

        initUI()
        if (context?.let { SWUIHelper.isTablet(it) }!!) {
            childFragmentManager.beginTransaction().add(
                R.id.bookDetailReviewContainer, SWReviewBookFragment.newInstance(
                    this.detail!!
                )
            ).commitNow()
        }
        return binding?.root
    }

    private fun initUI() {
        presenter?.getSearchSimilar(context, 0, SWConstants.MAX_VALUE, detail?.data?.id)

        binding?.let { it ->
            it.tvISBN?.text = detail?.data?.productReviewOverview?.isbn
            it.tvRlatedISBN?.text = detail?.data?.productReviewOverview?.isbnRelatedPrint
            if (detail?.data?.productReviewOverview?.fullDescription != null) {
                val sp = Html.fromHtml(detail?.data?.productReviewOverview?.fullDescription)
                it.tvDescription.text = sp
            }
            it.tvAboutAuthors.text = detail?.data?.productReviewOverview?.aboutAuthor

            setTextView(
                it.tvDescription,
                it.btnSeeMoreDescription,
                detail?.data?.productReviewOverview?.fullDescription
            )
            setTextView(
                it.tvAboutAuthors,
                it.btnSeeMoreAuthor,
                detail?.data?.productReviewOverview?.aboutAuthor
            )

            it.btnSeeMoreDescription.setOnClickListener(this)
            it.btnSeeMoreAuthor.setOnClickListener(this)

            adapterInfo = SWBookInfoInDetailBookAdapter(detail?.data?.productInfoBooks)
            adapterInfo?.onCallBackListener(this)
            if (!context?.let { it1 -> SWUIHelper.isTablet(it1) }!!) {
                getInfoBook()
            }
        }
    }

    private fun setTextView(textView: TextView?, button: TextView?, string: String?) {
        if (string == null || string == "") {
            textView?.visibility = View.GONE
            button?.visibility = View.GONE
        } else {
            textView?.post {
                val count = textView.lineCount
                if (count < 4) {
                    button?.visibility = View.GONE
                } else {
                    textView.ellipsize = TextUtils.TruncateAt.END
                    textView.maxLines = 4
                    button?.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWSearchSimilarPresenter()
        presenter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    private fun getInfoBook() {
        val manager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding?.let { it ->
            it.rcBookInfo?.layoutManager = manager
            it.rcBookInfo?.itemAnimator = DefaultItemAnimator()
            it.rcBookInfo?.isNestedScrollingEnabled = false
            it.rcBookInfo?.adapter = adapterInfo
        }
    }

    private fun getSimilar() {
        val manager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding?.rcSimilar?.layoutManager = manager
        binding?.rcSimilar?.itemAnimator = DefaultItemAnimator()
        binding?.rcSimilar?.adapter = adapterSimilar

        adapterSimilar?.setOnCallBackListener(this)
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun getSearchSimilarSuccess(result: SWBookStoreResponse?) {
        adapterSimilar = SWHighlightBooksAdapter(1, result?.data)
        SWBookCacheManager.writeKey(result.toString(), Const.FILE_SIMILAR, detail?.data?.id)
        Log.d("TAG", "getSearchSimilarSuccess: $result")
        getSimilar()
        presenter?.getPublisher(context)
    }

    override fun getPublisherSuccess(result: SWPublisherResponse) {
        publishers = result.data
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun showIndicator() {
//        showLoading(true,
//            "Over view",
//            getString(R.string.dialog_loading_content))
    }

    override fun hideIndicator() {
        hideLoading()
    }

    override fun showNetworkError() {
        val file = File(
            SWApplication.cacheDir,
            "${Const.FOLDER_SANWA}/" + Sharepref.getUserEmail(context) + "/" + detail?.data?.id + "/" + Const.FILE_SIMILAR
        )
        if (file.exists()) {
            val detail: SWBookStoreResponse = Gson().fromJson(
                SWBookCacheManager.readFileCache(file),
                SWBookStoreResponse::class.java
            )
            adapterSimilar = SWHighlightBooksAdapter(1, detail.data)
            getSimilar()
        }
    }

    override fun onHighlightItemClicked(model: SWBookInfoResponse?, read: Boolean) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.container, SWBookDetailFragment.newInstance(model?.id, false))
            ?.addToBackStack(null)?.commit()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnSeeMoreDescription -> {
                binding?.let { it ->
                    if (!seeMoreDescription) {
                        it.tvDescription.maxLines = SWConstants.MAX_VALUE
                        it.btnSeeMoreDescription.text = getString(R.string.book_detail_see_less)
                    } else {
                        it.tvDescription.maxLines = 4
                        it.tvDescription.ellipsize = TextUtils.TruncateAt.END
                        it.btnSeeMoreDescription.text = getString(R.string.book_detail_see_more)
                    }
                    seeMoreDescription = !seeMoreDescription
                }
            }
            R.id.btnSeeMoreAuthor -> {
                binding?.let { it ->
                    if (!seeMoreAuthor) {
                        it.tvAboutAuthors.maxLines = SWConstants.MAX_VALUE
                        it.btnSeeMoreAuthor.text = getString(R.string.book_detail_see_less)
                    } else {
                        it.tvAboutAuthors.maxLines = 4
                        it.tvAboutAuthors.ellipsize = TextUtils.TruncateAt.END
                        it.btnSeeMoreAuthor.text = getString(R.string.book_detail_see_more)
                    }
                    seeMoreAuthor = !seeMoreAuthor
                }
            }
        }
    }

    override fun onCallbackListener(position: Int, ids: List<Int>) {
        if (position == 0) {
            val filterModel = SWFilterBookRequest(ids, null, null, null, null)
            activity?.supportFragmentManager?.beginTransaction()?.replace(
                R.id.container, SWSameCategoryBookFragment.newInstance(
                    category = true,
                    filter = false,
                    request = filterModel
                )
            )?.addToBackStack(null)?.commit()
        } else if (position == 4) {
            val listPublisher = ArrayList<SWPublisherResponse.Data?>()
            for (i in publishers!!.indices) {
                for (j in ids.indices) {
                    if (publishers!![i]?.id == ids[j]) {
                        listPublisher.add(publishers!![i])
                    }
                }
            }
            val filterModel = SWFilterBookRequest(null, null, null, null, listPublisher)
            activity?.supportFragmentManager?.beginTransaction()?.replace(
                R.id.container, SWSameCategoryBookFragment.newInstance(
                    category = false,
                    filter = false,
                    request = filterModel
                )
            )?.addToBackStack(null)?.commit()
        }
    }

    override fun onSeeAllDetailListener() {
        activity?.supportFragmentManager?.beginTransaction()?.replace(
            R.id.container,
            SWAboutBookFragment.newInstance(detail?.data?.productInfoBooks)
        )?.addToBackStack(null)?.commit()
    }
}