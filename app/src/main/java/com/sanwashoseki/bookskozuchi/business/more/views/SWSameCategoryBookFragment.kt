package com.sanwashoseki.bookskozuchi.business.more.views

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
import com.sanwashoseki.bookskozuchi.business.ebookstore.views.SWBookDetailFragment
import com.sanwashoseki.bookskozuchi.business.filter.models.requests.SWFilterBookRequest
import com.sanwashoseki.bookskozuchi.business.filter.models.responses.SWFilterBookResponse
import com.sanwashoseki.bookskozuchi.business.filter.services.SWFilterBookInterface
import com.sanwashoseki.bookskozuchi.business.filter.services.SWFilterBookPresenter
import com.sanwashoseki.bookskozuchi.business.filter.views.SWFilterBookFragment
import com.sanwashoseki.bookskozuchi.business.more.services.SWSameCategoryBookInterface
import com.sanwashoseki.bookskozuchi.business.more.services.SWSameCategoryBookPresenter
import com.sanwashoseki.bookskozuchi.databinding.FragmentHighlightBookBinding
import com.sanwashoseki.bookskozuchi.others.Const
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants

class SWSameCategoryBookFragment : SWBaseFragment(), SWHighlightBooksAdapter.OnItemClickListener,
    SWMainActivity.OnHeaderClickedListener, SWFilterBookInterface,
    SWSameCategoryBookInterface {

    private var binding: FragmentHighlightBookBinding? = null
    private var adapter: SWHighlightBooksAdapter? = null
    private var preGetBook: SWSameCategoryBookPresenter? = null
    private var preFilterBook: SWFilterBookPresenter? = null

    private var isCategory: Boolean? = null
    private var isFilter: Boolean? = null
    private var filterModel: SWFilterBookRequest? = null

    private var skeletonScreen: SkeletonScreen? = null
    private var isSwipe = false

    companion object {
        @JvmStatic
        fun newInstance(category: Boolean?, filter: Boolean?, request: SWFilterBookRequest?) =
            SWSameCategoryBookFragment()
                .apply {
                    arguments = Bundle().apply {
                        isCategory = category
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
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_highlight_book, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        (activity as SWMainActivity?)?.hideBottomNavigationMenu(true)
        (activity as SWMainActivity).setWishListButton(hideWishList = true, isWishList = false)
        (activity as SWMainActivity).setHeaderInChildrenScreen("Sanwa Shoeki Publisher",
            isSearch = false,
            isFilter = true)
        (activity as SWMainActivity).onCallBackSearchListener(this)

        val ids = ArrayList<Int>()
        if (isFilter == false) {
            if (isCategory == true) {
                preGetBook?.getSameCategoryBook(context,
                    filterModel?.categoryIds!!,
                    0,
                    SWConstants.MAX_VALUE)
            } else {
                val ids = ArrayList<Int?>()
                for (i in 0 until filterModel?.publishers!!.size) {
                    ids.add(filterModel?.publishers!![i]!!.id)
                }
                preGetBook?.getSamePublisherBook(context, ids as List<Int>, 0, SWConstants.MAX_VALUE)
            }

        } else {
            if (filterModel?.publishers != null && filterModel?.publishers!!.isNotEmpty()) {
                for (i in 0 until filterModel?.publishers!!.size) {
                    ids.add(filterModel?.publishers!![i]!!.id!!)
                }
            }
            preFilterBook?.filterInSearch(context, "", filterModel?.categoryIds, filterModel?.bookType, filterModel?.priceMin, filterModel?.priceMax, ids)
        }
        binding?.let { it ->
            it.swipeRefresh.setOnRefreshListener {
                isSwipe = true
                if (isFilter == false) {
                    if (isCategory == true) {
                        preGetBook?.getSameCategoryBook(context,
                            filterModel?.categoryIds!!,
                            0,
                            SWConstants.MAX_VALUE)
                    } else {
                        val ids = ArrayList<Int?>()
                        for (i in 0 until filterModel?.publishers!!.size) {
                            ids.add(filterModel?.publishers!![i]!!.id)
                        }
                        preGetBook?.getSamePublisherBook(context, ids as List<Int>, 0, SWConstants.MAX_VALUE)
                    }
                } else {
                    preFilterBook?.filterInSearch(context, "", filterModel?.categoryIds, filterModel?.bookType, filterModel?.priceMin, filterModel?.priceMax, ids)
                }
            }
        }
    }

    private fun getListBooks() {
        binding?.let { it ->
            it.rcListBook.layoutManager = GridLayoutManager(context, 2)
            it.rcListBook.itemAnimator = DefaultItemAnimator()
            it.rcListBook.adapter = adapter

            adapter?.setOnCallBackListener(this)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        preGetBook = SWSameCategoryBookPresenter()
        preFilterBook = SWFilterBookPresenter()
        preGetBook?.attachView(this)
        preFilterBook?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        preGetBook?.detachView()
        preFilterBook?.detachView()
    }

    override fun onBackPressed(): Boolean {
//        replaceFragment(SWBookStoreFragment.newInstance(), R.id.container, true, null)
        return false
    }

    override fun onHighlightItemClicked(model: SWBookInfoResponse?, read: Boolean) {
        replaceFragment(SWBookDetailFragment.newInstance(model?.id, false),
            R.id.container,
            true,
            null)
    }

    override fun onSearchListener(content: String) {}

    override fun onShoppingCartClicked() {}

    override fun onWishListClicked(isWishList: Boolean) {}

    override fun onFilterClicked() {
        if (isCategory == true) {
            replaceFragment(SWFilterBookFragment.newInstance(false, Const.FILTER_SAME_CATEGORY, filterModel), R.id.container, true, null)
        } else {
            replaceFragment(SWFilterBookFragment.newInstance(false, Const.FILTER_SAME_PUBLISHER, filterModel), R.id.container, true, null)
        }
    }

    override fun filterSuccess(result: SWFilterBookResponse) {
        adapter = SWHighlightBooksAdapter(2, result.data)
        getListBooks()
    }

    override fun getSameCategoryBookSuccess(result: SWFilterBookResponse) {
        if (isSwipe) {
            binding?.swipeRefresh?.isRefreshing = false
        }
        adapter = SWHighlightBooksAdapter(2, result.data)
        getListBooks()
    }

    override fun getSamePublisherBookSuccess(result: SWFilterBookResponse) {
        if (isSwipe) {
            binding?.swipeRefresh?.isRefreshing = false
        }
        adapter = SWHighlightBooksAdapter(2, result.data)
        getListBooks()
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun showIndicator() {
        if (!isSwipe) {
            binding?.rcListBook?.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
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
//        showLoading(true, getString(R.string.book_store_highlight), getString(R.string.dialog_loading_content))
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