package com.sanwashoseki.bookskozuchi.business.authentication.forgotpassword.views

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.business.authentication.forgotpassword.services.SWResetPasswordInterface
import com.sanwashoseki.bookskozuchi.business.authentication.forgotpassword.services.SWResetPasswordPresenter
import com.sanwashoseki.bookskozuchi.business.authentication.login.views.SWLoginFragment
import com.sanwashoseki.bookskozuchi.databinding.FragmentResetPasswordBinding

class SWResetPasswordFragment : SWBaseFragment(), View.OnClickListener, SWResetPasswordInterface {

    private var binding: FragmentResetPasswordBinding? = null
    private var presenter: SWResetPasswordPresenter? = null

    private var password = ""
    private var confirmPassword = ""

    private var showPass = false
    private var showConfirmPass = false

    companion object {
        @JvmStatic
        fun newInstance() =
            SWResetPasswordFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reset_password, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        validateButton()

        binding?.let { it ->
            it.btnSave.setOnClickListener(this)
            it.container.setOnClickListener(this)
        }
    }

    private fun validateButton() {
        binding?.edtPassWord?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding?.edtPassWord?.filters = arrayOf(filter)
            }
            override fun afterTextChanged(s: Editable?) {
                password = binding?.edtPassWord?.text.toString()
                presenter?.validation(password, confirmPassword)
            }
        })

        binding?.edtConfirmPassWord?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding?.edtConfirmPassWord?.filters = arrayOf(filter)
            }
            override fun afterTextChanged(s: Editable?) {
                confirmPassword = binding?.edtConfirmPassWord?.text.toString()
                presenter?.validation(password, confirmPassword)
            }
        })
    }

    private fun showPass() {
        showPass = !showPass
        if (showPass) {
            binding?.edtPassWord?.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            binding?.edtPassWord?.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }

    private fun showConfirmPass() {
        showConfirmPass = !showConfirmPass
        if (showConfirmPass) {
            binding?.edtConfirmPassWord?.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            binding?.edtConfirmPassWord?.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }


    override fun onClick(v: View) {
        when(v.id) {
//            R.id.btnShowPass -> showPass()
//            R.id.btnShowConfirmPass -> showConfirmPass()
            R.id.container -> hideSoftKeyBoard(context, binding?.container!!)
            R.id.btnSave -> replaceFragment(SWLoginFragment(), R.id.container, true, null)
        }
    }

    override fun onBackPressed(): Boolean {
        replaceFragment(SWConfirmEmailFragment(), R.id.container, true, null)
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWResetPasswordPresenter()
        presenter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    override fun resetSuccess() {
        Log.d("TAG", "resetSuccess: ok")
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun updateButton(valid: Boolean) {
        if (valid) {
            binding?.btnSave?.isEnabled = true
            binding?.btnSave?.setBackgroundResource(R.drawable.bg_button_authentication)
        } else {
            binding?.btnSave?.isEnabled = false
            binding?.btnSave?.setBackgroundResource(R.drawable.bg_button_inactive)
        }
    }

    override fun showNetworkError() {
        showLoading(false, getString(R.string.dialog_error_network_title), getString(R.string.dialog_error_network_content))
    }

    override fun showIndicator() {
        showLoading(true,getString(R.string.dialog_title_sign_reset_pass), getString(R.string.dialog_loading_content))
    }

    override fun hideIndicator() {
        hideLoading()
    }

}