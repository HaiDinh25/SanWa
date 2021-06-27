package com.sanwashoseki.bookskozuchi.business.more.views

import android.annotation.SuppressLint
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
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWAddShoppingCartWishListResponse
import com.sanwashoseki.bookskozuchi.business.more.models.requests.SWSendContactRequest
import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWFAQsResponse
import com.sanwashoseki.bookskozuchi.business.more.services.SWHelpContactUsInterface
import com.sanwashoseki.bookskozuchi.business.more.services.SWHelpContactUsPresenter
import com.sanwashoseki.bookskozuchi.databinding.FragmentContactUsBinding
import com.sanwashoseki.bookskozuchi.others.SWDialogThankYou
import com.sanwashoseki.bookskozuchi.utilities.Sharepref

class SWContactUsFragment : SWBaseFragment(), View.OnClickListener, SWHelpContactUsInterface {

    private var binding: FragmentContactUsBinding? = null
    private var presenter: SWHelpContactUsPresenter? = null

    private var userName: String? = null

    companion object {
        @JvmStatic
        fun newInstance(name: String?) =
            SWContactUsFragment().apply {
                arguments = Bundle().apply {
                    userName = name
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contact_us, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        binding?.let { it ->
            if (Sharepref.getUserToken(context) != "") {
                it.tvEmail.text = Sharepref.getUserEmail(context)
                it.tvName.text = userName
            } else {
                it.edtName.visibility = View.VISIBLE
                it.edtEmail.visibility = View.VISIBLE
                it.tvName.visibility = View.GONE
                it.tvEmail.visibility = View.GONE
            }

            it.edtEmail.filters = arrayOf(filter)
            validate()
            it.tvErrorName.visibility = View.GONE
            it.tvErrorEmail.visibility = View.GONE
            it.tvErrorMessage.visibility = View.GONE

            it.container.setOnClickListener(this)
            it.btnSend.setOnClickListener(this)
        }
    }

    fun validate() {
        binding?.let { it ->
            it.edtName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length!! > 0) {
                        it.tvErrorName.visibility = View.GONE
                    } else {
                        it.tvErrorName.visibility = View.VISIBLE
                    }
                }

                override fun afterTextChanged(s: Editable?) {}

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
                    if (it.edtEmail.text.isEmpty()) {
                        it.tvErrorEmail.text = getString(R.string.text_required)
                        it.tvErrorEmail.visibility = View.VISIBLE
                    } else {
                        if (!validEmail(it.edtEmail.text.toString())) {
                            it.tvErrorEmail.text = getString(R.string.login_email_error)
                            it.tvErrorEmail.visibility = View.VISIBLE
                        } else {
                            it.tvErrorEmail.visibility = View.GONE
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {}

            })

            it.edtMessage.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                @SuppressLint("SetTextI18n")
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    it.tvCount.text = s?.length.toString() + "/4000"
                    if (s?.length!! > 0) {
                        it.tvErrorMessage.visibility = View.GONE
                    } else {
                        it.tvErrorMessage.visibility = View.VISIBLE
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWHelpContactUsPresenter()
        presenter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.container -> hideSoftKeyBoard(context, binding?.container!!)
            R.id.btnSend -> {
                binding?.let { it ->
                    var model: SWSendContactRequest? = null
                    if (Sharepref.getUserToken(context) != "") {
                        if (it.edtMessage.text.isNotEmpty()) {
                            model = SWSendContactRequest(getString(R.string.help_contact),
                                it.edtMessage.text.toString(),
                                it.tvName.text.toString(),
                                it.tvEmail.text.toString(),
                                2)
                            presenter?.sendContact(context, model)
                        } else {
                            it.edtMessage.visibility = View.VISIBLE
                        }
                    } else {
                        if (it.edtName.text.isNotEmpty() && it.edtName.text.isNotEmpty() && it.edtName.text.isNotEmpty()) {
                            model = SWSendContactRequest(getString(R.string.help_contact),
                                it.edtMessage.text.toString(),
                                it.edtName.text.toString(),
                                it.edtEmail.text.toString(),
                                2)
                            if (validEmail(it.edtEmail.text.toString())) {
                                presenter?.sendContact(context, model)
                            }
                            return
                        } else {
                            if (it.edtName.text.isEmpty()) {
                                it.tvErrorName.visibility = View.VISIBLE
                            }
                            if (it.edtEmail.text.isEmpty()) {
                                it.tvErrorEmail.visibility = View.VISIBLE
                            }
                            if (it.edtMessage.text.isEmpty()) {
                                it.tvErrorMessage.visibility = View.VISIBLE
                            }
                            return
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun getFQAsSuccess(result: SWFAQsResponse) {}

    override fun sendContactSuccess(result: SWAddShoppingCartWishListResponse) {
        binding?.let { it ->

            it.tvErrorName.visibility = View.GONE
            it.tvErrorEmail.visibility = View.GONE
            it.tvErrorMessage.visibility = View.GONE

            val dialog = SWDialogThankYou(context,
                getString(R.string.shopping_payment_success_thank_you),
                getString(R.string.dialog_contacts_thanks),
                getString(R.string.dialog_go_to_store))
            dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog.show()
            dialog.onCallbackClicked(object : SWDialogThankYou.OnOKClickedListener {
                override fun onOKListener() {
                    (activity as SWMainActivity).replaceActivity(SWMainActivity::class.java)
                    (activity as SWMainActivity).finish()
                }
            })
        }
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun showIndicator() {
        showLoading(true,
            getString(R.string.help_contact),
            getString(R.string.dialog_loading_content))
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