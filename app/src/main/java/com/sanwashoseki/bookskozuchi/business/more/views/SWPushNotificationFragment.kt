package com.sanwashoseki.bookskozuchi.business.more.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWAddShoppingCartWishListResponse
import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWPushNotificationResponse
import com.sanwashoseki.bookskozuchi.business.more.services.SWPushNotificationInterface
import com.sanwashoseki.bookskozuchi.business.more.services.SWPushNotificationPresenter
import com.sanwashoseki.bookskozuchi.databinding.FragmentPushNotificationBinding
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen

class SWPushNotificationFragment : SWBaseFragment(), SWPushNotificationInterface {

    private var binding: FragmentPushNotificationBinding? = null
    private var presenter: SWPushNotificationPresenter? = null

    private var skeletonScreen: SkeletonScreen? = null

    companion object {
        @JvmStatic
        fun newInstance() =
            SWPushNotificationFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_push_notification,
            container,
            false)

        initUI()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter?.getStatusNotification(context)
    }

    private fun initUI() {
        (activity as SWMainActivity).hideBottomNavigationMenu(true)
        (activity as SWMainActivity).setHeaderInChildrenScreen(getString(R.string.more_push_notification),
            isSearch = false,
            isFilter = false)

        binding?.let { it ->
            it.switchAccount.setOnCheckedChangeListener { _, isChecked ->
                if (!isChecked) {
                    it.switchAccount.isChecked = false
                    presenter?.pushStatusNotification(context, false)
                } else {
                    it.switchAccount.isChecked = true
                    presenter?.pushStatusNotification(context, true)
                }
            }
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWPushNotificationPresenter()
        presenter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    override fun getStatusNotificationSuccess(result: SWPushNotificationResponse) {
        binding?.let { it ->
            skeletonScreen?.hide()
            it.switchAccount.isChecked = result.data?.isMobileAppNotificationEnable == true
        }
    }

    override fun pushStatusNotificationSuccess(result: SWAddShoppingCartWishListResponse) {

    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun showIndicator() {
        skeletonScreen = Skeleton.bind(binding?.rootView)
            .load(R.layout.skeleton_fragment_push_notification)
            .duration(1200)
            .color(R.color.colorSkeleton)
            .angle(0)
            .show()

//        showLoading(true, getString(R.string.more_push_notification), getString(R.string.dialog_loading_content))
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

}