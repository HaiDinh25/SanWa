package com.sanwashoseki.bookskozuchi.business.filter.views

import android.os.Bundle
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
import com.sanwashoseki.bookskozuchi.business.filter.adapters.SWSubCategoryInFilterAdapter
import com.sanwashoseki.bookskozuchi.business.filter.models.requests.SWFilterBookRequest
import com.sanwashoseki.bookskozuchi.databinding.FragmentFilterCategoryBinding
import java.util.stream.Collectors

class SWFilterSubCategoryFragment : SWBaseFragment(),
    SWSubCategoryInFilterAdapter.OnItemCheckedListener, View.OnClickListener {

    private var subCategories: List<SWCategoriesResponse.Data.SubCategories>? = null
    private var subSelects = ArrayList<Int>()
    private var adapter: SWSubCategoryInFilterAdapter? = null
    private var binding: FragmentFilterCategoryBinding? = null

    private var filterModel: SWFilterBookRequest? = null
    private var idCategory: Int? = null
    private var titleName: String? = ""
    private var isCategory: Boolean? = null
    private var keyFilter: String? = null

    companion object {
        @JvmStatic
        fun newInstance(
            idCate: Int?,
            title: String?,
            categories: SWCategoriesResponse.Data?,
            filter: SWFilterBookRequest?,
            category: Boolean?,
            key: String?
        ) =
            SWFilterSubCategoryFragment().apply {
                arguments = Bundle().apply {
                    idCategory = idCate
                    titleName = title
                    subCategories = categories?.subCategories
                    filterModel = SWFilterBookRequest(
                        filter?.categoryIds,
                        filter?.bookType,
                        filter?.priceMin,
                        filter?.priceMax,
                        filter?.publishers
                    )
                    adapter = SWSubCategoryInFilterAdapter(subCategories, filter)
                    isCategory = category
                    keyFilter = key
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_filter_category,
            container,
            false
        )

        initUI()
        return binding?.root
    }

    private fun initUI() {
        (activity as SWMainActivity).setHeaderInChildrenScreen(titleName.toString(), isSearch = false, isFilter = false)

        getSubCategory()

        Log.d("TAG", "initUI: ${filterModel?.categoryIds}")
        binding?.btnApply?.setOnClickListener(this)
    }

    private fun getSubCategory() {
        binding?.rcSubCategory?.layoutManager = LinearLayoutManager(context)
        binding?.rcSubCategory?.itemAnimator = DefaultItemAnimator()
        binding?.rcSubCategory?.adapter = adapter

        adapter?.onCallBackCheckedItem(this)
    }

    override fun onBackPressed(): Boolean {
        val frm = SWFilterBookFragment.newInstance(isCategory!!, keyFilter, filterModel)
        val args = Bundle()
        frm.arguments = args

        replaceFragment(frm, R.id.container, true, null)

        return true
    }

    override fun onCheckedItemListener(id: Int?) {
        subSelects.add(id!!)
    }

    override fun onUnCheckedItemListener(id: Int?) {
        val ids = ArrayList<Int>()
        Log.d("TAG", "onUnCheckedItemListener: $id")
        filterModel?.categoryIds?.let { ids.addAll(it) }
        for (i in 0 until ids.size) {
            if (ids[i] == id) {
                ids.removeAt(i)
                break
            }
        }
        val idSub = ArrayList<Int?>()
        for (element in subCategories!!) {
            idSub.add(element.id)
        }
//        for (i in 0 until ids.size) {
//            if (!idSub.contains(ids[i])) {
//                ids.removeAt(i)
//                break
//            }
//        }
        subSelects = ids
        Log.d("TAG", "onUnCheckedItemListener: $ids   $subSelects")
        filterModel = SWFilterBookRequest(
            ids,
            filterModel?.bookType,
            filterModel?.priceMin,
            filterModel?.priceMax,
            filterModel?.publishers
        )
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btnApply -> {
                val ids = ArrayList<Int>()
                filterModel?.categoryIds?.let { ids.addAll(it) }
                ids.addAll(subSelects)

                Log.d("TAG", "onClick: $ids")

                //Loại bỏ các phần tử bị trùng trong ids
                val listWithoutDuplicates: List<Int> = ids.stream().distinct().collect(Collectors.toList())
                filterModel = SWFilterBookRequest(
                    listWithoutDuplicates,
                    filterModel?.bookType,
                    filterModel?.priceMin,
                    filterModel?.priceMax,
                    filterModel?.publishers
                )
                onBackPressed()
            }
        }
    }

}