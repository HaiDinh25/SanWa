package com.sanwashoseki.bookskozuchi.business.more.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.databinding.FragmentColorThemBinding
import com.sanwashoseki.bookskozuchi.utilities.Sharepref

class SWColorThemFragment : SWBaseFragment() {

    private var binding: FragmentColorThemBinding? = null

    companion object {
        @JvmStatic
        fun newInstance() =
            SWColorThemFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_color_them, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        (activity as SWMainActivity).hideBottomNavigationMenu(true)
        (activity as SWMainActivity).setHeaderInChildrenScreen(getString(R.string.more_color_theme), isSearch = false, isFilter = false)

        binding?.let { it ->
            if (Sharepref.getNightMode(context)) {
                it.btnDark.isChecked = true
                it.btnLight.isChecked = false
            }

            it.btnLight.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    Sharepref.setNightMode(context, false)
                    applyThemes()
                    (activity as SWMainActivity).finish()
                    (activity as SWMainActivity).replaceActivity(SWMainActivity::class.java)
                }
            }
            it.btnDark.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    Sharepref.setNightMode(context, true)
                    applyThemes()
                    (activity as SWMainActivity).finish()
                    (activity as SWMainActivity).replaceActivity(SWMainActivity::class.java)
                }
            }
        }
    }

    private fun applyThemes() {
        if (Sharepref.getNightMode(activity)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }

}