package com.sanwashoseki.bookskozuchi.business.ebooklibrary.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.ebooklibrary.views.SWMyBookFragment
import com.sanwashoseki.bookskozuchi.business.ebooklibrary.views.SWWishListFragment

class SWViewPageAdapter(
    private var context: Context,
    fragment: FragmentManager
) : FragmentPagerAdapter(fragment) {

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> SWMyBookFragment()
            else -> SWWishListFragment.newInstance()
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> context.getString(R.string.library_tab_my_book)
            else -> context.getString(R.string.library_tab_wish_list)
        }
    }
}