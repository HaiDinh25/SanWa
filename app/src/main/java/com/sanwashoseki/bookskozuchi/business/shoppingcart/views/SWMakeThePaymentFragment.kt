package com.sanwashoseki.bookskozuchi.business.shoppingcart.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWAddShoppingCartWishListResponse
import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.requests.SWMakeThePaymentRequest
import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.requests.SWShoppingCartRequest
import com.sanwashoseki.bookskozuchi.business.shoppingcart.services.SWMakeThePaymentInterface
import com.sanwashoseki.bookskozuchi.business.shoppingcart.services.SWMakeThePaymentPresenter
import com.sanwashoseki.bookskozuchi.databinding.FragmentMakeThePaymentBinding
import com.sanwashoseki.bookskozuchi.utilities.SWUIHelper
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class SWMakeThePaymentFragment : SWBaseFragment(), View.OnClickListener, SWMakeThePaymentInterface {

    private var books: List<SWShoppingCartRequest>? = null
    private var binding: FragmentMakeThePaymentBinding? = null
    private var presenter: SWMakeThePaymentPresenter? = null

    companion object {
        @JvmStatic
        fun newInstance(listBook: List<SWShoppingCartRequest>?) =
            SWMakeThePaymentFragment().apply {
                arguments = Bundle().apply {
                    books = listBook
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_make_the_payment,
            container,
            false
        )

        initUI()
        return binding?.root
    }

    @SuppressLint("SetTextI18n")
    private fun initUI() {
        (activity as SWMainActivity).hideBottomNavigationMenu(true)
        (activity as SWMainActivity).setHeaderInChildrenScreen(getString(R.string.shopping_payment_payment_title), isSearch = false, isFilter = false)

        presenter?.validation(binding?.edtCardNumber?.text.toString(), binding?.edtCVV?.text.toString())

        var amount = 0
        for (element in books!!) {
            amount = (amount + element.book.price!!).roundToInt()
        }
        setExpirationTime()

        binding?.let { it ->
            it.tvAmount.text = getString(R.string.currency) + " " + SWUIHelper.formatTextPrice(
                amount.toDouble()
            )

            it.edtCardNumber.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
//                    checkRequired(it.edtCardNumber.text.toString(), it.tvErrorCardNum)
                    presenter?.validation(binding?.edtCardNumber?.text.toString(), binding?.edtCVV?.text.toString())
                }
            })

            it.edtCVV.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
//                    checkRequired(it.edtCVV.text.toString(), it.tvErrorCvv)
                    presenter?.validation(binding?.edtCardNumber?.text.toString(), binding?.edtCVV?.text.toString())
                }
            })

            it.btnMakeThePayment.setOnClickListener(this)
            it.container.setOnClickListener(this)
        }

    }

    private fun checkRequired(str: String, textView: TextView?) {
        if (str.isNotEmpty()) {
            textView?.visibility = View.GONE
        } else {
            textView?.visibility = View.VISIBLE
        }
    }

    private fun setExpirationTime() {
        val years = ArrayList<Int>()
        val months = ArrayList<Int>()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        for (i in 1..20) {
            years.add(currentYear - 1 + i)
            if (i < 13) {
                months.add(i)
            }
        }
        val yearAdapter: ArrayAdapter<Int> = ArrayAdapter<Int>(requireContext(), R.layout.spinner_item_payment, years)
        binding?.spinnerYear?.adapter = yearAdapter
        val monthAdapter: ArrayAdapter<Int> = ArrayAdapter<Int>(requireContext(), R.layout.spinner_item_payment, months)
        binding?.spinnerMonth?.adapter = monthAdapter
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWMakeThePaymentPresenter()
        presenter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btnMakeThePayment -> {
                binding?.let { it ->
                    val name = it.edtNameCard.text.toString()
                    val number = it.edtCardNumber.text.toString()
                    val cvv = it.edtCVV.text.toString()
                    val month = it.spinnerMonth.selectedItem.toString()
                    val year = it.spinnerYear.selectedItem.toString()
                    val ids = ArrayList<Int?>()
                    for (i in books!!.indices) {
                        ids.add(books!![i].book.shoppingCartItemId)
                    }
                    val model = SWMakeThePaymentRequest(name, number, month, year, cvv, ids)

                    presenter?.makeThePayment(context, model)
                }
            }
            R.id.container -> hideSoftKeyBoard(context, binding?.container!!)
        }
    }

    override fun makePaymentSuccess(result: SWAddShoppingCartWishListResponse?) {
        replaceFragment(SWPaymentSuccessFragment.newInstance(), R.id.container, true, null)
    }

    override fun updateButton(isValid: Boolean) {
        binding?.let { it ->
            if (isValid) {
                it.btnMakeThePayment.isEnabled = true
                it.btnMakeThePayment.setBackgroundResource(R.drawable.bg_button_authentication)
            } else {
                it.btnMakeThePayment.isEnabled = false
                it.btnMakeThePayment.setBackgroundResource(R.drawable.bg_button_inactive)
            }
        }
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun showIndicator() {
        showLoading(true, "Payment", getString(R.string.dialog_loading_content))
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