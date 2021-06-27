package com.sanwashoseki.bookskozuchi.business.search.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookInfoResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.views.SWBookDetailFragment
import com.sanwashoseki.bookskozuchi.business.filter.models.requests.SWFilterBookRequest
import com.sanwashoseki.bookskozuchi.business.filter.models.responses.SWFilterBookResponse
import com.sanwashoseki.bookskozuchi.business.filter.services.SWFilterBookInterface
import com.sanwashoseki.bookskozuchi.business.filter.services.SWFilterBookPresenter
import com.sanwashoseki.bookskozuchi.business.filter.views.SWFilterBookFragment
import com.sanwashoseki.bookskozuchi.business.search.adapters.SWSearchBookAdapter
import com.sanwashoseki.bookskozuchi.business.search.models.responses.SWSearchBookResponse
import com.sanwashoseki.bookskozuchi.business.search.services.SWSearchBookInterface
import com.sanwashoseki.bookskozuchi.business.search.services.SWSearchBookPresenter
import com.sanwashoseki.bookskozuchi.databinding.FragmentSearchBookBinding
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.utils.Utils
import com.sanwashoseki.bookskozuchi.utilities.SWUIHelper

class SWSearchBookFragment : SWBaseFragment(), View.OnClickListener,
    SWMainActivity.OnHeaderClickedListener, SWSearchBookInterface,
    SWSearchBookAdapter.OnItemClickedListener, SWFilterBookInterface {

    private var binding: FragmentSearchBookBinding? = null
    private var preSearch: SWSearchBookPresenter? = null
    private var adapter: SWSearchBookAdapter? = null
    private var preFilterBook: SWFilterBookPresenter? = null

    private var skeletonScreen: SkeletonScreen? = null

    private var isFilter: Boolean? = null
    private var filterModel: SWFilterBookRequest? = null
    private var keySearch = ""
    private var isLoading = false

    companion object {
        @JvmStatic
        fun newInstance(filter: Boolean?, request: SWFilterBookRequest?) =
            SWSearchBookFragment().apply {
                arguments = Bundle().apply {
                    isFilter = filter
                    filterModel = request
                    Log.d("TAG", "newInstance: ${filterModel?.publishers}")
                }
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_book, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        (activity as SWMainActivity?)?.hideBottomNavigationMenu(true)
        (activity as SWMainActivity).setHeaderInChildrenScreen("", isSearch = true, isFilter = true)
        (activity as SWMainActivity).onCallBackSearchListener(this)

        Log.d("TAG", "initUI: $filterModel")

        keySearch = Sharepref.getKeySearch(context).toString()
//        if (keySearch != "") {
            if (isFilter == false) {
                preSearch?.searchBook(context, keySearch)
            } else {
                val publisherId = ArrayList<Int>()
                if (filterModel?.publishers != null) {
                    for (i in 0 until filterModel?.publishers!!.size) {
                        publisherId.add(filterModel?.publishers!![i]?.id!!)
                    }
                }
                preFilterBook?.filterInSearch(context, keySearch, filterModel?.categoryIds, filterModel?.bookType, filterModel?.priceMin, filterModel?.priceMax, publisherId)
            }
//        }

        binding?.let { it ->
            it.container.setOnClickListener(this)
        }

        binding?.rcSearchResponse?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                var currentItem = getCurrentItem()
                if (!isLoading && currentItem == (adapter?.itemCount?.minus(16))) {
                    isLoading = true
                    preSearch?.loadMoreBooks(context, keySearch)
                }
            }
        })
    }

    private fun getCurrentItem(): Int {
        return (binding?.rcSearchResponse?.getLayoutManager() as LinearLayoutManager)
            .findFirstVisibleItemPosition()
    }

    private fun getListBooks() {
        binding?.let { it ->
            var spanCount = 3
            if (SWUIHelper.isTablet(requireContext())) {
                spanCount = 4
            }
            it.rcSearchResponse.layoutManager = GridLayoutManager(context, spanCount)
            it.tvResult.visibility = View.VISIBLE
            it.rcSearchResponse.itemAnimator = DefaultItemAnimator()
            it.rcSearchResponse.adapter = adapter

            adapter?.onCallBackListener(this)
        }
    }

    override fun onLoadMoreSuccess(result: SWSearchBookResponse?) {
        isLoading = false
        result?.data?.let { adapter?.addMoreData(it) }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        preSearch = SWSearchBookPresenter()
        preFilterBook = SWFilterBookPresenter()
        preSearch?.attachView(this)
        preFilterBook?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        preSearch?.detachView()
        preFilterBook?.detachView()
    }

    override fun onBackPressed(): Boolean {
        Sharepref.setKeySearch(context, "")
        (activity as SWMainActivity).removeSearch()
        return false
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.container -> hideSoftKeyBoard(context, binding?.container!!)
        }
    }

    override fun onSearchListener(content: String) {
        keySearch = content
        Sharepref.setKeySearch(context, keySearch)
        hideSoftKeyBoard(context, binding?.container!!)
        preSearch?.searchBook(context, content)
    }

    override fun onShoppingCartClicked() {}

    override fun onWishListClicked(isWishList: Boolean) {}

    override fun onFilterClicked() {
        Log.d("TAG", "onFilterClicked: hfshlkdjflk")
        replaceFragment(SWFilterBookFragment.newInstance(false, Const.FILTER_SEARCH_AUTHOR_NAME, filterModel), R.id.container, true, null)
    }

    @SuppressLint("SetTextI18n")
    override fun searchSuccess(result: SWSearchBookResponse) {
        binding?.tvResult?.text = result.total.toString() + "冊の結果"
        adapter = SWSearchBookAdapter(result.data)
        getListBooks()
    }

    @SuppressLint("SetTextI18n")
    override fun filterSuccess(result: SWFilterBookResponse) {
        binding?.tvResult?.text = result.data?.size.toString() + "冊の結果"
        adapter = SWSearchBookAdapter(result.data)
        getListBooks()
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.search_title), msg)
    }

    override fun showIndicator() {
        binding?.rcSearchResponse?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        skeletonScreen = Skeleton.bind(binding?.rcSearchResponse)
            .adapter(adapter)
            .shimmer(true)
            .angle(20)
            .frozen(false)
            .duration(1200)
            .count(10)
            .load(R.layout.skeleton_recycle_item_high_light_books)
            .show()

//        showLoading(true, getString(R.string.search_title), getString(R.string.dialog_loading_content))
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

    override fun onItemClickedListener(result: SWBookInfoResponse?) {
        replaceFragment(SWBookDetailFragment.newInstance(result?.id, false), R.id.container, false, null)
    }

}