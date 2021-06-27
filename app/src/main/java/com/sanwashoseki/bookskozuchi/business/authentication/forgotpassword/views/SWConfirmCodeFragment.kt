package com.sanwashoseki.bookskozuchi.business.authentication.forgotpassword.views

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.business.authentication.forgotpassword.services.SWConfirmCodeInterface
import com.sanwashoseki.bookskozuchi.business.authentication.forgotpassword.services.SWConfirmCodePresenter
import com.sanwashoseki.bookskozuchi.databinding.FragmentConfirmCodeBinding

class SWConfirmCodeFragment : SWBaseFragment(), View.OnClickListener, SWConfirmCodeInterface {

    private var binding: FragmentConfirmCodeBinding? = null
    private var presenter: SWConfirmCodePresenter? = null

    private var code = ""

    companion object {

        @JvmStatic
        fun newInstance() =
            SWConfirmCodeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_confirm_code, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        binding?.edtCode?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                code = binding?.edtCode?.text.toString()
                presenter?.validation(code)
            }
        })

        binding?.let { it ->
            it.btnSendCode.setOnClickListener(this)
            it.container.setOnClickListener(this)
        }
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.container -> hideSoftKeyBoard(context, binding?.container!!)
            R.id.btnSendCode -> replaceFragment(SWResetPasswordFragment(), R.id.container, true, null)
        }
    }

    override fun onBackPressed(): Boolean {
        replaceFragment(SWConfirmEmailFragment(), R.id.container, true, null)
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWConfirmCodePresenter()
        presenter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    override fun confirmSuccess() {
        TODO("Not yet implemented")
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun updateButton(valid: Boolean) {
        if (valid) {
            binding?.btnSendCode?.isEnabled = true
            binding?.btnSendCode?.setBackgroundResource(R.drawable.bg_button_authentication)
        } else {
            binding?.btnSendCode?.isEnabled = false
            binding?.btnSendCode?.setBackgroundResource(R.drawable.bg_button_inactive)
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