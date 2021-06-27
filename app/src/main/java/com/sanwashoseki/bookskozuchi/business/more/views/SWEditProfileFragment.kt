package com.sanwashoseki.bookskozuchi.business.more.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.loader.content.CursorLoader
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.business.more.models.requests.SWUpdateProfileRequest
import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWProfileResponse
import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWUpdateProfileResponse
import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWUploadAvatarResponse
import com.sanwashoseki.bookskozuchi.business.more.services.SWUpdateAvatarInterface
import com.sanwashoseki.bookskozuchi.business.more.services.SWUpdateAvatarPresenter
import com.sanwashoseki.bookskozuchi.business.more.services.SWUpdateProfileInterface
import com.sanwashoseki.bookskozuchi.business.more.services.SWUpdateProfilePresenter
import com.sanwashoseki.bookskozuchi.databinding.FragmentEditProfileBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.net.URISyntaxException
import java.text.DecimalFormat
import java.util.*


class SWEditProfileFragment : SWBaseFragment(), View.OnClickListener, SWUpdateProfileInterface,
    SWUpdateAvatarInterface {

    private var binding: FragmentEditProfileBinding? = null
    private var preProfile: SWUpdateProfilePresenter? = null
    private var preUpdateAvatar: SWUpdateAvatarPresenter? = null

    private var getProfile: SWProfileResponse? = null
    private var updateProfile: SWUpdateProfileRequest? = null
    private var gender = "M"

    companion object {
        @JvmStatic
        fun newInstance(input: SWProfileResponse?) =
            SWEditProfileFragment().apply {
                arguments = Bundle().apply {
                    getProfile = input
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        (activity as SWMainActivity).hideBottomNavigationMenu(true)
        (activity as SWMainActivity).setHeaderInChildrenScreen(getString(R.string.more_edit_profile),
            isSearch = false,
            isFilter = false)

        genderSelected()
        binding?.let { it ->
            it.edtFirstName.setText(getProfile?.data?.firstName)
            it.edtLastName.setText(getProfile?.data?.lastName)
            it.edtEmail.setText(getProfile?.data?.email)
            it.edtPhone.setText(getProfile?.data?.phoneNumber)
            it.edtAddress.setText(getProfile?.data?.address)
            Glide.get(requireContext()).clearMemory()
            Glide.with(requireContext())
                .load(getProfile?.data?.avatarUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(it.imgAvatar)

            when (getProfile?.data?.gender) {
                "M" -> {
                    gender = "M"
                    it.radioMale.isChecked = true
                    it.radioFemale.isChecked = false
                }
                "F" -> {
                    gender = "F"
                    it.radioMale.isChecked = false
                    it.radioFemale.isChecked = true
                }
            }
            it.edtBirthDay.setText(getProfile?.data?.birthday)

            preProfile?.validationButton(getProfile?.data?.firstName.toString(),
                getProfile?.data?.lastName.toString(),
                getProfile?.data?.phoneNumber.toString(),
                getProfile?.data?.address.toString())

            validationButton()
            it.btnChangePass.setOnClickListener(this)
            it.edtBirthDay.setOnClickListener(this)
            it.btnUpdate.setOnClickListener(this)
            it.container.setOnClickListener(this)
            it.imgAvatar.setOnClickListener(this)
        }
    }

    private fun validationButton() {
        binding?.let { it ->
            it.edtFirstName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    checkRequired(it.edtFirstName.text.toString(), it.tvErrorFirstName)
//                    valid()
                }
            })

            it.edtLastName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    checkRequired(it.edtLastName.text.toString(), it.tvErrorLastName)
//                    valid()
                }
            })

            it.edtPhone.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    checkRequired(it.edtPhone.text.toString(), it.tvErrorPhone)
//                    valid()
                }
            })

            it.edtAddress.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    checkRequired(it.edtAddress.text.toString(), it.tvErrorAddress)
//                    valid()
                }
            })
        }
    }

    fun valid() {
        binding?.let { it ->
            preProfile?.validationButton(it.edtFirstName.text.toString(),
                it.edtLastName.text.toString(),
                it.edtPhone.text.toString(),
                it.edtAddress.text.toString())
        }
    }

    private fun checkRequired(str: String, textView: TextView?) {
        if (str.isNotEmpty()) {
            textView?.visibility = View.GONE
        } else {
            textView?.visibility = View.VISIBLE
        }
    }

    private fun showTextWarning() {
        binding?.let { it ->
            when {
                it.edtFirstName.text.isEmpty() -> {
                    checkRequired(it.edtFirstName.text.toString(), it.tvErrorFirstName)
                }
                it.edtLastName.text.isEmpty() -> {
                    checkRequired(it.edtLastName.text.toString(), it.tvErrorLastName)
                }
                it.edtPhone.text.isEmpty() -> {
                    checkRequired(it.edtPhone.text.toString(), it.tvErrorPhone)
                }
                it.edtAddress.text.isEmpty() -> {
                    checkRequired(it.edtAddress.text.toString(), it.tvErrorAddress)
                }
            }
        }
    }

    fun checkIsNotNull(): Boolean {
        if (binding?.edtFirstName?.text.toString()
                .isNotEmpty() && binding?.edtLastName?.text.toString()
                .isNotEmpty() && binding?.edtPhone?.text.toString()
                .isNotEmpty() && binding?.edtAddress?.text.toString().isNotEmpty()
        )
            return true
        return false
    }

    private fun genderSelected() {
        binding?.radioGroup?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioMale -> {
                    gender = "M"
                }
                R.id.radioFemale -> {
                    gender = "F"
                }
            }
            Log.d("TAG", "genderSelected: $gender")
        }
    }

    private fun getFile() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    private var uploadFile: MultipartBody.Part? = null
    private var fileSize = 0

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val uri: Uri = data.data!!
                try {
                    val file = File(getRealPathFromURI(uri))
                    val requestFile = RequestBody.create(
                        activity?.contentResolver?.getType(uri).toString().toMediaTypeOrNull(), file
                    )
                    uploadFile = MultipartBody.Part.createFormData(
                        "uploadedFile",
                        file.name,
                        requestFile
                    )

                    val miB = (1024 * 1024).toLong()
                    val format = DecimalFormat("#.##")
                    fileSize = format.format(file.length() / miB).toInt()
                    if (fileSize <= 10) {
                        Glide.with(requireContext())
                            .load(uri)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(binding!!.imgAvatar)
//                        preUpdateAvatar?.updateAvatar(context, uploadFile)
                    }
                } catch (e: URISyntaxException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(requireContext(), contentUri, proj, null, null, null)
        val cursor = loader.loadInBackground()
        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val result = cursor.getString(column_index)
        cursor.close()
        return result
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        preProfile = SWUpdateProfilePresenter()
        preUpdateAvatar = SWUpdateAvatarPresenter()
        preProfile?.attachView(this)
        preUpdateAvatar?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        preProfile?.detachView()
        preUpdateAvatar?.detachView()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.container -> hideSoftKeyBoard(context, binding?.container!!)
            R.id.btnChangePass -> replaceFragment(
                SWChangePasswordFragment.newInstance(),
                R.id.container,
                false,
                null
            )
            R.id.edtBirthDay -> openDatePickerDialog(binding?.edtBirthDay!!)
            R.id.btnUpdate -> {
                binding?.let { it ->
                    val firstName = it.edtFirstName.text.toString()
                    val lastName = it.edtLastName.text.toString()
                    val phone = it.edtPhone.text.toString()
                    val address = it.edtAddress.text.toString()
                    val birthday = it.edtBirthDay.text.toString()

                    updateProfile = SWUpdateProfileRequest(firstName,
                        lastName,
                        phone,
                        address,
                        gender,
                        birthday)
                }
                if (checkIsNotNull()) {
                    if (uploadFile != null) {
                        if (fileSize <= 10) {
                            preUpdateAvatar?.updateAvatar(context, uploadFile)
                        }
                    }
                    preProfile?.updateUserInfo(context, updateProfile)
                } else {
                    showTextWarning()
                }
            }
            R.id.imgAvatar -> {
                val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                if (permissions.map { ContextCompat.checkSelfPermission(requireContext(),
                        it) }.any { it != PackageManager.PERMISSION_GRANTED }) {
                    ActivityCompat.requestPermissions(requireActivity(), permissions, 1000)
                } else {
                    getFile()
                }
            }
        }
    }

    override fun updateSuccess(result: SWUpdateProfileResponse) {}

    override fun updateButton(isValid: Boolean) {
        binding?.let { it ->
            if (isValid) {
                it.btnUpdate.isEnabled = true
                it.btnUpdate.setBackgroundResource(R.drawable.bg_button_authentication)
            } else {
                it.btnUpdate.isEnabled = false
                it.btnUpdate.setBackgroundResource(R.drawable.bg_button_inactive)
            }
        }
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun updateAvatarSuccess(result: SWUploadAvatarResponse) {
        binding?.let { it ->
            Glide.with(this)
                .load(result.data)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(it.imgAvatar)
        }
        uploadFile = null
        preProfile?.updateUserInfo(context, updateProfile)

        showLoading(false,
            getString(R.string.dialog_confirm),
            getString(R.string.more_edit_profile_success))
    }

    override fun showMsgUpdateAvatarError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun showIndicator() {
        showLoading()
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