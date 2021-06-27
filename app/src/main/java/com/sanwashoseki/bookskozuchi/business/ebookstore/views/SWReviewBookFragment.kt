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
import com.sanwashoseki.bookskozuchi.business.ebooklibrary.views.SWAddReviewBookFragment
import com.sanwashoseki.bookskozuchi.business.ebookstore.adapters.SWReviewBooksAdapter
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWBookDetailResponse
import com.sanwashoseki.bookskozuchi.databinding.FragmentBookDetailReviewBookBinding

class SWReviewBookFragment : SWBaseFragment(), View.OnClickListener {

    private var binding: FragmentBookDetailReviewBookBinding? = null
    private var adapter: SWReviewBooksAdapter? = null
    private var review: SWBookDetailResponse? = null

    companion object {
        @JvmStatic
        fun newInstance(mode: SWBookDetailResponse) = SWReviewBookFragment().apply {
                arguments = Bundle().apply {
                    review = mode
                    adapter = SWReviewBooksAdapter(mode.data!!.productReviewsModel)
                }
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_book_detail_review_book, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        getReview()
        binding?.let { it ->
            if (review?.data?.isPurchased == true) {
                it.btnReview.visibility = View.VISIBLE
            } else {
                it.btnReview.visibility = View.GONE
            }
            if (review?.data?.productReviewsModel?.size == 0) {
                it.tvReviewNull.visibility = View.VISIBLE
            }
            it.btnReview.setOnClickListener(this)
        }
    }

    private fun getReview() {
        binding?.let { it ->
            it.rcReview.layoutManager = LinearLayoutManager(context)
            it.rcReview.itemAnimator = DefaultItemAnimator()
            it.rcReview.adapter = adapter
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onResume() {
        super.onResume()
        getReview()
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btnReview -> {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.container, SWAddReviewBookFragment.newInstance(review, false))?.commit()
            }
        }
    }

}