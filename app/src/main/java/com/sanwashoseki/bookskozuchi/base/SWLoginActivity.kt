package com.sanwashoseki.bookskozuchi.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.authentication.login.views.SWLoginFragment
import com.sanwashoseki.bookskozuchi.utilities.Sharepref

class SWLoginActivity : SWBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Sharepref.getNightMode(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        setContentView(R.layout.activity_login)

        replaceFragment(R.id.container, SWLoginFragment(), true, null)
    }
}