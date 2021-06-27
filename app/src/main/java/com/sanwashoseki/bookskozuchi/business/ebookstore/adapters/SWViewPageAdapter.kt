package com.sanwashoseki.bookskozuchi.business.ebookstore.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWBookDetailResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.views.SWOverViewBookFragment
import com.sanwashoseki.bookskozuchi.business.ebookstore.views.SWReviewBookFragment

@Suppress("DEPRECATION")
class SWViewPageAdapter(
    private var context: Context,
    fragment: FragmentManager,
    private var bookDetail: SWBookDetailResponse,
) : FragmentStatePagerAdapter(fragment) {

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> SWOverViewBookFragment.newInstance(bookDetail)
            else -> SWReviewBookFragment.newInstance(bookDetail)
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> context.getString(R.string.book_detail_tab_view_over_view)
            else -> context.getString(R.string.book_detail_tab_review) + "(" + bookDetail.data?.productReviewsModel?.size + ")"
        }
    }

}