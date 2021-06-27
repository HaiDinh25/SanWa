package com.sanwashoseki.bookskozuchi.business.authentication.register.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWLoginActivity
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.requests.SWLoginFacebookRequest
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.requests.SWLoginLineRequest
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.responses.SWLoginResponse
import com.sanwashoseki.bookskozuchi.business.authentication.login.views.SWLoginFragment
import com.sanwashoseki.bookskozuchi.business.authentication.register.models.requests.SWRegisterRequest
import com.sanwashoseki.bookskozuchi.business.authentication.register.models.responses.SWRegisterResponse
import com.sanwashoseki.bookskozuchi.business.authentication.register.services.SWRegisterInterface
import com.sanwashoseki.bookskozuchi.business.authentication.register.services.SWRegisterPresenter
import com.sanwashoseki.bookskozuchi.databinding.FragmentRegisterBinding
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.linecorp.linesdk.LineApiResponseCode
import com.linecorp.linesdk.Scope
import com.linecorp.linesdk.auth.LineAuthenticationParams
import com.linecorp.linesdk.auth.LineLoginApi
import com.linecorp.linesdk.auth.LineLoginResult

class SWRegisterFragment : SWBaseFragment(), View.OnClickListener, SWRegisterInterface {

    private var binding: FragmentRegisterBinding? = null
    private var presenter: SWRegisterPresenter? = null

    private var callbackManager: CallbackManager? = null
    private val REQUEST_CODE_SIGN_IN_LINE = 2

    private var firstName = ""
    private var lastName = ""
    private var email = ""
    private var pass = ""
    private var confirmPass = ""

    companion object {
        fun newInstance(): SWRegisterFragment {
            val fragment = SWRegisterFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        validateButton()
        binding?.edtEmail?.filters = arrayOf(filter)
//        binding?.edtPassWord?.filters = arrayOf(filter)
//        binding?.edtConfirmPassWord?.filters = arrayOf(filter)
        binding?.edtPassWord?.imeOptions = EditorInfo.IME_FLAG_FORCE_ASCII
        binding?.let { it ->
            it.btnSignIn.setOnClickListener(this)
            it.btnSignUp.setOnClickListener(this)
            it.container.setOnClickListener(this)
            it.btnSignInFacebook.setOnClickListener(this)
            it.btnSignInLine.setOnClickListener(this)
            it.btnPrivacy.setOnClickListener(this)
        }
    }

    private fun validateButton() {
        binding?.let { it ->
            it.edtFirstName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    firstName = it.edtFirstName.text.toString()
                    checkRequired(firstName, it.tvErrorFirstName)
//                    presenter?.validation(firstName, lastName, email, pass, confirmPass, validEmail(email))
                }

            })

            it.edtLastName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    lastName = it.edtLastName.text.toString()
                    checkRequired(lastName, it.tvErrorLastName)
//                    presenter?.validation(firstName, lastName, email, pass, confirmPass, validEmail(email))
                }

            })

            it.edtEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    it.edtEmail.filters = arrayOf(filter)
                }

                override fun afterTextChanged(s: Editable?) {
                    email = it.edtEmail.text.toString()
                    checkRequired(email, it.tvErrorEmail)
                    if (email.isEmpty()) {
                        it.tvErrorEmail.text = getString(R.string.text_required)
                    } else {
                        if (!validEmail(email)) {
                            it.tvErrorEmail.text = getString(R.string.login_email_error)
                            it.tvErrorEmail.visibility = View.VISIBLE
                        } else {
                            it.tvErrorEmail.text = getString(R.string.text_required)
                            it.tvErrorEmail.visibility = View.GONE
                        }
                    }
//                    presenter?.validation(firstName, lastName, email, pass, confirmPass, validEmail(email))
                }

            })

            it.edtPassWord.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                binding?.edtPassWord?.filters = arrayOf(filter)
                }

                override fun afterTextChanged(s: Editable?) {
                    pass = it.edtPassWord.text.toString()
                    checkRequired(pass, it.tvErrorPass)
                    if (pass.isEmpty()) {
                        it.tvErrorPass.text = getString(R.string.text_required)
                    } else {
                        if (pass.length < 8) {
                            it.tvErrorPass.text = getString(R.string.login_pass_error)
                            it.tvErrorPass.visibility = View.VISIBLE
                        } else {
                            it.tvErrorPass.text = getString(R.string.text_required)
                            it.tvErrorPass.visibility = View.GONE
                        }
                    }
//                    presenter?.validation(firstName, lastName, email, pass, confirmPass, validEmail(email))
                }

            })

            it.edtConfirmPassWord.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                binding?.edtConfirmPassWord?.filters = arrayOf(filter)
                }

                override fun afterTextChanged(s: Editable?) {
                    confirmPass = it.edtConfirmPassWord.text.toString()
                    checkRequired(confirmPass, it.tvErrorConfirmPass)
                    if (confirmPass.isEmpty()) {
                        it.tvErrorConfirmPass.text = getString(R.string.text_required)
                    } else {
                        if (confirmPass.length < 8) {
                            it.tvErrorConfirmPass.text = getString(R.string.login_pass_error)
                            it.tvErrorConfirmPass.visibility = View.VISIBLE
                        } else {
                            if (confirmPass != pass) {
                                it.tvErrorConfirmPass.text =
                                    getString(R.string.register_text_pass_confirm_fail)
                                it.tvErrorConfirmPass.visibility = View.VISIBLE
                            } else {
                                it.tvErrorConfirmPass.text = getString(R.string.text_required)
                                it.tvErrorConfirmPass.visibility = View.GONE
                            }
                        }
                    }
//                    presenter?.validation(firstName, lastName, email, pass, confirmPass, validEmail(email))
                }

            })
        }
    }

    private fun checkRequired(str: String, textView: TextView?) {
        if (str.isNotEmpty()) {
            textView?.visibility = View.GONE
        } else {
            textView?.visibility = View.VISIBLE
        }
    }

    private fun signInFacebook() {
//        showLoading(true, "Sign In", "Loading...")
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile", "email"))
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                @SuppressLint("SetTextI18n")
                override fun onSuccess(loginResult: LoginResult) {
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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnSignIn -> replaceFragment(SWLoginFragment(), R.id.container, true, null)
            R.id.btnSignUp -> {
                binding?.let { it ->
                    if (firstName == "") {
                        it.tvErrorFirstName.visibility = View.VISIBLE
                    }
                    if (lastName == "") {
                        it.tvErrorLastName.visibility = View.VISIBLE
                    }
                    if (email == "") {
                        it.tvErrorEmail.visibility = View.VISIBLE
                    }
                    if (pass == "") {
                        it.tvErrorPass.visibility = View.VISIBLE
                    }
                    if (confirmPass == "") {
                        it.tvErrorConfirmPass.visibility = View.VISIBLE
                    }
                    if (!it.tvErrorFirstName.isShown && !it.tvErrorLastName.isShown && !it.tvErrorEmail.isShown
                        && !it.tvErrorPass.isShown && !it.tvErrorConfirmPass.isShown
                    ) {
                        val request = SWRegisterRequest(email,
                            firstName,
                            lastName,
                            pass,
                            confirmPass,
                            getDeviceID().toString(),
                            Sharepref.getFirebaseToken(
                                context),
                            Const.CLIENT_ID,
                            Const.CLIENT_SECRET)
                        presenter?.register(context, request)
                    }
                }
            }
            R.id.container -> hideSoftKeyBoard(context, binding?.container!!)
            R.id.btnSignInFacebook -> signInFacebook()
            R.id.btnSignInLine -> signInLine()
            R.id.btnPrivacy -> {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(Const.URL_PRIVACY))
                startActivity(i)
            }
        }
    }

    override fun onBackPressed(): Boolean {
        replaceFragment(SWLoginFragment(), R.id.container, true, null)
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWRegisterPresenter()
        presenter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    override fun registerSuccess(result: SWRegisterResponse?) {
        replaceFragment(SWRegisterCompleteFragment(), R.id.container, true, null)
        Log.d("TAG", "registerSuccess: $result")
    }

    override fun loginSuccess(result: SWLoginResponse?) {
        Sharepref.removeUserInfo(context)
        val email = result?.data?.customer?.email.toString()
        val password = ""
        val token = result?.data?.accessToken?.tokenString.toString()
        val refreshToken = result?.data?.refreshToken?.tokenString.toString()
        Sharepref.saveUserInfo(context, email, password, token, refreshToken)

        activity?.finish()
        (activity as SWLoginActivity).replaceActivity(SWMainActivity::class.java)
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun updateButton(valid: Boolean) {
        if (valid) {
//            binding?.btnSignUp?.isEnabled = true
//            binding?.btnSignUp?.setBackgroundResource(R.drawable.bg_button_authentication)
        } else {
//            binding?.btnSignUp?.isEnabled = false
//            binding?.btnSignUp?.setBackgroundResource(R.drawable.bg_button_inactive)
        }
    }

    override fun showNetworkError() {
        showLoading(false,
            getString(R.string.dialog_error_network_title),
            getString(R.string.dialog_error_network_content))
    }

    override fun showIndicator() {
        showLoading(true,
            getString(R.string.dialog_title_sign_up),
            getString(R.string.dialog_loading_content))
    }

    override fun hideIndicator() {
        hideLoading()
    }
}