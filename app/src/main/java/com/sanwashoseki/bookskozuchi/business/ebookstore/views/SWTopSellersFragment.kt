package com.sanwashoseki.bookskozuchi.business.ebookstore.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.business.ebookstore.adapters.SWTopSellerBooksAdapter
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookInfoResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookStoreResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.services.SWBookStoreTopSellerInterface
import com.sanwashoseki.bookskozuchi.business.ebookstore.services.SWBookStoreTopSellerPresenter
import com.sanwashoseki.bookskozuchi.business.filter.models.requests.SWFilterBookRequest
import com.sanwashoseki.bookskozuchi.business.filter.models.responses.SWFilterBookResponse
import com.sanwashoseki.bookskozuchi.business.filter.services.SWFilterBookInterface
import com.sanwashoseki.bookskozuchi.business.filter.services.SWFilterBookPresenter
import com.sanwashoseki.bookskozuchi.business.filter.views.SWFilterBookFragment
import com.sanwashoseki.bookskozuchi.databinding.FragmentTopSellersBinding
import com.sanwashoseki.bookskozuchi.others.Const
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants

class SWTopSellersFragment : SWBaseFragment(), SWTopSellerBooksAdapter.OnItemClickListener,
    SWBookStoreTopSellerInterface, SWMainActivity.OnHeaderClickedListener, SWFilterBookInterface {

    private var binding: FragmentTopSellersBinding? = null
    private var adapter: SWTopSellerBooksAdapter? = null
    private var preTopSeller: SWBookStoreTopSellerPresenter? = null
    private var preFilterBook: SWFilterBookPresenter? = null

    private var isFilter: Boolean? = null
    private var filterModel: SWFilterBookRequest? = null

    private var skeletonScreen: SkeletonScreen? = null
    private var isSwipe = false

    companion object {
        @JvmStatic
        fun newInstance(filter: Boolean?, request: SWFilterBookRequest?) =
            SWTopSellersFragment().apply {
                arguments = Bundle().apply {
                    isFilter = filter
                    filterModel = request
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_top_sellers, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        (activity as SWMainActivity?)?.hideBottomNavigationMenu(true)
        (activity as SWMainActivity).setHeaderInChildrenScreen(getString(R.string.book_store_sellers),
            isSearch = false,
            isFilter = true)
        (activity as SWMainActivity).onCallBackSearchListener(this)

        val ids = ArrayList<Int>()
        if (isFilter == false) {
            preTopSeller?.getBookStoreTopSeller(context, SWConstants.MAX_VALUE, true)
        } else {
            if (filterModel?.publishers != null && filterModel?.publishers!!.isNotEmpty()) {
                for (i in 0 until filterModel?.publishers!!.size) {
                    ids.add(filterModel?.publishers!![i]!!.id!!)
                }
            }
            preFilterBook?.filterTopSellerBook(context,
                filterModel?.categoryIds,
                filterModel?.bookType,
                filterModel?.priceMin,
                filterModel?.priceMax,
                ids)
        }
        binding?.let { it ->
            it.swipeRefresh.setOnRefreshListener {
                isSwipe = true
                if (isFilter == false) {
                    preTopSeller?.getBookStoreTopSeller(context, SWConstants.MAX_VALUE, true)
                } else {
                    preFilterBook?.filterTopSellerBook(context,
                        filterModel?.categoryIds,
                        filterModel?.bookType,
                        filterModel?.priceMin,
                        filterModel?.priceMax,
                        ids)
                }
            }
        }
    }

    private fun getTopSellerBooks() {
        binding?.let { it ->
            it.rcTopSellerBook.layoutManager = LinearLayoutManager(context)
            it.rcTopSellerBook.itemAnimator = DefaultItemAnimator()
            it.rcTopSellerBook.adapter = adapter

            adapter?.setOnCallBackListener(this)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        preTopSeller = SWBookStoreTopSellerPresenter()
        preFilterBook = SWFilterBookPresenter()
        preTopSeller?.attachView(this)
        preFilterBook?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        preTopSeller?.detachView()
        preFilterBook?.detachView()
    }

    override fun onBackPressed(): Boolean {
//        replaceFragment(SWBookStoreFragment.newInstance(), R.id.container, true, null)
        return false
    }

    override fun onTopSellerItemClicked(model: SWBookInfoResponse?) {
        replaceFragment(SWBookDetailFragment.newInstance(model?.id, false),
            R.id.container,
            false,
            null)
    }

    override fun getTopSellerSuccess(result: SWBookStoreResponse) {
        if (isSwipe) {
            binding?.swipeRefresh?.isRefreshing = false
        }
        adapter = SWTopSellerBooksAdapter(2, result.data)
        getTopSellerBooks()
    }

    override fun filterSuccess(result: SWFilterBookResponse) {
        binding?.let { it ->
            if (result.data?.size == 0 || result.data == null) {
                it.tvEmpty.visibility = View.VISIBLE
                it.rcTopSellerBook.visibility = View.GONE
            } else {
                it.tvEmpty.visibility = View.GONE
                it.rcTopSellerBook.visibility = View.VISIBLE
            }
            adapter = SWTopSellerBooksAdapter(2, result.data)
            getTopSellerBooks()
        }
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun showIndicator() {
        binding?.let { it ->
            if (!isSwipe) {
                it.rcTopSellerBook.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                skeletonScreen = Skeleton.bind(it.rcTopSellerBook)
                    .adapter(adapter)
                    .shimmer(true)
                    .angle(20)
                    .frozen(false)
                    .duration(1200)
                    .count(10)
                    .load(R.layout.skeleton_recycle_item_top_seller_books)
                    .show()
            }
        }

//        showLoading(true, getString(R.string.book_store_sellers), getString(R.string.dialog_loading_content))
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

    override fun onSearchListener(content: String) {}

    override fun onShoppingCartClicked() {}

    override fun onWishListClicked(isWishList: Boolean) {}

    override fun onFilterClicked() {
        replaceFragment(SWFilterBookFragment.newInstance(false,
            Const.FILTER_TOP_SELLER,
            filterModel), R.id.container, true, null)
    }

}