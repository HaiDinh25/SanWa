package com.sanwashoseki.bookskozuchi.business.ebookrequest.views

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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.sanwashoseki.bookskozuchi.BuildConfig
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.business.ebookrequest.adapters.SWImageRequestBookAdapter2
import com.sanwashoseki.bookskozuchi.business.ebookrequest.services.SWAddRequestBookInterface
import com.sanwashoseki.bookskozuchi.business.ebookrequest.services.SWAddRequestBookPresenter
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWAddShoppingCartWishListResponse
import com.sanwashoseki.bookskozuchi.databinding.FragmentNewRequestBookBinding
import com.sanwashoseki.bookskozuchi.utilities.SWPathUtil
import com.sanwashoseki.bookskozuchi.utilities.SWPathUtil.createImageFile
import com.sanwashoseki.bookskozuchi.utilities.SWPathUtil.getRealPathFromURI
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.net.URISyntaxException
import java.text.DecimalFormat

class SWNewRequestBookFragment : SWBaseFragment(), View.OnClickListener, SWAddRequestBookInterface,
    SWImageRequestBookAdapter2.OnRemoveImageListener {

    private val TAG = "SWNewRequestBookFragment"
    private val REQUEST_CODE_STORAGE_PERMISSION = 1000
    private val REQUEST_CODE_CAMERA_PERMISSION = 1001
    private val REQUEST_CODE_GET_IMAGE = 1002
    private val REQUEST_CODE_TAKE_PHOTO = 1003

    private var binding: FragmentNewRequestBookBinding? = null
    private var presenter: SWAddRequestBookPresenter? = null
    private var adapterImage: SWImageRequestBookAdapter2? = null

    private val uris = ArrayList<Uri>()
    private var fileNames = ArrayList<String>()
    private var files = ArrayList<File>()
    private var requestFile: RequestBody? = null
    private val multipartBody = ArrayList<MultipartBody.Part>()
    private var photoPaths = HashMap<Uri, String>()
    private lateinit var currentPhotoPath: String
    private lateinit var currentPhotoURI: Uri
    private var isMore: Boolean? = null

    companion object {
        @JvmStatic
        fun newInstance(more: Boolean?) =
            SWNewRequestBookFragment().apply {
                arguments = Bundle().apply {
                    isMore = more
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_new_request_book,
            container,
            false
        )

        initUI()
        return binding?.root
    }

    private fun initUI() {
        (activity as SWMainActivity).hideBottomNavigationMenu(true)
        (activity as SWMainActivity).setHeaderInChildrenScreen(
            getString(R.string.request_book_title),
            isSearch = false,
            isFilter = false
        )
        binding?.let { it ->
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

            it.edtTitle.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(p0: Editable?) {
                    if (it.edtTitle.text.toString() == "") {
                        it.btnSendRequest.isEnabled = false
                        it.btnSendRequest.setBackgroundResource(R.drawable.bg_button_inactive)
                    } else {
                        it.btnSendRequest.isEnabled = true
                        it.btnSendRequest.setBackgroundResource(R.drawable.bg_button_authentication)
                    }
                }
            })

            it.container.setOnClickListener(this)
            it.btnImage.setOnClickListener(this)
            it.btnSendRequest.setOnClickListener(this)
            it.btnCamera.setOnClickListener(this)
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWAddRequestBookPresenter()
        presenter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.container -> hideSoftKeyBoard(context, binding?.container!!)
            R.id.btnImage -> {
                if (uris.size < 5) {
                    val permissionCheck = ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            REQUEST_CODE_STORAGE_PERMISSION
                        )
                    } else pickImage()
                } else {
                    showMessageError(getString(R.string.request_book_select_max))
                }
            }
            R.id.btnCamera -> if (uris.size < 5) takePhoto()
            R.id.btnSendRequest -> {
                binding?.let { it ->
                    val name = it.edtTitle.text.toString().toRequestBody(MultipartBody.FORM)
                    val author = it.edtAuthor.text.toString()
                        .toRequestBody(MultipartBody.FORM)
                    val content = it.edtContent.text.toString()
                        .toRequestBody(MultipartBody.FORM)
                    if (uris.size != 0) {
                        for (i in 0 until uris.size) {
                            fileNames.add(SWPathUtil.getPath(context, uris[i]).toString())
                            val path =
                                if (getRealPathFromURI(uris[i]) != null) getRealPathFromURI(uris[i]) else photoPaths.get(
                                    uris[i]
                                )
                            Log.d(TAG, "Send file path: " + uris[i].path)
                            files.add(File(path))
                            requestFile =
                                files[i]
                                    .asRequestBody(
                                        requireActivity().contentResolver.getType(
                                            uris[i]
                                        ).toString().toMediaTypeOrNull()
                                    )
                            multipartBody.add(
                                MultipartBody.Part.createFormData(
                                    "files",
                                    files[i].name,
                                    requestFile!!
                                )
                            )
                        }
                    }
                    presenter?.addRequest(context, name, author, content, multipartBody)
                }
            }
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_GET_IMAGE)
    }

    private fun takePhoto() {
        val permissionCheck = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA_PERMISSION)
        } else {
            try {
                context?.let { dispatchTakePictureIntent(it) }
            } catch (e: ActivityNotFoundException) {
                Log.e(TAG, "Take a photo error !",)
            }
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
                    currentPhotoPath = photoFile.absolutePath
                }

                photoFile?.also {
                    currentPhotoURI = FileProvider.getUriForFile(
                        context,
                        BuildConfig.APPLICATION_ID + ".provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoURI)
                    startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PHOTO)
                }
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
                    try {
                        context?.let { dispatchTakePictureIntent(it) }
                    } catch (e: ActivityNotFoundException) {
                        Log.e(TAG, "Take a photo error !",)
                    }
                }
                REQUEST_CODE_STORAGE_PERMISSION -> pickImage()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_GET_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val uri: Uri = data.data!!
                addImageToList(uri)
            }
        } else if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "take photo done")
            galleryAddPic()
            uris.add(currentPhotoURI)
            photoPaths.put(currentPhotoURI, currentPhotoPath)
            binding?.let { it ->
                adapterImage = SWImageRequestBookAdapter2(uris)
                val manager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                it.rcImage.layoutManager = manager
                it.rcImage.itemAnimator = DefaultItemAnimator()
                it.rcImage.adapter = adapterImage

                adapterImage?.onCallBackListener(this)
            }
        }
    }

    //Add the photo to a gallery to make it accessible from the system's Media Provider, user can view it in gallery
    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            requireActivity().sendBroadcast(mediaScanIntent)
        }
    }

    private fun addImageToList(uri: Uri) {
        try {
            var fileSize = 0
            val file = File(getRealPathFromURI(uri))
            val miB = (1024 * 1024).toLong()
            val format = DecimalFormat("#.##")
            fileSize = format.format(file.length() / miB).toInt()
            if (fileSize <= 10) {
                uris.add(uri)
                binding?.let { it ->
                    adapterImage = SWImageRequestBookAdapter2(uris)
                    val manager = LinearLayoutManager(
                        context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    it.rcImage.layoutManager = manager
                    it.rcImage.itemAnimator = DefaultItemAnimator()
                    it.rcImage.adapter = adapterImage

                    adapterImage?.onCallBackListener(this)
                }
            } else {
                showMessageError(getString(R.string.request_book_file_size_max))
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    override fun addRequestSuccess(result: SWAddShoppingCartWishListResponse) {
        replaceFragment(SWBookRequestFragment.newInstance(isMore), R.id.container, true, null)
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun showIndicator() {
        showLoading(
            true,
            "Request",
            getString(R.string.dialog_loading_content)
        )
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

    override fun onRemoveImageListener(uri: Uri?) {
        for (i in 0 until uris.size) {
            if (uris[i] == uri) {
                uris.removeAt(i)
                photoPaths.remove(uri)
                break
            }
        }
        adapterImage?.notifyDataSetChanged()
    }

}