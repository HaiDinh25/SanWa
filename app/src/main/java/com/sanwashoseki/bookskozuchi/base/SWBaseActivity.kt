package com.sanwashoseki.bookskozuchi.base

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.kaopiz.kprogresshud.KProgressHUD
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.others.SWDialogSignIn
import com.sanwashoseki.bookskozuchi.utilities.IOnBackPressed

open class SWBaseActivity : AppCompatActivity() {

    companion object {
        val TAG: String = SWBaseActivity::class.java.simpleName
    }

    private var progressHUD: KProgressHUD? = null
    private val mFragmentManager =
        supportFragmentManager
    private var transaction: FragmentTransaction? = null
    fun replaceFragment(container: Int, fragment: Fragment?, deleteBackStack: Boolean, tag: String?) {
        transaction = mFragmentManager.beginTransaction()
        transaction?.setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        )
        transaction?.replace(container, fragment!!)
        if (deleteBackStack) {
            transaction?.addToBackStack(null)
        } else {
            transaction?.addToBackStack(tag)
        }
        transaction?.commit()
    }

    fun replaceActivity(t: Class<*>?) {
        val intent = Intent(this, t)
        startActivity(intent)
    }

    fun checkExternalStoragePermission() {
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1000)
        }
    }

    override fun onBackPressed() {
        val fragment =
            supportFragmentManager.findFragmentById(R.id.container)
        if (fragment !is IOnBackPressed || !(fragment as IOnBackPressed).onBackPressed()) {
            super.onBackPressed()
        }
    }

    fun showLoading(isLoading: Boolean, title: String, content: String) {
        if (progressHUD == null) {
            progressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .setSize(100, 100)
        }
        progressHUD?.dismiss()
        progressHUD?.show();
    }

    fun hideLoading() {
        progressHUD?.dismiss()
    }

    fun showSignIn() {
        val dialogSignIn = SWDialogSignIn(
            this,
            getString(R.string.dialog_confirm),
            getString(R.string.dialog_confirm_sign_in),
            false
        )
        dialogSignIn?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialogSignIn?.show()
        dialogSignIn?.onCallbackClicked(object : SWDialogSignIn.OnSignInClickedListener {
            override fun onSignInListener() {
                replaceActivity(SWLoginActivity::class.java)
                dialogSignIn.dismiss()
                //finish()
            }
        })
    }
}