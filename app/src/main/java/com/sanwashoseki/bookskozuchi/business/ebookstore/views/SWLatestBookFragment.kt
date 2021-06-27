package com.sanwashoseki.bookskozuchi.business.ebookstore.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants
import com.sanwashoseki.bookskozuchi.business.ebookstore.adapters.SWLatestBooksAdapter
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookInfoResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookStoreResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.services.SWBookStoreLatestInterface
import com.sanwashoseki.bookskozuchi.business.ebookstore.services.SWBookStoreLatestPresenter
import com.sanwashoseki.bookskozuchi.business.filter.models.requests.SWFilterBookRequest
import com.sanwashoseki.bookskozuchi.business.filter.models.responses.SWFilterBookResponse
import com.sanwashoseki.bookskozuchi.business.filter.services.SWFilterBookInterface
import com.sanwashoseki.bookskozuchi.business.filter.services.SWFilterBookPresenter
import com.sanwashoseki.bookskozuchi.business.filter.views.SWFilterBookFragment
import com.sanwashoseki.bookskozuchi.databinding.FragmentLatestBookBinding
import com.sanwashoseki.bookskozuchi.others.Const

class SWLatestBookFragment : SWBaseFragment(), SWLatestBooksAdapter.OnItemClickListener,
    SWBookStoreLatestInterface, SWMainActivity.OnHeaderClickedListener, SWFilterBookInterface {

    private var binding: FragmentLatestBookBinding? = null
    private var adapter: SWLatestBooksAdapter? = null
    private var preGetLatest: SWBookStoreLatestPresenter? = null
    private var preFilterBook: SWFilterBookPresenter? = null

    private var isFilter: Boolean? = null
    private var filterModel: SWFilterBookRequest? = null

    private var skeletonScreen: SkeletonScreen? = null
    private var isSwipe = false
    private var isLoading = false

    companion object {
        @JvmStatic
        fun newInstance(filter: Boolean?, request: SWFilterBookRequest?) =
            SWLatestBookFragment().apply {
                arguments = Bundle().apply {
                    isFilter = filter
                    filterModel = request
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_latest_book, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        (activity as SWMainActivity?)?.hideBottomNavigationMenu(true)
        (activity as SWMainActivity).setHeaderInChildrenScreen(
            getString(R.string.book_store_latest),
            isSearch = false,
            isFilter = true
        )
        (activity as SWMainActivity).onCallBackSearchListener(this)

        val ids = ArrayList<Int>()
        if (isFilter == false) {
            preGetLatest?.getBookStoreLatest(context, SWConstants.MAX_VALUE, true)
        } else {
            if (filterModel?.publishers != null && filterModel?.publishers!!.isNotEmpty()) {
                for (i in 0 until filterModel?.publishers!!.size) {
                    ids.add(filterModel?.publishers!![i]!!.id!!)
                }
            }
            preFilterBook?.filterLatestBook(
                context,
                true,
                filterModel?.categoryIds,
                filterModel?.bookType,
                filterModel?.priceMin,
                filterModel?.priceMax,
                ids
            )
        }
        binding?.let { it ->
            it.swipeRefresh.setOnRefreshListener {
                isSwipe = true
                if (isFilter == false) {
                    preGetLatest?.getBookStoreLatest(context, SWConstants.MAX_VALUE, true)
                } else {
                    preFilterBook?.filterLatestBook(
                        context,
                        true,
                        filterModel?.categoryIds,
                        filterModel?.bookType,
                        filterModel?.priceMin,
                        filterModel?.priceMax,
                        ids
                    )
                }
            }
        }

        binding?.rcLatestBook?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                var currentItem = getCurrentItem()
                if (!isLoading && currentItem == (adapter?.itemCount?.minus(8))) {
                    isLoading = true
                    preGetLatest?.loadMoreBooks(context, SWConstants.MAX_VALUE)
                }
            }
        })
    }

    private fun getCurrentItem(): Int {
        return (binding?.rcLatestBook?.getLayoutManager() as LinearLayoutManager)
            .findFirstVisibleItemPosition()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        preGetLatest = SWBookStoreLatestPresenter()
        preFilterBook = SWFilterBookPresenter()
        preGetLatest?.attachView(this)
        preFilterBook?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        preGetLatest?.detachView()
        preFilterBook?.detachView()
    }

    private fun getLatestBooks() {
        binding?.let { it ->
            it.rcLatestBook.layoutManager = GridLayoutManager(context, 2)
            it.rcLatestBook.itemAnimator = DefaultItemAnimator()
            it.rcLatestBook.adapter = adapter
        }

        adapter?.setOnCallBackListener(this)
    }

    override fun onBackPressed(): Boolean {
//        replaceFragment(SWBookStoreFragment.newInstance(), R.id.container, true, null)
        return false
    }

    override fun onLatestItemClicked(model: SWBookInfoResponse?) {
        replaceFragment(
            SWBookDetailFragment.newInstance(model?.id, false),
            R.id.container,
            false,
            null
        )
    }

    override fun getLatestSuccess(result: SWBookStoreResponse) {
        if (isSwipe) {
            binding?.swipeRefresh?.isRefreshing = false
        }
        adapter = SWLatestBooksAdapter(2, result.data)
        getLatestBooks()
    }

    override fun filterSuccess(result: SWFilterBookResponse) {
        adapter = SWLatestBooksAdapter(2, result.data)
        getLatestBooks()
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun onLoadMoreSuccess(result: SWBookStoreResponse) {
        isLoading = false
        result!!.data?.let { adapter?.addMoreData(it) }
    }

    override fun showIndicator() {
        if (!isSwipe) {
            binding?.rcLatestBook?.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            skeletonScreen = Skeleton.bind(binding?.rcLatestBook)
                .adapter(adapter)
                .shimmer(true)
                .angle(20)
                .frozen(false)
                .duration(1200)
                .count(10)
                .load(R.layout.skeleton_recycle_item_high_light_books)
                .show()
        }
//        showLoading(true, getString(R.string.book_store_latest), getString(R.string.dialog_loading_content))
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
        replaceFragment(
            SWFilterBookFragment.newInstance(false, Const.FILTER_LATEST, filterModel),
            R.id.container,
            true,
            null
        )
    }
}