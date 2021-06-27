package com.sanwashoseki.bookskozuchi.business.ebookrequest.dialogs

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
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
import android.view.Window
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sanwashoseki.bookskozuchi.BuildConfig
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.databinding.DialogReplyRequestBinding
import com.sanwashoseki.bookskozuchi.others.SWDialogLoading
import com.sanwashoseki.bookskozuchi.utilities.SWPathUtil.createImageFile
import com.sanwashoseki.bookskozuchi.utilities.SWPathUtil.getRealPathFromURI
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.net.URISyntaxException
import java.text.DecimalFormat

class SWDialogReplyRequest(private var name: String?) : BottomSheetDialogFragment(),
    View.OnClickListener {

    private val TAG = "SWDialogReplyRequest"
    private var binding: DialogReplyRequestBinding? = null
    private var listener: OnReplyListener? = null

    private val REQUEST_CODE_STORAGE_PERMISSION = 1000
    private val REQUEST_CODE_CAMERA_PERMISSION = 1001
    private val REQUEST_CODE_GET_IMAGE = 1002
    private val REQUEST_CODE_TAKE_PHOTO = 1003

    private var uri: Uri? = null
    private var file: File? = null
    private var requestFile: RequestBody? = null
    private var multipartBody = ArrayList<MultipartBody.Part>()
    private lateinit var photoPath: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_reply_request, container, false)
        initUi()
        return binding?.root
    }

    @SuppressLint("SetTextI18n")
    private fun initUi() {
        binding?.let { it ->
            it.tvReplyTo.text = name + getString(R.string.request_reply_title)

            it.edtContent.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                @SuppressLint("SetTextI18n")
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    binding?.tvCount?.text = s?.length.toString() + "/255"
                }

                override fun afterTextChanged(s: Editable?) {}
            })
            it.btnSendReply.setOnClickListener(this)
            it.btnCamera.setOnClickListener(this)
            it.btnImage.setOnClickListener(this)
            it.btnRemove.setOnClickListener(this)
        }
    }

    fun onCallBacReply(listener: OnReplyListener?) {
        this.listener = listener
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnCamera -> {
                takePhoto()
            }
            R.id.btnImage -> {
                val permissionCheck = checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    pickImage()
                } else {
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_CODE_STORAGE_PERMISSION
                    )
                }
            }
            R.id.btnSendReply -> {
                if (uri != null) {
                    file = File(photoPath)
                    requestFile =
                        RequestBody.create(
                            requireActivity().contentResolver.getType(uri!!)
                                .toString().toMediaTypeOrNull(), file!!
                        )
                    multipartBody.add(
                        MultipartBody.Part.createFormData(
                            "file",
                            file!!.name,
                            requestFile!!
                        )
                    )
                }
                if (binding?.edtContent?.text?.isBlank() == true) {
                    binding?.tvErrorContent?.visibility = View.VISIBLE
                } else {
                    listener?.onReplyListener(binding?.edtContent?.text.toString(), multipartBody)
                }
            }
            R.id.btnRemove -> {
                uri = null
                photoPath = ""
                binding?.cardView?.visibility = View.GONE
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.get(0) == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                REQUEST_CODE_CAMERA_PERMISSION -> {
                    context?.let { dispatchTakePictureIntent(it) }
                }
                REQUEST_CODE_STORAGE_PERMISSION -> pickImage()
            }
        }
    }

    private fun takePhoto() {
        val permissionCheck = checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        )
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            try {
                context?.let { dispatchTakePictureIntent(it) }
            } catch (e: ActivityNotFoundException) {
                Log.e(TAG, "Take a photo error !",)
            }
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA_PERMISSION)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent(context: Context) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(this.requireActivity().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Log.d(TAG, "Has an error when create file image: ")
                    null
                }
                if (photoFile != null) {
                    photoPath = photoFile.absolutePath
                }

                photoFile?.also {
                    uri = FileProvider.getUriForFile(
                        context,
                        BuildConfig.APPLICATION_ID + ".provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PHOTO)
                }
            }
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        //intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_GET_IMAGE)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            //val photo = data?.extras!!["data"] as Bitmap?
            binding?.cardView?.visibility = View.VISIBLE
            binding?.imageView?.setImageURI(uri)
        }

        ///storage/emulated/0/Android/data/com.bookskozuchi.android/files/Pictures/JPEG_20210405160914_6977613667787323563.jpg

        if (requestCode == REQUEST_CODE_GET_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                uri = data.data
                photoPath = uri?.let { getRealPathFromURI(it) }.toString()
                try {
                    val file = File(photoPath)

                    Log.d("TAG", "onActivityResult: $file")
                    val miB = (1024 * 1024).toLong()
                    val format = DecimalFormat("#.##")
                    val fileSize = format.format(file.length() / miB).toInt()
                    if (fileSize <= 10) {
                        binding?.let { it ->
                            it.cardView.visibility = View.VISIBLE
                            it.imageView.setImageURI(uri)
                        }
                    } else {
                        val dialog = SWDialogLoading(
                            requireActivity(),
                            false,
                            "",
                            getString(R.string.request_book_file_size_max)
                        )
                        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
                        dialog.show()
                    }
                } catch (e: URISyntaxException) {
                    e.printStackTrace()
                }
            }
        }
    }

    interface OnReplyListener {
        fun onReplyListener(content: String?, multipartBody: List<MultipartBody.Part>?)
    }
}