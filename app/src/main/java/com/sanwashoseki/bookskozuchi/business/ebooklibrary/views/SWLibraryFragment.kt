package com.sanwashoseki.bookskozuchi.business.ebooklibrary.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.databinding.FragmentLibraryBinding
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.business.ebooklibrary.adapters.SWViewPageAdapter

class SWLibraryFragment : SWBaseFragment() {

    private var binding: FragmentLibraryBinding? = null

    companion object {
        fun newInstance(): SWLibraryFragment {
            val fragment = SWLibraryFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_library, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        (activity as SWMainActivity?)?.hideBottomNavigationMenu(false)
        (activity as SWMainActivity).setHeaderInMainMenu(getString(R.string.navigation_library), hideButton = true)

        binding?.let { it ->
            val adapter = SWViewPageAdapter(requireContext(), childFragmentManager)
            it.viewPage.adapter = adapter
            it.tabLayout.setupWithViewPager(it.viewPage)
        }
    }

    override fun onBackPressed(): Boolean {
        requireActivity().finish()
        return false
    }

}