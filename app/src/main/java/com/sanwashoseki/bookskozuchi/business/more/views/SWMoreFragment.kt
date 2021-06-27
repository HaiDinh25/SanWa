package com.sanwashoseki.bookskozuchi.business.more.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWLoginActivity
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.business.authentication.logout.models.requests.SWLogoutRequest
import com.sanwashoseki.bookskozuchi.business.ebookrequest.views.SWBookRequestFragment
import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWProfileResponse
import com.sanwashoseki.bookskozuchi.business.more.services.SWLogoutInterface
import com.sanwashoseki.bookskozuchi.business.more.services.SWLogoutPresenter
import com.sanwashoseki.bookskozuchi.business.more.services.SWProfileInterface
import com.sanwashoseki.bookskozuchi.business.more.services.SWProfilePresenter
import com.sanwashoseki.bookskozuchi.databinding.FragmentMoreBinding
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen


class SWMoreFragment : SWBaseFragment(), View.OnClickListener, SWLogoutInterface,
    SWProfileInterface {

    private var binding: FragmentMoreBinding? = null
    private var presenterLogout: SWLogoutPresenter? = null
    private var presenterGetInFo: SWProfilePresenter? = null

    private var userInfo: SWProfileResponse? = null

    private var skeletonScreen: SkeletonScreen? = null

    companion object {
        @JvmStatic
        fun newInstance() = SWMoreFragment().apply {
            arguments = Bundle().apply {

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        (activity as SWMainActivity?)?.hideBottomNavigationMenu(false)
        (activity as SWMainActivity).setHeaderInMainMenu(getString(R.string.navigation_more), true)

        binding?.let { it ->
            if (Sharepref.getUserToken(context) != "") {
                it.layoutGeneral.visibility = View.VISIBLE
                it.layoutPushNitifications.visibility = View.VISIBLE
                it.layoutName.visibility = View.VISIBLE
                it.layoutSignIn.visibility = View.GONE
                it.imgNotSignIn.visibility = View.GONE
                presenterGetInFo?.getUserInfo(context)
            } else {
                it.layoutGeneral.visibility = View.GONE
                it.layoutPushNitifications.visibility = View.GONE
                it.layoutName.visibility = View.GONE
                it.btnSignOut.visibility = View.GONE
                it.view.visibility = View.GONE
                it.imgNotSignIn.visibility = View.VISIBLE
                it.layoutSignIn.visibility = View.VISIBLE
            }

            it.btnProfile.setOnClickListener(this)
            it.btnPurchase.setOnClickListener(this)
            it.btnMyReview.setOnClickListener(this)
            it.btnMyBookRequest.setOnClickListener(this)
            it.btnSettingAuth.setOnClickListener(this)
            it.btnPushNotification.setOnClickListener(this)
            it.btnColorTheme.setOnClickListener(this)
            it.btnAbout.setOnClickListener(this)
            it.btnHelp.setOnClickListener(this)
            it.btnSignOut.setOnClickListener(this)
            it.layoutSignIn.setOnClickListener(this)
        }
    }

    override fun onClick(v: View) {
        if (NetworkUtil.isNetworkConnected(context)) {
            when (v.id) {
                R.id.layoutSignIn -> {
                    activity?.finish()
                    (activity as SWMainActivity).replaceActivity(SWLoginActivity::class.java)
                }
                R.id.btnProfile -> selectFragment(SWEditProfileFragment.newInstance(userInfo))
                R.id.btnPurchase -> selectFragment(SWPurchaseHistoryFragment.newInstance())
                R.id.btnMyReview -> selectFragment(SWMyReviewFragment.newInstance())
                R.id.btnMyBookRequest -> selectFragment(SWBookRequestFragment.newInstance(true))
                R.id.btnSettingAuth -> selectFragment(SWSettingAuthenticateFragment.newInstance())
                R.id.btnPushNotification -> selectFragment(SWPushNotificationFragment.newInstance())
                R.id.btnColorTheme -> selectFragment(SWColorThemFragment.newInstance())
                R.id.btnAbout -> selectFragment(SWAboutUsFragment.newInstance())
                R.id.btnHelp -> selectFragment(SWHelpAndContactFragment.newInstance(binding?.tvName?.text.toString()))
                R.id.btnSignOut -> {
                    val request = SWLogoutRequest(getDeviceID(),
                        Const.CLIENT_ID,
                        Const.CLIENT_SECRET)
                    presenterLogout?.logout(context, request)
                }
            }
        } else {
            showLoading(
                false,
                getString(R.string.dialog_error_network_title),
                getString(R.string.dialog_error_network_content)
            )
        }
    }

    private fun selectFragment(fragment: Fragment?) {
        replaceFragment(fragment, R.id.container, false, null)
    }

    override fun onBackPressed(): Boolean {
        requireActivity().finish()
        return false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenterLogout = SWLogoutPresenter()
        presenterGetInFo = SWProfilePresenter()
        presenterLogout?.attachView(this)
        presenterGetInFo?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenterLogout?.detachView()
        presenterGetInFo?.detachView()
    }

    override fun logoutSuccess() {
        Sharepref.removeUserInfo(context)
        Sharepref.setPassCodeAuth(context, false)
        activity?.finish()
        (activity as SWMainActivity).replaceActivity(SWLoginActivity::class.java)
    }

    override fun showMessageError(msg: String) {
//        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun getInfoSuccess(result: SWProfileResponse) {
        userInfo = result
        binding?.let { it ->
            skeletonScreen?.hide()
            it.tvName.text = result.data?.nameFormated
            it.tvEmail.text = result.data?.email
            Log.d("TAG", "getInfoSuccess: " + result.data?.avatarUrl)
            Glide.get(requireContext()).clearMemory()
            Glide.with(this)
                .load(result.data?.avatarUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(it.imgAvatar)
        }
    }

    override fun showNetworkError() {
        showLoading(
            false,
            getString(R.string.dialog_error_network_title),
            getString(R.string.dialog_error_network_content)
        )
    }

    override fun showMessageInfoError(msg: String) {
//        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun showIndicator() {
        showLoading(true,
            getString(R.string.dialog_title_sign_out),
            getString(R.string.dialog_loading_content))
    }

    override fun showIndicateGetInfo() {
        skeletonScreen = Skeleton.bind(binding?.rootView)
            .load(R.layout.skeleton_fragment_more)
            .duration(1200)
            .color(R.color.shimmer_color)
            .angle(0)
            .show()

//        showLoading(true, "Get user info", getString(R.string.dialog_loading_content))
    }

    override fun hideIndicator() {
        hideLoading()
    }

}