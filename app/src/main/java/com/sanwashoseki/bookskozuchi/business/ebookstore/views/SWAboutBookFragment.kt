package com.sanwashoseki.bookskozuchi.business.ebookstore.views

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
import com.sanwashoseki.bookskozuchi.business.ebookstore.adapters.SWSeeAllDetailAdapter
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWBookDetailResponse
import com.sanwashoseki.bookskozuchi.databinding.FragmentAboutBookBinding

class SWAboutBookFragment : SWBaseFragment() {

    private var binding: FragmentAboutBookBinding? = null
    private var adapter: SWSeeAllDetailAdapter? = null

    companion object {
        @JvmStatic
        fun newInstance(model: List<SWBookDetailResponse.Data.ProductInfoBooks>?) =
            SWAboutBookFragment().apply {
                arguments = Bundle().apply {
                    adapter = SWSeeAllDetailAdapter(model)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_about_book, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        (activity as SWMainActivity?)?.hideBottomNavigationMenu(true)
        (activity as SWMainActivity).setHeaderInChildrenScreen(getString(R.string.about_detail_head), isSearch = false, isFilter = false)
        (activity as SWMainActivity).hideWishListFilter()

        val manager = LinearLayoutManager(context)
        binding?.let { it ->
            it.rcDetail.layoutManager = manager
            it.rcDetail.itemAnimator = DefaultItemAnimator()
            it.rcDetail.isNestedScrollingEnabled = false
            it.rcDetail.adapter = adapter
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}