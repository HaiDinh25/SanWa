package com.sanwashoseki.bookskozuchi.business.more.views

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.*
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.business.more.dialogs.SWDialogInputPassCode
import com.sanwashoseki.bookskozuchi.databinding.FragmentSettingAuthenticateBinding
import com.sanwashoseki.bookskozuchi.utilities.Sharepref

enum class BiometricAuthenticateResult {
    Available,
    NotSupport,
    NoneEnrolled
}

class SWSettingAuthenticateFragment : Fragment(), SWDialogInputPassCode.OnInPutPassCodeListener {

    private var binding: FragmentSettingAuthenticateBinding? = null
    private var isPassCodeEnabled = false
    private var isBiometricEnabled = false

    private val REQUEST_CODE_BIOMETRIC_AUTHENTICATORS_ALLOWED = 1000

    companion object {
        fun newInstance(): SWSettingAuthenticateFragment {
            val fragment = SWSettingAuthenticateFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_setting_authenticate,
            container,
            false
        )

        initUI()
        return binding?.root
    }

    private fun initUI() {
        binding?.let { it ->
            (activity as SWMainActivity).hideBottomNavigationMenu(true)
            (activity as SWMainActivity).setHeaderInChildrenScreen(
                getString(R.string.more_setting_auth),
                isSearch = false,
                isFilter = false
            )

            it.switchPassCode.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked && !Sharepref.getPassCodeAuth(context)) {
                    val dialog = SWDialogInputPassCode(requireContext(), false)
                    dialog.show()
                    dialog.onCallbackListener(this)
                }
                Sharepref.setPassCodeAuth(context, isChecked)
                if (!isChecked) {
                    isBiometricEnabled = false
                    isPassCodeEnabled = false
                    Sharepref.setPassCodeAuth(context, false)
                    Sharepref.setBiometricAuth(context, false)
                    it.switchBiometricAuth.isChecked = false
                    it.layoutBiometricAuth.visibility = View.GONE
                    it.biometricAuthenDescription.visibility = View.GONE
                }
            }
            isPassCodeEnabled = Sharepref.getPassCodeAuth(context)
            it.switchPassCode.isChecked = Sharepref.getPassCodeAuth(context)
            it.switchBiometricAuth.isChecked = Sharepref.getBiometricAuth(context)
            it.layoutBiometricAuth.visibility = if (isPassCodeEnabled) View.VISIBLE else View.GONE
            binding?.switchBiometricAuth?.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    checkBiometricAuthenticateAvailable()
                    setBiometricAuthen(true)
                } else {
                    setBiometricAuthen(false)
                }
            }
        }
    }

    private fun setupBiometricLayoutVisibility() {
        val isBiometricAuthenAvailable = checkBiometricAuthenticateAvailable()
        when (isBiometricAuthenAvailable) {
            BiometricAuthenticateResult.Available -> {
                binding?.biometricAuthenDescription?.visibility = View.GONE
                enableBiometricLayout()
            }
            BiometricAuthenticateResult.NoneEnrolled -> {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R && isPassCodeEnabled) {
                    binding?.layoutBiometricAuth?.visibility = View.VISIBLE
                    binding?.switchBiometricAuth?.isEnabled = false
                    binding?.biometricAuthenDescription?.visibility = View.VISIBLE
                    binding?.switchBiometricAuth?.setOnCheckedChangeListener(null)
                }
            }
            BiometricAuthenticateResult.NotSupport -> binding?.layoutBiometricAuth?.visibility = View.GONE
        }
    }

    private fun enableBiometricLayout() {
        binding?.layoutBiometricAuth?.visibility = View.VISIBLE
        binding?.switchBiometricAuth?.isEnabled = true
        binding?.switchBiometricAuth?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkBiometricAuthenticateAvailable()
                setBiometricAuthen(true)
            } else {
                setBiometricAuthen(false)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R && isPassCodeEnabled) {
            setupBiometricLayoutVisibility()
        }
    }

    private fun setBiometricAuthen(isEnabled: Boolean) {
        isBiometricEnabled = isEnabled
        Sharepref.setBiometricAuth(context, isEnabled)
    }

    private fun checkBiometricAuthenticateAvailable(): BiometricAuthenticateResult {
        // if version SDK < R, can not prompts the user to create credentials that your app accepts
        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(BIOMETRIC_WEAK)) {
            BIOMETRIC_SUCCESS -> return BiometricAuthenticateResult.Available
            BIOMETRIC_ERROR_NO_HARDWARE -> return BiometricAuthenticateResult.NotSupport
            BIOMETRIC_ERROR_HW_UNAVAILABLE -> return BiometricAuthenticateResult.NotSupport
            BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Prompts the user to create credentials that your app accepts.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                        putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BIOMETRIC_WEAK)
                    }
                    startActivityForResult(
                        enrollIntent,
                        REQUEST_CODE_BIOMETRIC_AUTHENTICATORS_ALLOWED
                    )
                }
                return BiometricAuthenticateResult.NoneEnrolled
            }
        }
        return BiometricAuthenticateResult.NotSupport
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_BIOMETRIC_AUTHENTICATORS_ALLOWED) {
            if (resultCode == Activity.RESULT_OK) {
                setBiometricAuthen(true)
            } else {
                //user canceled biometric authentication setting
                binding?.switchBiometricAuth?.isChecked = false
                setBiometricAuthen(false)
            }
        }
    }

    override fun onCancelListener() {
        binding?.let { it ->
            isPassCodeEnabled = false
            it.switchPassCode.isChecked = false
            Sharepref.setPassCodeAuth(context, false)
            it.layoutBiometricAuth.visibility = View.GONE
            it.biometricAuthenDescription.visibility = View.GONE
        }
    }

    override fun onSuccessListener(passCode: String) {
        binding?.let { it ->
            isPassCodeEnabled = true
            Sharepref.setPassCodeValue(context, passCode)
            Sharepref.setPassCodeAuth(context, isPassCodeEnabled)
            enableBiometricLayout()
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R && isPassCodeEnabled) {
                setupBiometricLayoutVisibility()
            }
        }
    }
}