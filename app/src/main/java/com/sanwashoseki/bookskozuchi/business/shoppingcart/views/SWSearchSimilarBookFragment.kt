package com.sanwashoseki.bookskozuchi.business.shoppingcart.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.business.ebookstore.adapters.SWHighlightBooksAdapter
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookInfoResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookStoreResponse
import com.sanwashoseki.bookskozuchi.business.filter.models.requests.SWFilterBookRequest
import com.sanwashoseki.bookskozuchi.business.filter.models.responses.SWPublisherResponse
import com.sanwashoseki.bookskozuchi.business.filter.views.SWFilterBookFragment
import com.sanwashoseki.bookskozuchi.business.shoppingcart.services.SWSearchSimilarInterface
import com.sanwashoseki.bookskozuchi.business.shoppingcart.services.SWSearchSimilarPresenter
import com.sanwashoseki.bookskozuchi.databinding.FragmentHighlightBookBinding
import com.sanwashoseki.bookskozuchi.others.Const
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants

class SWSearchSimilarBookFragment : SWBaseFragment(), SWMainActivity.OnHeaderClickedListener,
    SWHighlightBooksAdapter.OnItemClickListener, SWSearchSimilarInterface {

    private var binding: FragmentHighlightBookBinding? = null
    private var adapter: SWHighlightBooksAdapter? = null
    private var presenter: SWSearchSimilarPresenter? = null

    private var productId: Int? = null
    private var filterModel: SWFilterBookRequest? = null

    private var skeletonScreen: SkeletonScreen? = null
    private var isSwipe = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_highlight_book, container, false)

        initUI()
        return binding?.root
    }

    companion object {
        @JvmStatic
        fun newInstance(id: Int?, request: SWFilterBookRequest?) =
            SWSearchSimilarBookFragment().apply {
                arguments = Bundle().apply {
                    productId = id
                    filterModel = request
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter?.getSearchSimilar(context, 0, SWConstants.MAX_VALUE, productId)
    }

    fun initUI() {
        (activity as SWMainActivity?)?.hideBottomNavigationMenu(true)
        (activity as SWMainActivity).setHeaderInChildrenScreen("Similar", isSearch = false, isFilter = false)
        (activity as SWMainActivity).onCallBackSearchListener(this)

        binding?.let { it ->
            it.swipeRefresh.setOnRefreshListener {
                isSwipe = true
                presenter?.getSearchSimilar(context, 0, SWConstants.MAX_VALUE, productId)
            }
        }
    }

    private fun getListBooks() {
        binding?.rcListBook?.layoutManager = GridLayoutManager(context, 2)
        binding?.rcListBook?.itemAnimator = DefaultItemAnimator()
        binding?.rcListBook?.adapter = adapter

        adapter?.setOnCallBackListener(this)
    }

    override fun onBackPressed(): Boolean {
        replaceFragment(SWShoppingCartFragment.newInstance(null), R.id.container, true, null)
        return true
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

    override fun onSearchListener(content: String) {}

    override fun onShoppingCartClicked() {}

    override fun onWishListClicked(isWishList: Boolean) {}

    override fun onFilterClicked() {
        replaceFragment(SWFilterBookFragment.newInstance(false,  Const.FILTER_SIMILAR, filterModel), R.id.container, true, null)
    }

    override fun onHighlightItemClicked(model: SWBookInfoResponse?, read: Boolean) {

    }

    override fun getSearchSimilarSuccess(result: SWBookStoreResponse?) {
        if (isSwipe) {
            binding?.swipeRefresh?.isRefreshing = false
        }
        adapter = SWHighlightBooksAdapter(2, result?.data)
        getListBooks()
    }

    override fun getPublisherSuccess(result: SWPublisherResponse) {

    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.book_store_title), msg)
    }

    override fun showIndicator() {
        if (!isSwipe) {
            binding?.rcListBook?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            skeletonScreen = Skeleton.bind(binding?.rcListBook)
                .adapter(adapter)
                .shimmer(true)
                .angle(20)
                .frozen(false)
                .duration(1200)
                .count(10)
                .load(R.layout.skeleton_recycle_item_high_light_books)
                .show()
        }
//        showLoading(true, "Search similar", getString(R.string.dialog_loading_content))
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