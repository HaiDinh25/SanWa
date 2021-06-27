package com.sanwashoseki.bookskozuchi.business.filter.views

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
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWCategoriesResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.services.SWBookStoreCategoriesInterface
import com.sanwashoseki.bookskozuchi.business.ebookstore.services.SWBookStoreCategoriesPresenter
import com.sanwashoseki.bookskozuchi.business.ebookstore.views.SWChildCategoriesBookDetailFragment
import com.sanwashoseki.bookskozuchi.business.ebookstore.views.SWHighlightBookFragment
import com.sanwashoseki.bookskozuchi.business.ebookstore.views.SWLatestBookFragment
import com.sanwashoseki.bookskozuchi.business.ebookstore.views.SWTopSellersFragment
import com.sanwashoseki.bookskozuchi.business.filter.adapters.SWCategoryInFilterAdapter
import com.sanwashoseki.bookskozuchi.business.filter.models.requests.SWFilterBookRequest
import com.sanwashoseki.bookskozuchi.business.filter.models.responses.SWFilterBookResponse
import com.sanwashoseki.bookskozuchi.business.filter.services.SWFilterBookInterface
import com.sanwashoseki.bookskozuchi.business.filter.services.SWFilterBookPresenter
import com.sanwashoseki.bookskozuchi.business.more.views.SWSameCategoryBookFragment
import com.sanwashoseki.bookskozuchi.business.search.views.SWSearchBookFragment
import com.sanwashoseki.bookskozuchi.business.shoppingcart.views.SWSearchSimilarBookFragment
import com.sanwashoseki.bookskozuchi.databinding.FragmentFilterBookBinding
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.utilities.SWUIHelper
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import kotlin.collections.ArrayList

class SWFilterBookFragment : SWBaseFragment(), SWBookStoreCategoriesInterface,
    SWCategoryInFilterAdapter.OnCategoryItemClicked, View.OnClickListener, SWFilterBookInterface {

    private var binding: FragmentFilterBookBinding? = null
    private var preCategory: SWBookStoreCategoriesPresenter? = null
    private var preFilter: SWFilterBookPresenter? = null
    private var adapter: SWCategoryInFilterAdapter? = null

    private var bookType: Boolean? = null
    private var isAudio = false
    private var isBook = false
    private var priceMin = ""
    private var priceMax = ""
    private var isCategory: Boolean? = null

    private var categoryIds = ArrayList<Int>()
    private var publisherIds = ArrayList<Int>()
    private var publisherNames = ArrayList<String?>()

    private var filterModel: SWFilterBookRequest? = null
    private var filterKey: String? = null

    private var skeletonScreen: SkeletonScreen? = null

    companion object {
        @JvmStatic
        fun newInstance(category: Boolean, key: String?, filter: SWFilterBookRequest?) =
            SWFilterBookFragment().apply {
                arguments = Bundle().apply {
                    isCategory = category
                    filterKey = key
                    filterModel = filter
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter_book, container, false)

        initUI()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (filterKey == Const.FILTER_SAME_CATEGORY) {
            binding?.layoutCategory?.visibility = View.GONE
        } else {
            if (filterKey == Const.FILTER_SAME_PUBLISHER) {
                binding?.layoutPublisher?.visibility = View.GONE
            }
            preCategory?.getCategories(context, true)
        }
    }

    fun initUI() {
        (activity as SWMainActivity?)?.hideBottomNavigationMenu(true)
        (activity as SWMainActivity).setHeaderInChildrenScreen(getString(R.string.filter_title),
            isSearch = false,
            isFilter = false)

        binding?.let { it ->
            if (filterModel != null) {
                if (filterModel?.publishers != null && filterModel?.publishers!!.isNotEmpty()) {
                    for (i in 0 until filterModel?.publishers!!.size) {
                        publisherIds.add(filterModel?.publishers!![i]?.id!!)
                        publisherNames.add(filterModel?.publishers!![i]?.vendorName)
                    }
                }
                if (filterModel?.categoryIds != null && filterModel?.categoryIds!!.isNotEmpty()) {
                    for (element in filterModel?.categoryIds!!) {
                        categoryIds.add(element)
                    }
                }
                if (filterModel?.priceMin != null && filterModel?.priceMin != "") {
                    priceMin = filterModel?.priceMin.toString()
                    it.edtPriceMin.setText(SWUIHelper.formatTextPrice(priceMin.toDouble()))
                }
                if (filterModel?.priceMax != null && filterModel?.priceMax != "") {
                    priceMax = filterModel?.priceMax.toString()
                    it.edtPriceMax.setText(SWUIHelper.formatTextPrice(priceMax.toDouble()))
                }
                Log.d("TAG", "initUI: " + filterModel?.bookType)
                if (filterModel?.bookType != null) {
                    if (filterModel?.bookType == true) {
                        isAudio = true
                        isBook = false
                        setBookAudioButton(true)
                    } else {
                        isAudio = false
                        isBook = true
                        setBookButton(true)
                    }
                } else {
                    setBookButton(false)
                    setBookAudioButton(false)
                    isAudio = false
                    isBook = false
                    bookType = null
                }
            }
            if (publisherNames.size != 0) {
                it.tvPublisher.visibility = View.VISIBLE
                var subContent = ""
                for (i in 0 until publisherNames.size) {
                    if (i == 0) {
                        subContent = publisherNames[i].toString()
                    } else {
                        if (subContent != "") {
                            subContent = subContent + "、 " + publisherNames[i].toString()
                        }
                    }
                }
                it.tvPublisher.text = subContent
            } else {
                it.tvPublisher.visibility = View.GONE
            }
            if (isCategory!!) {
                it.layoutCategory.visibility = View.GONE
            } else {
                it.layoutCategory.visibility = View.VISIBLE
            }

            it.edtPriceMin.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    priceMin = it.edtPriceMin.text.toString().replace("[($., )]".toRegex(), "")
                    filterModel = SWFilterBookRequest(filterModel?.categoryIds,
                        bookType,
                        priceMin,
                        filterModel?.priceMax,
                        filterModel?.publishers)
                }
            })

            it.edtPriceMax.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    priceMax = it.edtPriceMax.text.toString().replace("[($., )]".toRegex(), "")
                    filterModel = SWFilterBookRequest(filterModel?.categoryIds,
                        bookType,
                        priceMin,
                        priceMax,
                        filterModel?.publishers)
                }
            })

            it.btnBook.setOnClickListener(this)
            it.btnAudioBook.setOnClickListener(this)
            it.btnPublisher.setOnClickListener(this)
            it.btnClear.setOnClickListener(this)
            it.btnApply.setOnClickListener(this)
        }
    }

    private fun getCategory() {
        binding?.rcCategory?.layoutManager = LinearLayoutManager(context)
        binding?.rcCategory?.itemAnimator = DefaultItemAnimator()
        binding?.rcCategory?.adapter = adapter

        adapter?.onCallbackListener(this)
    }

    override fun onCategoryClickedListener(idCategory: Int?, name: String?, subCategory: SWCategoriesResponse.Data?, ) {
        replaceFragment(SWFilterSubCategoryFragment.newInstance(idCategory,
            name,
            subCategory,
            filterModel,
            isCategory,
            filterKey), R.id.container, true, null)
        Log.d("TAG", "onClick: $filterModel")
    }

    override fun onCheckAllListener(idCategory: Int?, subCategory: List<SWCategoriesResponse.Data.SubCategories>?, ) {
        if (!categoryIds.contains(idCategory)) {
            categoryIds.add(idCategory!!)
        }
        for (i in subCategory!!.indices) {
            if (!categoryIds.contains(subCategory[i].id)) {
                categoryIds.add(subCategory[i].id!!)
            }
        }
        filterModel = SWFilterBookRequest(categoryIds, bookType, priceMin, priceMax, filterModel?.publishers)
    }

    override fun onUnCheckAllListener(idCategory: Int?, subCategory: List<SWCategoriesResponse.Data.SubCategories>?) {
        val ids = ArrayList<Int?>()
        ids.add(idCategory)
        for (i in subCategory!!.indices) {
            ids.add(subCategory[i].id)
        }
        categoryIds.removeAll(ids)
        filterModel = SWFilterBookRequest(categoryIds, bookType, priceMin, priceMax, filterModel?.publishers)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnBook -> {
                isBook = !isBook
                setBookButton(isBook)
            }
            R.id.btnAudioBook -> {
                isAudio = !isAudio
                setBookAudioButton(isAudio)
            }
            R.id.btnPublisher -> {
                binding?.tvPublisher?.visibility = View.GONE
                replaceFragment(
                    SWPublisherFragment.newInstance(
                        filterModel,
                        isCategory,
                        filterKey
                    ), (R.id.container), true, null
                )
            }
            R.id.btnApply -> {
                if (priceMax.isEmpty()) {
                    priceMax = Int.MAX_VALUE.toString()
                }
                if (priceMin > priceMax) {
                    showMessageError(getString(R.string.filter_validate_price))
                } else {
                    isBook = false
                    isAudio = false
                    onBackFragment()
                }
            }
            R.id.btnClear -> {
                clearDataFilter()
            }
        }
    }

    private fun onBackFragment() {
        when (filterKey) {
            Const.FILTER_HIGH_LIGHT -> replaceFragment(
                SWHighlightBookFragment.newInstance(
                    true,
                    filterModel
                ), R.id.container, true, null
            )
            Const.FILTER_LATEST -> replaceFragment(
                SWLatestBookFragment.newInstance(
                    true,
                    filterModel
                ), R.id.container, true, null
            )
            Const.FILTER_TOP_SELLER -> replaceFragment(
                SWTopSellersFragment.newInstance(
                    true,
                    filterModel
                ), R.id.container, true, null
            )
            Const.FILTER_CATEGORY -> replaceFragment(
                SWChildCategoriesBookDetailFragment.newInstance(
                    null,
                    true,
                    filterModel
                ), R.id.container, true, null
            )
            Const.FILTER_SEARCH_AUTHOR_NAME -> replaceFragment(
                SWSearchBookFragment.newInstance(
                    true,
                    filterModel
                ), R.id.container, true, null
            )
            Const.FILTER_SIMILAR -> replaceFragment(
                SWSearchSimilarBookFragment.newInstance(
                    19,
                    filterModel
                ), R.id.container, true, null
            )
            Const.FILTER_LIBRARY -> (activity as SWMainActivity).setBottomNavigationMenu(2)
            Const.FILTER_SAME_CATEGORY -> replaceFragment(
                SWSameCategoryBookFragment.newInstance(
                    true, filter = true,
                    request = filterModel
                ), R.id.container, true, null
            )
            Const.FILTER_SAME_PUBLISHER -> replaceFragment(
                SWSameCategoryBookFragment.newInstance(
                    false, filter = true,
                    request = filterModel
                ), R.id.container, true, null
            )
        }
    }

    private fun setBookButton(isSelect: Boolean) {
        binding?.let { it ->
            if (isSelect) {
                it.btnBook.setBackgroundResource(R.drawable.bg_button_red_border)
                it.checkBook.visibility = View.VISIBLE
                if (!isAudio) {
                    bookType = false
                }
            } else {
                it.btnBook.setBackgroundResource(R.drawable.bg_button_blur_border)
                it.checkBook.visibility = View.GONE
                if (isAudio) {
                    bookType = true
                }
            }
            if ((isBook && isAudio) || (!isBook && !isAudio)) {
                bookType = null
            }
            filterModel = SWFilterBookRequest(filterModel?.categoryIds,
                bookType,
                priceMin,
                priceMax,
                filterModel?.publishers)
        }
    }

    private fun setBookAudioButton(isSelect: Boolean) {
        binding?.let { it ->
            if (isSelect) {
                it.btnAudioBook.setBackgroundResource(R.drawable.bg_button_red_border)
                it.checkAudio.visibility = View.VISIBLE
                if (!isBook) {
                    bookType = true
                }
            } else {
                it.btnAudioBook.setBackgroundResource(R.drawable.bg_button_blur_border)
                it.checkAudio.visibility = View.GONE
                if (isBook) {
                    bookType = false
                }
            }
            if ((isBook && isAudio) || (!isBook && !isAudio)) {
                bookType = null
            }
            filterModel = SWFilterBookRequest(filterModel?.categoryIds,
                bookType,
                priceMin,
                priceMax,
                filterModel?.publishers)
        }
    }

    private fun clearDataFilter() {
        binding?.let { it ->
            filterModel?.publishers?.clear()
            it.edtPriceMin.text?.clear()
            it.edtPriceMax.text?.clear()
            categoryIds.removeAll(categoryIds)
            publisherIds.removeAll(publisherIds)
            it.tvPublisher.visibility = View.GONE
            isBook = false
            isAudio = false
            setBookButton(isBook)
            setBookAudioButton(isAudio)
//            adapter?.notifyDataSetChanged()

            preCategory?.getCategories(context, false)

            filterModel?.categoryIds = categoryIds

            Log.d("TAG", "clearDataFilter: $filterModel")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        preCategory = SWBookStoreCategoriesPresenter()
        preFilter = SWFilterBookPresenter()
        preCategory?.attachView(this)
        preFilter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        preCategory?.detachView()
        preFilter?.detachView()
    }

    override fun onBackPressed(): Boolean {
        if (filterKey != Const.FILTER_SAME_CATEGORY && filterKey != Const.FILTER_SAME_PUBLISHER) {
            filterModel = null
        }
        onBackFragment()
        return true
    }

    override fun getCategoriesSuccess(result: SWCategoriesResponse) {
        adapter = SWCategoryInFilterAdapter(result.data, categoryIds)
        skeletonScreen?.hide()
        getCategory()
        hideLoading()
    }

    override fun filterSuccess(result: SWFilterBookResponse) {
        replaceFragment(SWFilterResponseFragment.newInstance(result), R.id.container, false, null)
        clearDataFilter()
    }

    override fun showMessageError(msg: String) {
//        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun showIndicator() {
        skeletonScreen = Skeleton.bind(binding?.rootView)
            .load(R.layout.skeleton_fragment_filter_book)
            .duration(1200)
            .color(R.color.colorSkeleton)
            .angle(0)
            .show()
//        showLoading(true, getString(R.string.filter_title), getString(R.string.dialog_loading_content))
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