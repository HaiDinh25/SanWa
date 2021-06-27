package com.sanwashoseki.bookskozuchi.business.shoppingcart.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.databinding.FragmentPaymentSuccessBinding

class SWPaymentSuccessFragment : SWBaseFragment(), View.OnClickListener {

    private var binding: FragmentPaymentSuccessBinding? = null

    companion object {
        @JvmStatic
        fun newInstance() =
            SWPaymentSuccessFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_success, container, false)

        initUI()
        return binding?.root
    }

    fun initUI() {
        (activity as SWMainActivity).hideHeader()
        binding?.btnApply?.setOnClickListener(this)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed(): Boolean {
        return true
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btnApply -> (activity as SWMainActivity).setBottomNavigationMenu(2)
        }
    }

}