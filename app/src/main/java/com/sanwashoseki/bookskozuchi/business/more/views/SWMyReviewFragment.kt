package com.sanwashoseki.bookskozuchi.business.more.views

import android.annotation.SuppressLint
import android.content.Context
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
import com.sanwashoseki.bookskozuchi.business.authentication.register.models.responses.SWRegisterResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.views.SWBookDetailFragment
import com.sanwashoseki.bookskozuchi.business.more.adpaters.SWMyReviewAdapter
import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWMyReviewResponse
import com.sanwashoseki.bookskozuchi.business.more.services.SWMyReviewInterface
import com.sanwashoseki.bookskozuchi.business.more.services.SWMyReviewPresenter
import com.sanwashoseki.bookskozuchi.databinding.FragmentMyReviewBinding
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen


class SWMyReviewFragment : SWBaseFragment(), SWMyReviewAdapter.OnItemClickListener,
    SWMyReviewInterface, View.OnClickListener, SWBaseFragment.OnConfirmListener {

    private var binding: FragmentMyReviewBinding? = null
    private var adapter: SWMyReviewAdapter? = null
    private var presenter: SWMyReviewPresenter? = null

    private var skeletonScreen: SkeletonScreen? = null
    private var isSwipe = false
    private var idReview: Int? = null

    companion object {
        @JvmStatic
        fun newInstance() = SWMyReviewFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_review, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        (activity as SWMainActivity).hideBottomNavigationMenu(true)
        (activity as SWMainActivity).setHeaderInChildrenScreen(getString(R.string.more_my_review),
            isSearch = false,
            isFilter = false)

        presenter?.getMyReview(context)

        binding?.let { it ->
            it.swipeRefresh.setOnRefreshListener {
                isSwipe = true
                presenter?.getMyReview(context)
            }
            it.btnFindNow.setOnClickListener(this)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWMyReviewPresenter()
        presenter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    private fun getReview() {
        binding?.rcMyReview?.layoutManager = LinearLayoutManager(context)
        binding?.rcMyReview?.itemAnimator = DefaultItemAnimator()
        binding?.rcMyReview?.adapter = adapter

        adapter?.onCallBackClicked(this)
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    @SuppressLint("SetTextI18n")
    override fun getMyReviewSuccess(result: SWMyReviewResponse?) {
        binding?.let { it ->
            if (isSwipe) {
                it.swipeRefresh.isRefreshing = false
            }
            if (result?.data?.size == 0) {
                it.layoutEmpty.visibility = View.VISIBLE
                it.layoutData.visibility = View.GONE
            } else {
                it.layoutEmpty.visibility = View.GONE
                it.layoutData.visibility = View.VISIBLE
                it.tvNumReview.text = getString(R.string.my_review_total) + " " + result?.data?.size + " " + getString(
                    R.string.my_review)
                adapter = SWMyReviewAdapter(result?.data)
                getReview()
            }
        }
    }

    override fun deleteReviewSuccess(result: SWRegisterResponse?) {
        presenter?.getMyReview(context)
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun showIndicator() {
        binding?.let { it ->
            if (!isSwipe) {
                it.rcMyReview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                skeletonScreen = Skeleton.bind(it.rcMyReview)
                    .adapter(adapter)
                    .shimmer(true)
                    .angle(20)
                    .frozen(false)
                    .duration(1200)
                    .count(10)
                    .load(R.layout.skeleton_recycle_item_my_review)
                    .show()
            }
        }

//        showLoading(true, getString(R.string.more_my_review), getString(R.string.dialog_loading_content))
    }

    override fun hideIndicator() {
        hideLoading()
    }

    override fun showNetworkError() {
        showLoading(
            false,
            getString(R.string.dialog_error_network_title),
            getString(R.string.dialog_error_network_content)
        )
    }

    override fun onDeleteReviewListener(id: Int?) {
        idReview = id
        showDialogConfirm(getString(R.string.dialog_remove_review), false)
        onCallBackConfirm(this)
    }

    override fun onDetailListener(id: Int?) {
        replaceFragment(SWBookDetailFragment.newInstance(id, false), R.id.container, false, null)
    }

    override fun onClick(p0: View?) {
        (activity as SWMainActivity).replaceActivity(SWMainActivity::class.java)
        (activity as SWMainActivity).finish()
    }

    override fun onConfirmListener() {
        presenter?.deleteReview(context, idReview)
    }

}