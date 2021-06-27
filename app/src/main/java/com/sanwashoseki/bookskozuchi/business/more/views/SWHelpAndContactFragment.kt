package com.sanwashoseki.bookskozuchi.business.more.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.business.more.adpaters.SWViewPageHelpAdapter
import com.sanwashoseki.bookskozuchi.databinding.FragmentHelpAndContactBinding

class SWHelpAndContactFragment : SWBaseFragment() {

    private var binding: FragmentHelpAndContactBinding? = null
    private var adapter: SWViewPageHelpAdapter? = null

    private var userName: String? = null

    companion object {
        @JvmStatic
        fun newInstance(name: String?) =
            SWHelpAndContactFragment().apply {
                arguments = Bundle().apply {
                    userName = name
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_help_and_contact, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        (activity as SWMainActivity).hideBottomNavigationMenu(true)
        (activity as SWMainActivity).setHeaderInChildrenScreen(getString(R.string.more_help), isSearch = false, isFilter = false)
        binding?.let { it ->
            adapter = SWViewPageHelpAdapter(requireContext(), childFragmentManager, userName)
            it.viewPage.adapter = adapter
            it.tabLayout.setupWithViewPager(it.viewPage)
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }

}