package com.sanwashoseki.bookskozuchi.business.authentication.logout.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.databinding.FragmentSecondBinding
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.entitys.SWLoginOutput
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.responses.SWLoginResponse
import com.sanwashoseki.bookskozuchi.business.authentication.login.views.SWLoginFragment
import com.sanwashoseki.bookskozuchi.business.authentication.logout.services.SWLogoutInterface
import com.sanwashoseki.bookskozuchi.business.authentication.logout.services.SWLogoutPresenter
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.business.authentication.logout.models.requests.SWLogoutRequest

class SWSecondFragment : SWBaseFragment(), View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, SWLogoutInterface {

    private var binding: FragmentSecondBinding? = null

    private var mName: String? = null
    private var mEmail: String? = null
    private var mTypeLogin: String? = null

    private var ggApiClient: GoogleApiClient? = null

    private var presenter: SWLogoutPresenter? = null

    companion object {
        @SuppressLint("SetTextI18n")
        @JvmStatic
        fun newInstance(input: SWLoginResponse?, typeLogin: String) = SWSecondFragment()
            .apply {
                arguments = Bundle().apply {
                    mName = input?.data?.customer?.fullName
                    mEmail = input?.data?.customer?.email
                    mTypeLogin = typeLogin
                }
            }

        fun newInstance(input: SWLoginOutput, typeLogin: String, googleApiClient: GoogleApiClient?) = SWSecondFragment()
            .apply {
            arguments = Bundle().apply {
                mName = input.name
                mEmail = input.email
                mTypeLogin = typeLogin
                ggApiClient = googleApiClient
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_second, container, false)

        initUI()
        return binding?.root
    }

    @SuppressLint("SetTextI18n")
    private fun initUI() {
        binding?.btnLogout?.setOnClickListener(this)

        Log.d("TAG", "initUI: " + Sharepref.getUserInfo(context))

        binding?.tvContent?.text = mName + "\n" + mEmail
//        Glide.get(requireContext()).clearMemory()
//        Glide.with(requireActivity())
//            .load(profileURL)
//            .into(mBinding.imgAvatar)
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btnLogout -> {
                when(mTypeLogin) {
                    Const.LOGIN_FACEBOOK -> logoutFacebook()
                    Const.LOGIN_GOOGLE -> logoutGoogle()
                    Const.LOGIN_EMAIL -> logoutEmail()
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWLogoutPresenter()
        presenter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView();
    }

    private fun logoutEmail() {
        Log.d("TAG", "logoutEmail: " + Sharepref.getUserToken(context))
        val request = SWLogoutRequest(getDeviceID(), Const.CLIENT_ID, Const.CLIENT_SECRET)
        presenter?.logout(context, request)
    }

    private fun logoutFacebook() {
        if (AccessToken.getCurrentAccessToken() != null) {
            GraphRequest(
                AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE,
                GraphRequest.Callback {
                    AccessToken.setCurrentAccessToken(null)
                    LoginManager.getInstance().logOut()

                    replaceFragment(SWLoginFragment(), R.id.container, true, null)
                }).executeAsync()
        }
    }

    private fun logoutGoogle() {
        Auth.GoogleSignInApi.signOut(ggApiClient).setResultCallback{
            ggApiClient?.stopAutoManage(requireActivity())
            ggApiClient?.disconnect()
            replaceFragment(SWLoginFragment(), R.id.container, true, null)
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.e("TAG", "onConnectionFailed():$connectionResult");
        Toast.makeText(context, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    override fun logoutSuccess() {
        replaceFragment(SWLoginFragment(), R.id.container, true, null)
    }

    override fun showNetworkError() {
        Toast.makeText(context, "Network not connect", Toast.LENGTH_LONG).show()
    }

    override fun showMessageError(e: String) {
        Toast.makeText(context, e, Toast.LENGTH_LONG).show()
    }

    override fun showIndicator() {
        binding?.progressBar?.visibility = View.VISIBLE
    }

    override fun hideIndicator() {
        binding?.progressBar?.visibility = View.GONE
    }
}