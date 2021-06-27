package com.sanwashoseki.bookskozuchi.business.ebookstore.views

import android.annotation.SuppressLint
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
import com.sanwashoseki.bookskozuchi.business.ebookstore.adapters.SWChildCategoriesBooksAdapter
import com.sanwashoseki.bookskozuchi.business.ebookstore.adapters.SWParentCategoryAdapter
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookInfoResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWChildCategoriesBookResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.services.SWChildCategoriesBookInterface
import com.sanwashoseki.bookskozuchi.business.ebookstore.services.SWChildCategoriesBookPresenter
import com.sanwashoseki.bookskozuchi.business.filter.views.SWFilterBookFragment
import com.sanwashoseki.bookskozuchi.databinding.FragmentCategoryBookBinding
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.utilities.Sharepref

class SWCategoriesBookFragment : SWBaseFragment(), SWChildCategoriesBookInterface,
    SWMainActivity.OnHeaderClickedListener, SWParentCategoryAdapter.OnItemClickListener,
    SWChildCategoriesBooksAdapter.OnCategoriesItemClickedListener, View.OnClickListener {

    private var binding: FragmentCategoryBookBinding? = null
    private var presenter: SWChildCategoriesBookPresenter? = null
    private var childCategoryAdapter: SWChildCategoriesBooksAdapter? = null
    private var parentCategoryAdapter: SWParentCategoryAdapter? = null

    private var idCategory: Int? = null

    companion object {
        @JvmStatic
        fun newInstance(id: Int?) =
            SWCategoriesBookFragment().apply {
                arguments = Bundle().apply {
                    idCategory = id
                }
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category_book, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        (activity as SWMainActivity?)?.hideBottomNavigationMenu(true)
//        (activity as SWMainActivity).setHeaderInChildrenScreen(getString(R.string.book_store_category), isSearch = false, isFilter = false)
        (activity as SWMainActivity).onCallBackSearchListener(this)

        Sharepref.setCategoryId(context,  idCategory.toString())
        presenter?.getCategoryBook(context, Sharepref.getCategoryId(context)!!.toInt(), 10)
        binding?.btnMore?.setOnClickListener(this)
    }

    private fun getCategories() {
        binding?.let { it ->
            it.rcCategory.layoutManager = LinearLayoutManager(context)
            it.rcCategory.itemAnimator = DefaultItemAnimator()
            it.rcCategory.adapter = childCategoryAdapter
        }
        childCategoryAdapter?.onCallBackCategoriesClicked(this)
    }

    private fun getProductCategories() {
        val manager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding?.let { it ->
            it.rcCategoryProduct.layoutManager = manager
            it.rcCategoryProduct.itemAnimator = DefaultItemAnimator()
            it.rcCategoryProduct.adapter = parentCategoryAdapter
        }
        parentCategoryAdapter?.setOnCallBackListener(this)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWChildCategoriesBookPresenter()
        presenter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    @SuppressLint("SetTextI18n")
    override fun getCategorySuccess(result: SWChildCategoriesBookResponse) {
        (activity as SWMainActivity).setHeaderInChildrenScreen(result.data?.name!!, isSearch = false, isFilter = false)
        binding?.let { it ->
            if (result.data?.subCategories!!.isEmpty() && result.data?.products!!.isEmpty()) {
                it.tvNull.visibility = View.VISIBLE
                it.rcCategory.visibility = View.GONE
            } else {
                if (result.data?.subCategories!!.isNotEmpty() && result.data?.products!!.isNotEmpty()) {
                    it.layoutCategoryProduct.visibility = View.VISIBLE
                    it.tvCategoryName.text = "#" + result.data?.name
                    parentCategoryAdapter = SWParentCategoryAdapter(1, result.data?.products)
                    getProductCategories()
                }
                childCategoryAdapter = if (result.data?.subCategories!!.isNotEmpty()) {
                    SWChildCategoriesBooksAdapter(1, result.data!!.subCategories, result.data)
                } else {
                    SWChildCategoriesBooksAdapter(2, result.data!!.subCategories, result.data)
                }
                getCategories()
            }
        }
    }

    override fun onLoadMoreSuccess(result: SWChildCategoriesBookResponse) {}

    override fun showMessageError(msg: String) {
//        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun showIndicator() {
        showLoading(true, getString(R.string.book_store_category), getString(R.string.dialog_loading_content))
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
        replaceFragment(SWFilterBookFragment.newInstance(true, Const.FILTER_CATEGORY, null), R.id.container, false, null)
    }

    override fun onSeeMoreClicked(id: Int?) {
        replaceFragment(SWChildCategoriesBookDetailFragment.newInstance(id, false, null), R.id.container, true, null)
    }

    override fun onDetailBookClickedListener(detail: SWBookInfoResponse?) {
        replaceFragment(SWBookDetailFragment.newInstance(detail?.id, false), R.id.container, false, null)
    }

    override fun onParentCategoryItemClicked(model: SWBookInfoResponse?, read: Boolean) {
        replaceFragment(SWBookDetailFragment.newInstance(model?.id, false), R.id.container, false, null)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnMore -> replaceFragment(SWChildCategoriesBookDetailFragment.newInstance(idCategory, false, null), R.id.container, true, null)
        }
    }
}