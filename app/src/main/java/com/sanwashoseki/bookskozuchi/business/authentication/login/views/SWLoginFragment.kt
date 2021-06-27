package com.sanwashoseki.bookskozuchi.business.authentication.login.views

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Bundle
import android.os.CancellationSignal
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWLoginActivity
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.business.authentication.forgotpassword.views.SWConfirmEmailFragment
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.entitys.SWLoginOutput
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.requests.SWLoginFacebookRequest
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.requests.SWLoginLineRequest
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.requests.SWLoginRequest
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.responses.SWLoginResponse
import com.sanwashoseki.bookskozuchi.business.authentication.login.services.SWLoginInterface
import com.sanwashoseki.bookskozuchi.business.authentication.login.services.SWLoginPresenter
import com.sanwashoseki.bookskozuchi.business.authentication.logout.views.SWSecondFragment
import com.sanwashoseki.bookskozuchi.business.authentication.register.views.SWRegisterFragment
import com.sanwashoseki.bookskozuchi.databinding.FragmentLoginBinding
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.linecorp.linesdk.LineApiResponseCode
import com.linecorp.linesdk.Scope
import com.linecorp.linesdk.auth.LineAuthenticationParams
import com.linecorp.linesdk.auth.LineLoginApi
import com.linecorp.linesdk.auth.LineLoginResult
import com.sanwashoseki.bookskozuchi.BuildConfig
import com.sanwashoseki.bookskozuchi.books.dialogs.SWDialogConfirmPurchase
import com.sanwashoseki.bookskozuchi.business.authentication.dialogs.SWDialogFingerAuthentication
import com.sanwashoseki.bookskozuchi.utilities.toEditable

class SWLoginFragment : SWBaseFragment(), View.OnClickListener,
    GoogleApiClient.OnConnectionFailedListener, SWLoginInterface {

    private var binding: FragmentLoginBinding? = null
    private var presenter: SWLoginPresenter? = null

    private var callbackManager: CallbackManager? = null
    private var googleApiClient: GoogleApiClient? = null
    private val REQUEST_CODE_SIGN_IN = 1
    private val REQUEST_CODE_SIGN_IN_LINE = 2

    private var email = ""
    private var password = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        FacebookSdk.sdkInitialize(requireActivity().applicationContext)

        initUI()
        return binding?.root
    }

    @SuppressLint("SetTextI18n")
    private fun initUI() {
        printKeyHash()
        validateButton()
        logoutFacebook()

        //Chặn dấu cách
        binding?.edtEmail?.filters = arrayOf(filter)
//        binding?.edtPassword?.filters = arrayOf(filter)
        
        binding?.edtPassword?.setOnKeyListener { _, code, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                if (code == KeyEvent.KEYCODE_ENTER) {
                    if (binding?.btnSignIn?.isEnabled!!) {
                        signInEmail()
                    }
                }
            }
            false
        }

        binding?.let { it ->
            it.container.setOnClickListener(this)
            it.btnSignIn.setOnClickListener(this)
            it.btnSignInFacebook.setOnClickListener(this)
            it.btnSignInGoogle.setOnClickListener(this)
            it.btnSignInLine.setOnClickListener(this)
            it.btnSignUp.setOnClickListener(this)
            it.btnForgotPassword.setOnClickListener(this)
            it.btnSkip.setOnClickListener(this)
        }

        binding?.edtEmail?.text = BuildConfig.FIELD_USERNAME.toEditable()
        binding?.edtPassword?.text = BuildConfig.FIELD_PASSWORD.toEditable()
    }

    private fun validateButton() {
        binding?.edtEmail?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding?.edtEmail?.filters = arrayOf(filter)
            }

            override fun afterTextChanged(s: Editable?) {
                binding?.let { it ->
                    email = it.edtEmail.text.toString()
                    if (email.isEmpty()) {
                        it.tvErrorEmail.text = getString(R.string.text_required)
                    } else {
                        if (!validEmail(email)) {
                            it.tvErrorEmail.text = getString(R.string.login_email_error)
                            it.tvErrorEmail.visibility = View.VISIBLE
                        } else {
                            it.tvErrorEmail.visibility = View.GONE
                        }
                    }
                }
                presenter?.validation(email, password, validEmail(email))
            }
        })

        binding?.edtPassword?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                binding?.edtPassword?.filters = arrayOf(filter)
            }

            override fun afterTextChanged(s: Editable?) {
                binding?.let { it ->
                    password = it.edtPassword.text.toString()
                    if (password.isEmpty()) {
                        it.tvErrorPass.text = getString(R.string.text_required)
                    } else {
                        if (password.length < 8) {
                            it.tvErrorPass.text = getString(R.string.login_pass_error)
                            it.tvErrorPass.visibility = View.VISIBLE
                        } else {
                            it.tvErrorPass.visibility = View.GONE
                        }
                    }
                }
                presenter?.validation(email, password, validEmail(email))
            }
        })
    }

    private fun signInFacebook() {
//        showLoading(true, "Sign In", "Loading...")
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile", "email"))
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                @SuppressLint("SetTextI18n")
                override fun onSuccess(loginResult: LoginResult) {
                    var emailFB: String?
                    var nameFB: String?
                    Log.d("MainActivity", "Facebook token: " + loginResult.accessToken.token)
                    //Use GraphApi to get the information into the app.
                    val request =
                        GraphRequest.newMeRequest(loginResult.accessToken) { userData, _ ->

                            val requestLogin = SWLoginFacebookRequest(
                                loginResult.accessToken.token.toString(),
                                getDeviceID(),
                                Sharepref.getFirebaseToken(context),
                                Const.CLIENT_ID,
                                Const.CLIENT_SECRET
                            )
                            presenter?.loginFacebook(context, requestLogin)
                        }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email")
                    request.parameters = parameters
                    request.executeAndWait()
                }

                override fun onCancel() {
                    Log.d("MainActivity", "Facebook onCancel.")
                }

                override fun onError(error: FacebookException) {
                    Log.d("MainActivity", "Facebook onError.")
                }
            })
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.e("TAG", "onConnectionFailed():$connectionResult")
        Toast.makeText(context, "Google Play Services error.", Toast.LENGTH_SHORT).show()
    }

    private fun signInLine() {
//        showLoading(true, "Sign In", "Loading...")
        val loginIntent: Intent = LineLoginApi.getLoginIntent(
            requireActivity(), Const.LINE_CHANGE_ID,
            LineAuthenticationParams.Builder()
                .scopes(listOf(Scope.OPENID_CONNECT, Scope.OC_EMAIL, Scope.PROFILE))
                .build()
        )
        startActivityForResult(loginIntent, REQUEST_CODE_SIGN_IN_LINE)
    }

    private fun signInGoogle() {
//        showLoading(true, "Sign In", "Loading...")
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()

        googleApiClient = GoogleApiClient.Builder(requireActivity())
            .enableAutoManage(requireActivity(), this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
            .build()

        val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(intent, REQUEST_CODE_SIGN_IN)
    }

    private fun handleResultGoogle(result: GoogleSignInResult) {
        if (result.isSuccess) {
            val account: GoogleSignInAccount = result.signInAccount!!
            val name: String = account.displayName!!
            val email: String = account.email!!
            val imgUrl: String = account.photoUrl.toString()
            val output = SWLoginOutput(name, email)
            replaceFragment(
                SWSecondFragment.newInstance(
                    output,
                    Const.LOGIN_GOOGLE,
                    googleApiClient
                ), R.id.container, true, null
            )
        }
    }

    private fun handleResultLine(result: LineLoginResult) {
        when (result.responseCode) {
            LineApiResponseCode.SUCCESS -> {
                val token = result.lineCredential!!.accessToken.tokenString
                val idToken = result.lineIdToken?.rawString

                val requestLogin = SWLoginLineRequest(
                    token, idToken.toString(),
                    getDeviceID(),
                    Sharepref.getFirebaseToken(context),
                    Const.CLIENT_ID,
                    Const.CLIENT_SECRET
                )
                presenter?.loginLine(context, requestLogin)
            }
            LineApiResponseCode.CANCEL ->
                // Login canceled by user
                Log.e("TAG", "onActivityResult: LINE Login Canceled by user.")
            else -> {
                // Login canceled due to other error
                Log.e("TAG", "onActivityResult: Login FAILED!")
                Log.e("TAG", result.errorData.toString())
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        hideLoading()
        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> {
                val result: GoogleSignInResult =
                    Auth.GoogleSignInApi.getSignInResultFromIntent(data)!!
                handleResultGoogle(result)
            }
            REQUEST_CODE_SIGN_IN_LINE -> {
                val result: LineLoginResult = LineLoginApi.getLoginResultFromIntent(data)
                handleResultLine(result)
            }
            else -> {
                callbackManager!!.onActivityResult(requestCode, resultCode, data)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun signInEmail() {
        val email = binding?.edtEmail?.text.toString()
        val password = binding?.edtPassword?.text.toString()
        val deviceID = getDeviceID()
//        val request = SWLoginRequest(email, password, deviceID)
        val request = SWLoginRequest(
            email,
            password,
            deviceID,
            Sharepref.getFirebaseToken(context),
            Const.CLIENT_ID,
            Const.CLIENT_SECRET
        )
        presenter?.loginEmail(context, request)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWLoginPresenter()
        presenter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView();
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnSignIn -> signInEmail()
            R.id.btnSignInFacebook -> signInFacebook()
            R.id.btnSignInGoogle -> signInGoogle()
            R.id.btnSignInLine -> signInLine()
            R.id.container -> hideSoftKeyBoard(context, binding?.container!!)
            R.id.btnSignUp -> replaceFragment(
                SWRegisterFragment.newInstance(),
                R.id.container,
                true,
                null
            )
            R.id.btnForgotPassword -> replaceFragment(SWConfirmEmailFragment.newInstance(),
                R.id.container,
                true,
                null)
            R.id.btnSkip -> {
                (activity as SWLoginActivity).replaceActivity(SWMainActivity::class.java)
                (activity as SWLoginActivity).finish()
            }
        }
    }

    override fun onBackPressed(): Boolean {
        requireActivity().finish()
        return false
    }

    override fun loginSuccess(result: SWLoginResponse?) {
        Sharepref.removeUserInfo(context)
        val email = result?.data?.customer?.email.toString()
        val password = binding?.edtPassword?.text.toString()
        val token = result?.data?.accessToken?.tokenString.toString()
        val refreshToken = result?.data?.refreshToken?.tokenString.toString()
        Sharepref.saveUserInfo(context, email, password, token, refreshToken)

        activity?.finish()
        //(activity as SWLoginActivity).replaceActivity(SWMainActivity::class.java)
    }

    private fun logoutFacebook() {
        if (AccessToken.getCurrentAccessToken() != null) {
            GraphRequest(
                AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE,
                GraphRequest.Callback {
                    AccessToken.setCurrentAccessToken(null)
                    LoginManager.getInstance().logOut()

//                    replaceFragment(SWLoginFragment(), R.id.container, true, null)
                }).executeAsync()
        }
    }

    override fun showNetworkError() {
        showLoading(
            false,
            getString(R.string.dialog_error_network_title),
            getString(R.string.dialog_error_network_content)
        )
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun updateButton(valid: Boolean) {
        if (valid) {
            binding?.btnSignIn?.isEnabled = true
            binding?.btnSignIn?.setBackgroundResource(R.drawable.bg_button_authentication)
        } else {
            binding?.btnSignIn?.isEnabled = false
            binding?.btnSignIn?.setBackgroundResource(R.drawable.bg_button_inactive)
        }
    }

    override fun showIndicator() {
        showLoading()
//        showLoading(
//            true,
//            getString(R.string.dialog_title_sign_in),
//            getString(R.string.dialog_loading_content)
//        )
    }

    override fun hideIndicator() {
        hideLoading()
    }

}
