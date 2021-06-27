package com.sanwashoseki.bookskozuchi.business.filter.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookInfoResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.views.SWBookDetailFragment
import com.sanwashoseki.bookskozuchi.business.filter.models.responses.SWFilterBookResponse
import com.sanwashoseki.bookskozuchi.business.search.adapters.SWSearchBookAdapter
import com.sanwashoseki.bookskozuchi.databinding.FragmentFilterResponseBinding

class SWFilterResponseFragment : SWBaseFragment(), SWSearchBookAdapter.OnItemClickedListener {

    private var binding: FragmentFilterResponseBinding? = null
    private var adapter: SWSearchBookAdapter? = null

    companion object {
        @JvmStatic
        fun newInstance(result: SWFilterBookResponse) =
            SWFilterResponseFragment().apply {
                arguments = Bundle().apply {
                    adapter = SWSearchBookAdapter(result.data)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter_response, container, false)

        initUI()
        return binding?.rcBook
    }

    private fun initUI() {
        (activity as SWMainActivity?)?.hideBottomNavigationMenu(true)
        (activity as SWMainActivity).setHeaderInChildrenScreen(getString(R.string.filter_title), isSearch = false, isFilter = false)

        getListBooks()
    }

    private fun getListBooks() {
        binding?.let { it ->
            it.rcBook.layoutManager = GridLayoutManager(context, 2)
            it.rcBook.itemAnimator = DefaultItemAnimator()
            it.rcBook.adapter = adapter

            adapter?.onCallBackListener(this)
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onItemClickedListener(result: SWBookInfoResponse?) {
        replaceFragment(SWBookDetailFragment.newInstance(result?.id, false), R.id.container, false, null)
    }
}