package com.sanwashoseki.bookskozuchi.business.authentication.register.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.databinding.FragmentRegisterCompleteBinding
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.business.authentication.login.views.SWLoginFragment

class SWRegisterCompleteFragment : SWBaseFragment(), View.OnClickListener {

    private var binding: FragmentRegisterCompleteBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register_complete, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        binding?.btnGotoSignIn?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btnGotoSignIn -> replaceFragment(SWLoginFragment(), R.id.container, true, null)
        }
    }

    override fun onBackPressed(): Boolean {
        replaceFragment(SWRegisterFragment(), R.id.container, true, null)
        return true
    }
}