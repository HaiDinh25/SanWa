package com.sanwashoseki.bookskozuchi.business.authentication.forgotpassword.views

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.business.authentication.forgotpassword.models.requests.SWConfirmEmailRequest
import com.sanwashoseki.bookskozuchi.business.authentication.forgotpassword.services.SWConfirmEmailInterface
import com.sanwashoseki.bookskozuchi.business.authentication.forgotpassword.services.SWConfirmEmailPresenter
import com.sanwashoseki.bookskozuchi.business.authentication.login.views.SWLoginFragment
import com.sanwashoseki.bookskozuchi.business.authentication.register.models.responses.SWRegisterResponse
import com.sanwashoseki.bookskozuchi.databinding.FragmentConfirmEmailBinding
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.others.SWDialogThankYou

class SWConfirmEmailFragment : SWBaseFragment(), View.OnClickListener, SWConfirmEmailInterface {

    private var binding: FragmentConfirmEmailBinding? = null
    private var presenter: SWConfirmEmailPresenter? = null

    private var email = ""

    companion object {

        @JvmStatic
        fun newInstance() =
            SWConfirmEmailFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_confirm_email, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        binding?.edtEmail?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                email = binding?.edtEmail?.text.toString()
                presenter?.validation(email, validEmail(email))
            }
        })

        binding?.let { it ->
            it.btnSendEmail.setOnClickListener(this)
            it.container.setOnClickListener(this)
        }
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.container -> hideSoftKeyBoard(context, binding?.container!!)
            R.id.btnSendEmail -> {
                val request = SWConfirmEmailRequest(binding?.edtEmail?.text.toString(), Const.CLIENT_ID, Const.CLIENT_SECRET)
                presenter?.confirmEmail(context, request)
//                replaceFragment(SWConfirmCodeFragment(), R.id.container, true, null)
            }
        }
    }

    override fun onBackPressed(): Boolean {
        replaceFragment(SWLoginFragment(), R.id.container, true, null)
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWConfirmEmailPresenter()
        presenter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    override fun confirmSuccess(result: SWRegisterResponse?) {
        val dialog = SWDialogThankYou(context, getString(R.string.forgot_pass_thank_you_title), getString(R.string.forgot_pass_thank_you_content), "OK")
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.show()
        dialog.onCallbackClicked(object : SWDialogThankYou.OnOKClickedListener {
            override fun onOKListener() {
                replaceFragment(SWLoginFragment(), R.id.container, true, null)
            }
        })
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun updateButton(valid: Boolean) {
        if (valid) {
            binding?.btnSendEmail?.isEnabled = true
            binding?.btnSendEmail?.setBackgroundResource(R.drawable.bg_button_authentication)
        } else {
            binding?.btnSendEmail?.isEnabled = false
            binding?.btnSendEmail?.setBackgroundResource(R.drawable.bg_button_inactive)
        }
    }

    override fun showNetworkError() {
        showLoading(false, getString(R.string.dialog_error_network_title), getString(R.string.dialog_error_network_content))
    }

    override fun showIndicator() {
        showLoading(true,getString(R.string.dialog_title_sign_confirm_email), getString(R.string.dialog_loading_content))
    }

    override fun hideIndicator() {
        hideLoading()
    }

}