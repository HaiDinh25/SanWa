package com.sanwashoseki.bookskozuchi.business.more.views

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
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.business.more.models.requests.SWChangePasswordRequest
import com.sanwashoseki.bookskozuchi.business.more.services.SWChangePasswordInterface
import com.sanwashoseki.bookskozuchi.business.more.services.SWChangePasswordPresenter
import com.sanwashoseki.bookskozuchi.databinding.FragmentChangePasswordBinding

class SWChangePasswordFragment : SWBaseFragment(), View.OnClickListener, SWChangePasswordInterface {

    private var binding: FragmentChangePasswordBinding? = null
    private var presenter: SWChangePasswordPresenter? = null

    private var request: SWChangePasswordRequest? = null
    private var oldPass = ""
    private var newPass = ""
    private var reNewPass = ""

    companion object {
        @JvmStatic
        fun newInstance() =
            SWChangePasswordFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_change_password, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        (activity as SWMainActivity).setHeaderInChildrenScreen(getString(R.string.more_change_pass), isSearch = false, isFilter = false)

        validateButton()
        binding?.let { it ->
            it.btnUpdate.setOnClickListener(this)
            it.container.setOnClickListener(this)
        }
    }

    private fun validateButton() {
        binding?.edtCurrentPass?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding?.edtCurrentPass?.filters = arrayOf(filter)
            }

            override fun afterTextChanged(s: Editable?) {
                binding?.let { it ->
                    oldPass = it.edtCurrentPass.text.toString()
                    if (oldPass.isEmpty()) {
                        it.tvErrorCurentPass.text = getText(R.string.text_required)
                    } else {
                        if (oldPass.length < 8) {
                            it.tvErrorCurentPass.text = getText(R.string.login_pass_error)
                            it.tvErrorCurentPass.visibility = View.VISIBLE
                        } else {
                            it.tvErrorCurentPass.visibility = View.GONE
                        }
                    }
                }
                presenter?.validation(oldPass, newPass, checkPass())
            }
        })

        binding?.edtNewPass?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding?.edtNewPass?.filters = arrayOf(filter)
            }

            override fun afterTextChanged(s: Editable?) {
                binding?.let { it ->
                    newPass = it.edtNewPass.text.toString()
                    if (newPass.isEmpty()) {
                        it.tvErrorNewPass.text = getText(R.string.text_required)
                    } else {
                        if (newPass.length < 8) {
                            it.tvErrorNewPass.text = getText(R.string.login_pass_error)
                            it.tvErrorNewPass.visibility = View.VISIBLE
                        } else {
                            it.tvErrorNewPass.visibility = View.GONE
                        }
                    }
                }
                presenter?.validation(oldPass, newPass, checkPass())
            }
        })

        binding?.edtReNewPass?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding?.edtReNewPass?.filters = arrayOf(filter)
            }

            override fun afterTextChanged(s: Editable?) {
                binding?.let { it ->
                    reNewPass = binding?.edtReNewPass?.text.toString()
                    if (reNewPass.isEmpty()) {
                        it.tvErrorConfirmNewPass.text = getText(R.string.text_required)
                    } else {
                        if (checkPass()) {
                            if (reNewPass.length < 8) {
                                it.tvErrorConfirmNewPass.text = getText(R.string.login_pass_error)
                                it.tvErrorConfirmNewPass.visibility = View.VISIBLE
                            } else {
                                it.tvErrorConfirmNewPass.visibility = View.GONE
                            }
                        } else {
                            it.tvErrorConfirmNewPass.text = getText(R.string.register_text_pass_confirm_fail)
                            it.tvErrorConfirmNewPass.visibility = View.VISIBLE
                        }
                    }
                }
                presenter?.validation(oldPass, newPass, checkPass())
            }
        })
    }

    private fun checkPass(): Boolean {
        return binding?.edtNewPass?.text.toString() == binding?.edtReNewPass?.text.toString()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWChangePasswordPresenter()
        presenter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.container -> hideSoftKeyBoard(context, binding?.container!!)
            R.id.btnUpdate -> {
                request = SWChangePasswordRequest(oldPass, newPass, reNewPass)
                presenter?.changePassword(context, request)
            }
        }
    }

    override fun changeSuccess() {
        binding?.let { it ->
            it.edtCurrentPass.text?.clear()
            it.edtNewPass.text?.clear()
            it.edtReNewPass.text?.clear()
            it.edtCurrentPass.focusable
        }
        showLoading(false, "Confirmation", "Change password successful.")
//        replaceFragment(SWMoreFragment.newInstance(), R.id.container, true, null)
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun updateButton(valid: Boolean) {
        if (valid) {
            binding?.btnUpdate?.isEnabled = true
            binding?.btnUpdate?.setBackgroundResource(R.drawable.bg_button_authentication)
        } else {
            binding?.btnUpdate?.isEnabled = false
            binding?.btnUpdate?.setBackgroundResource(R.drawable.bg_button_inactive)
        }
    }

    override fun showIndicator() {
        showLoading(true, getString(R.string.more_change_pass), getString(R.string.dialog_loading_content))
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