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
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants
import com.sanwashoseki.bookskozuchi.business.ebookstore.adapters.SWChildCategoriesBooksAdapter
import com.sanwashoseki.bookskozuchi.business.ebookstore.adapters.SWHighlightBooksAdapter
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookInfoResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWChildCategoriesBookResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.services.SWChildCategoriesBookInterface
import com.sanwashoseki.bookskozuchi.business.ebookstore.services.SWChildCategoriesBookPresenter
import com.sanwashoseki.bookskozuchi.business.filter.models.requests.SWFilterBookRequest
import com.sanwashoseki.bookskozuchi.business.filter.models.responses.SWFilterBookResponse
import com.sanwashoseki.bookskozuchi.business.filter.services.SWFilterBookInterface
import com.sanwashoseki.bookskozuchi.business.filter.services.SWFilterBookPresenter
import com.sanwashoseki.bookskozuchi.business.filter.views.SWFilterBookFragment
import com.sanwashoseki.bookskozuchi.databinding.FragmentHighlightBookBinding
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.utilities.Sharepref

class SWChildCategoriesBookDetailFragment : SWBaseFragment(), SWChildCategoriesBookInterface,
    SWMainActivity.OnHeaderClickedListener,
    SWChildCategoriesBooksAdapter.OnCategoriesItemClickedListener,
    SWHighlightBooksAdapter.OnItemClickListener, SWFilterBookInterface {

    private var binding: FragmentHighlightBookBinding? = null
    private var preCategory: SWChildCategoriesBookPresenter? = null
    private var adapter: SWHighlightBooksAdapter? = null
    private var preFilterBook: SWFilterBookPresenter? = null

    private var isFilter: Boolean? = null
    private var filterModel: SWFilterBookRequest? = null

    private var idCategory: Int? = null
    private var isLoading = false

    companion object {
        @JvmStatic
        fun newInstance(id: Int?, filter: Boolean?, request: SWFilterBookRequest?) =
            SWChildCategoriesBookDetailFragment().apply {
                arguments = Bundle().apply {
                    idCategory = id
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
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_highlight_book, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        (activity as SWMainActivity?)?.hideBottomNavigationMenu(true)
        (activity as SWMainActivity).setHeaderInChildrenScreen(
            Sharepref.getSubCategoryName(context)!!,
            isSearch = false,
            isFilter = true
        )
        (activity as SWMainActivity).onCallBackSearchListener(this)

        if (isFilter == false) {
            preCategory?.getCategoryBook(context, idCategory!!, SWConstants.MAX_VALUE)
            Sharepref.setSubCategoryId(context, idCategory.toString())
        } else {
            if (filterModel == null) {
                preCategory?.getCategoryBook(
                    context,
                    Sharepref.getSubCategoryId(context)?.toInt()!!,
                    SWConstants.MAX_VALUE
                )
            } else {
                val ids = ArrayList<Int>()
                if (filterModel?.publishers != null && filterModel?.publishers!!.isNotEmpty()) {
                    for (i in 0 until filterModel?.publishers!!.size) {
                        ids.add(filterModel?.publishers!![i]!!.id!!)
                    }
                }
                preFilterBook?.filterInCategory(
                    context,
                    Sharepref.getSubCategoryId(context)?.toInt(),
                    filterModel?.bookType,
                    filterModel?.priceMin,
                    filterModel?.priceMax,
                    ids
                )
            }
        }

        binding?.rcListBook?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                var currentItem = getCurrentItem()
                if (!isLoading && currentItem == (adapter?.itemCount?.minus(8))) {
                    isLoading = true
                    preCategory?.loadMoreBooks(context, idCategory!!, SWConstants.MAX_VALUE)
                }
            }
        })
    }

    private fun getCurrentItem(): Int {
        return (binding?.rcListBook?.getLayoutManager() as LinearLayoutManager)
            .findFirstVisibleItemPosition()
    }

    private fun getCategories() {
        binding?.rcListBook?.layoutManager = GridLayoutManager(context, 2)
        binding?.rcListBook?.itemAnimator = DefaultItemAnimator()
        binding?.rcListBook?.adapter = adapter

        adapter?.setOnCallBackListener(this)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        preCategory = SWChildCategoriesBookPresenter()
        preFilterBook = SWFilterBookPresenter()
        preCategory?.attachView(this)
        preFilterBook?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        preCategory?.detachView()
        preFilterBook?.detachView()
    }

    override fun onBackPressed(): Boolean {
        Sharepref.setSubCategoryId(context, "")
        Sharepref.setSubCategoryName(context, "")
        replaceFragment(
            SWCategoriesBookFragment.newInstance(
                Sharepref.getCategoryId(context)!!.toInt()
            ), R.id.container, true, null
        )
        return true
    }

    override fun getCategorySuccess(result: SWChildCategoriesBookResponse) {
        (activity as SWMainActivity).setHeaderInChildrenScreen(
            result.data?.name!!,
            isSearch = false,
            isFilter = true
        )
        Sharepref.setSubCategoryName(context, result.data?.name!!)
        adapter = SWHighlightBooksAdapter(2, result.data?.products)
        getCategories()
    }

    override fun onLoadMoreSuccess(result: SWChildCategoriesBookResponse) {
        isLoading = false
        result.data?.products?.let { adapter?.addMoreData(it) }
    }

    override fun filterSuccess(result: SWFilterBookResponse) {
//        (activity as SWMainActivity).setHeader(false, result.data?.name!!, false)
        adapter = SWHighlightBooksAdapter(2, result.data)
        getCategories()
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun showIndicator() {
        showLoading(
            true,
            getString(R.string.book_store_category),
            getString(R.string.dialog_loading_content)
        )
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
            SWFilterBookFragment.newInstance(true, Const.FILTER_CATEGORY, filterModel),
            R.id.container,
            true,
            null
        )
    }

    override fun onSeeMoreClicked(id: Int?) {
//        presenter?.getCategoryBook(context, id!!, SWConstants.MAX_VALUE)
    }

    override fun onDetailBookClickedListener(detail: SWBookInfoResponse?) {
        replaceFragment(
            SWBookDetailFragment.newInstance(detail?.id, false),
            R.id.container,
            false,
            null
        )
    }

    override fun onHighlightItemClicked(model: SWBookInfoResponse?, read: Boolean) {
        replaceFragment(
            SWBookDetailFragment.newInstance(model?.id, false),
            R.id.container,
            false,
            null
        )
    }

}