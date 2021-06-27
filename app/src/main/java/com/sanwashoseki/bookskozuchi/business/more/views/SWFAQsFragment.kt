package com.sanwashoseki.bookskozuchi.business.more.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWAddShoppingCartWishListResponse
import com.sanwashoseki.bookskozuchi.business.more.adpaters.SWFAQsAdapter
import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWFAQsResponse
import com.sanwashoseki.bookskozuchi.business.more.services.SWHelpContactUsInterface
import com.sanwashoseki.bookskozuchi.business.more.services.SWHelpContactUsPresenter
import com.sanwashoseki.bookskozuchi.databinding.FragmentFAQsBinding
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.ViewSkeletonScreen

class SWFAQsFragment : SWBaseFragment(), SWHelpContactUsInterface {

    private var binding: FragmentFAQsBinding? = null
    private var presenter: SWHelpContactUsPresenter? = null

    private var adapterAbout: SWFAQsAdapter? = null
    private var adapterPurchase: SWFAQsAdapter? = null

    private var skeletonScreen: ViewSkeletonScreen? = null

    companion object {
        @JvmStatic
        fun newInstance() =
            SWFAQsFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_f_a_qs, container, false)

        initUI()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter?.getFAQs(context)
    }

    private fun initUI() {
//        presenter?.getFAQs(context)
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWHelpContactUsPresenter()
        presenter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    private fun getFAQsReading(recycle: RecyclerView) {
        recycle.layoutManager = LinearLayoutManager(context)
        recycle.itemAnimator = DefaultItemAnimator()
        recycle.adapter = adapterAbout
    }

    private fun getFAQsPurchase(recycle: RecyclerView) {
        recycle.layoutManager = LinearLayoutManager(context)
        recycle.itemAnimator = DefaultItemAnimator()
        recycle.adapter = adapterPurchase
    }

    override fun getFQAsSuccess(result: SWFAQsResponse) {
        adapterAbout = SWFAQsAdapter(result.data?.readingBookModel?.readingBooks)
        adapterPurchase = SWFAQsAdapter(result.data?.purchasedBookModel?.purchasedBooks)
        binding?.let { it ->
            skeletonScreen?.hide()
            it.tvTitleReading.text = getString(R.string.about_book_purchase)
            it.tvTitleAbout.text = getString(R.string.about_reading_ebook)
            getFAQsReading(it.rcAbout)
            getFAQsPurchase(it.rcReading)
        }
    }

    override fun sendContactSuccess(result: SWAddShoppingCartWishListResponse) {}

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun showIndicator() {
        skeletonScreen = Skeleton.bind(binding?.rootView)
            .load(R.layout.skeleton_fragment_f_a_qs)
            .duration(1200)
            .color(R.color.colorSkeleton)
            .angle(0)
            .show()

//        showLoading(true, getString(R.string.help_faqs), getString(R.string.dialog_loading_content))
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

}