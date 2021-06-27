package com.sanwashoseki.bookskozuchi.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.provider.Settings
import android.text.InputFilter
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.kaopiz.kprogresshud.KProgressHUD
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.others.SWDialogLoading
import com.sanwashoseki.bookskozuchi.others.SWDialogSignIn
import com.sanwashoseki.bookskozuchi.utilities.IOnBackPressed
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*

open class SWBaseFragment : Fragment(), IOnBackPressed {

    private var mFragmentManager: FragmentManager? = null
    private val listener: OnDateSelectedListener? = null
    private var listenerConfirm: OnConfirmListener? = null
    private var progressHUD: KProgressHUD? = null
    private var dialogLoading: SWDialogLoading? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFragmentManager = fragmentManager
    }

    fun replaceFragment(
        fragment: Fragment?,
        container: Int,
        deleteInBackStack: Boolean,
        tag: String?
    ) {
        val transaction = mFragmentManager!!.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        )
        transaction.replace(container, fragment!!)
        if (!deleteInBackStack) {
            transaction.addToBackStack(tag)
        }
        transaction.commit()
    }

    fun hideSoftKeyBoard(context: Context?, view: View) {
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun validEmail(email: String?): Boolean {
        val emailPattern = "^[_A-Za-z0-9]+(\\.[_A-Za-z0-9]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,150})\$"
        return email.toString().trim { it <= ' ' }.matches(emailPattern.toRegex())
    }

    //remove space
    var filter = InputFilter { source, start, end, _, _, _ ->
        for (i in start until end) {
            if (Character.isWhitespace(source[i])) {
                return@InputFilter ""
            }
        }
        null
    }

    @SuppressLint("HardwareIds")
    fun getDeviceID(): String? {
        return Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun removeSpace(s: String) : String {
        return s.replace("\\s".toRegex(), "")
    }

    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            activity?.contentResolver?.query(uri, null, null, null, null).use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }
        return result
    }

    @SuppressLint("PackageManagerGetSignatures")
    fun printKeyHash() {
        try {
            val packageName = requireActivity().applicationContext.packageName
            val info = requireActivity().packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d(
                    "KeyHash:",
                    Base64.encodeToString(md.digest(), Base64.DEFAULT)
                )
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("KeyHash:", e.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("KeyHash:", e.toString())
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDateString(year: Int, mMonth: Int, mDay: Int): String? {
        val calendar = Calendar.getInstance()
        calendar[year, mMonth] = mDay
        val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
        return dateFormat.format(calendar.time)
    }

    open fun openDatePickerDialog(v: View) {
        // Get Current Date
        val cal = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireActivity(),
            { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                (v as TextView).text = getDateString(year, monthOfYear, dayOfMonth)
                listener?.onDateSelectedListener((v).text.toString())
            },
            cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.datePicker
        datePickerDialog.window!!.attributes.windowAnimations = R.style.DialogBotToTopAnim
        datePickerDialog.show()
    }

    fun showDialogConfirm(content: String, expiredToken: Boolean) {
        val dialog = SWDialogSignIn(requireContext(), getString(R.string.dialog_confirm), content, expiredToken)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.show()
        dialog.onCallbackClicked(object : SWDialogSignIn.OnSignInClickedListener {
            override fun onSignInListener() {
                listenerConfirm?.onConfirmListener()
                dialog.dismiss()
            }
        })
    }

    fun showLoading(isLoading: Boolean, title: String, content: String) {
        if (dialogLoading == null) {
            dialogLoading = SWDialogLoading(requireActivity(), isLoading, "", content)
            dialogLoading?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
        dialogLoading?.show()
    }

    fun showLoading() {
        if (progressHUD == null) {
            progressHUD = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .setSize(100, 100)
        }
        progressHUD?.dismiss()
        progressHUD?.show()
    }

    fun hideLoading() {
        progressHUD?.dismiss()
        dialogLoading?.dismiss()
    }

    override fun onBackPressed(): Boolean {
        mFragmentManager?.popBackStack()
        return false
    }

    fun onCallBackConfirm(listener: OnConfirmListener) {
        listenerConfirm = listener
    }

    interface OnConfirmListener {
        fun onConfirmListener()
    }

    interface OnDateSelectedListener {
        fun onDateSelectedListener(date: String?)
    }
}