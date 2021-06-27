package com.sanwashoseki.bookskozuchi.business.notifications.views

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
import com.sanwashoseki.bookskozuchi.business.notifications.adapters.SWNotificationAdapter
import com.sanwashoseki.bookskozuchi.business.notifications.models.responses.SWNotificationResponse
import com.sanwashoseki.bookskozuchi.business.notifications.services.SWNotificationsInterface
import com.sanwashoseki.bookskozuchi.business.notifications.services.SWNotificationsPresenter
import com.sanwashoseki.bookskozuchi.databinding.FragmentNotificationBinding
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants
import com.sanwashoseki.bookskozuchi.business.ebookrequest.views.SWRequestBookContentFragment
import com.sanwashoseki.bookskozuchi.business.ebookstore.views.SWBookDetailFragment
import com.sanwashoseki.bookskozuchi.business.more.views.SWMyReviewFragment
import com.sanwashoseki.bookskozuchi.business.more.views.SWPurchaseHistoryFragment
import com.sanwashoseki.bookskozuchi.business.notifications.models.responses.SWSetStatusNotification
import com.sanwashoseki.bookskozuchi.business.notifications.models.responses.SWUnreadNotificationResponse

class SWNotificationFragment : SWBaseFragment(), SWNotificationsInterface,
    SWNotificationAdapter.OnItemClickedListener {

    private var binding: FragmentNotificationBinding? = null
    private var presenter: SWNotificationsPresenter? = null
    private var adapter: SWNotificationAdapter? = null

    private var skeletonScreen: SkeletonScreen? = null
    private var isSwipe = false

    companion object {
        fun newInstance(): SWNotificationFragment {
            val fragment = SWNotificationFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        (activity as SWMainActivity).hideBottomNavigationMenu(true)
        (activity as SWMainActivity).setHeaderInChildrenScreen(getString(R.string.notifications), isSearch = false, isFilter = false)

        presenter?.getNotifications(context, true)

        binding?.let { it ->
            it.swipeRefresh.setOnRefreshListener {
                isSwipe = true
                presenter?.getNotifications(context, true)
            }
        }
    }

    private fun getNotification() {
        binding?.let { it ->
            it.rcNotification.layoutManager = LinearLayoutManager(context)
            it.rcNotification.itemAnimator = DefaultItemAnimator()
            it.rcNotification.adapter = adapter
            adapter?.onCallbackListener(this)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWNotificationsPresenter()
        presenter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun getUnreadNotificationsSuccess(result: SWUnreadNotificationResponse?) {}

    override fun getNotificationsSuccess(result: SWNotificationResponse?) {
        if (isSwipe) {
            binding?.swipeRefresh?.isRefreshing = false
        }
        hideIndicator()
        adapter = SWNotificationAdapter(result?.data)
        getNotification()
    }

    override fun setStatusSuccess(result: SWSetStatusNotification?) {
        when(result?.data?.entityName) {
            SWConstants.NAME_ORDER -> replaceFragment(SWPurchaseHistoryFragment.newInstance(), R.id.container, false, null)
            SWConstants.NAME_PRODUCT -> replaceFragment(SWBookDetailFragment.newInstance(result.data?.entityId, false), R.id.container, false, null)
            SWConstants.NAME_PRODUCT_REVIEW -> replaceFragment(SWMyReviewFragment.newInstance(), R.id.container, false, null)
            SWConstants.NAME_REQUEST_TOPPIC -> replaceFragment(SWRequestBookContentFragment.newInstance(result.data?.entityId, false), R.id.container, false, null)
        }
    }

    override fun showMessageError(msg: String) {}

    override fun expiredToken() {}

    override fun showIndicator() {
        binding?.let { it ->
            if (!isSwipe) {
                it.rcNotification.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                skeletonScreen = Skeleton.bind(it.rcNotification)
                    .adapter(adapter)
                    .shimmer(true)
                    .angle(20)
                    .frozen(false)
                    .duration(1200)
                    .count(10)
                    .load(R.layout.skeleton_recycle_item_notification)
                    .show()
            }
        }

//        showLoading(true, getString(R.string.notifications), getString(R.string.dialog_loading_content))
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

    override fun onClickedListener(detail: SWNotificationResponse.Data?) {
        if (detail?.isRead == false) {
            detail.id?.let { presenter?.setStatusNotifications(it) }
        } else {
            when(detail?.entityName) {
                SWConstants.NAME_ORDER -> replaceFragment(SWPurchaseHistoryFragment.newInstance(), R.id.container, false, null)
                SWConstants.NAME_PRODUCT -> replaceFragment(SWBookDetailFragment.newInstance(detail.entityId, false), R.id.container, false, null)
                SWConstants.NAME_PRODUCT_REVIEW -> replaceFragment(SWMyReviewFragment.newInstance(), R.id.container, false, null)
                SWConstants.NAME_REQUEST_TOPPIC -> replaceFragment(SWRequestBookContentFragment.newInstance(detail.entityId, false), R.id.container, false, null)
            }
        }
    }

}