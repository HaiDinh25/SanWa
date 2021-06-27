package com.sanwashoseki.bookskozuchi.business.more.adpaters

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.more.views.SWContactUsFragment
import com.sanwashoseki.bookskozuchi.business.more.views.SWFAQsFragment

class SWViewPageHelpAdapter(
    private var context: Context,
    fragment: FragmentManager,
    private var userName: String?,
) : FragmentPagerAdapter(fragment) {

    override fun getCount(): Int {
        return 2
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return super.isViewFromObject(view, `object`)
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> SWFAQsFragment.newInstance()
            else -> SWContactUsFragment.newInstance(userName)
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> context.getString(R.string.help_faqs)
            else -> context.getString(R.string.help_contact)
        }
    }
}