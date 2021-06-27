package com.sanwashoseki.bookskozuchi.business.ebookrequest.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.business.ebookrequest.adapters.SWImageRequestBookAdapter
import com.sanwashoseki.bookskozuchi.business.ebookrequest.adapters.SWRequestBookContentAdapter
import com.sanwashoseki.bookskozuchi.business.ebookrequest.dialogs.SWDialogReplyRequest
import com.sanwashoseki.bookskozuchi.business.ebookrequest.models.responses.SWRequestBookContentLibraryResponse
import com.sanwashoseki.bookskozuchi.business.ebookrequest.services.SWRequestBookContentInterface
import com.sanwashoseki.bookskozuchi.business.ebookrequest.services.SWRequestBookContentPresenter
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWAddShoppingCartWishListResponse
import com.sanwashoseki.bookskozuchi.databinding.FragmentRequestBookContentBinding
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.recycle_item_request_book.view.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

class SWRequestBookContentFragment : SWBaseFragment(), SWRequestBookContentInterface,
    View.OnClickListener, SWDialogReplyRequest.OnReplyListener, SWBaseFragment.OnConfirmListener {

    private var binding: FragmentRequestBookContentBinding? = null
    private var presenter: SWRequestBookContentPresenter? = null

    private var adapterReply: SWRequestBookContentAdapter? = null
    private var adapterImage: SWImageRequestBookAdapter? = null

    private var idRequest: Int? = null
    private var userName: String? = null
    private var isMore: Boolean? = null

    private var dialog: SWDialogReplyRequest? = null

    companion object {
        @JvmStatic
        fun newInstance(id: Int?, more: Boolean?) =
            SWRequestBookContentFragment().apply {
                arguments = Bundle().apply {
                    idRequest = id
                    isMore = more
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_request_book_content,
            container,
            false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        (activity as SWMainActivity).hideBottomNavigationMenu(true)
        (activity as SWMainActivity).setHeaderInChildrenScreen(getString(R.string.request_reply_content),
            isSearch = false,
            isFilter = false)

        presenter?.getRequest(context, idRequest)
        binding?.let { it ->
            it.btnDelete.setOnClickListener(this)
            it.btnReply.setOnClickListener(this)
        }
    }

    private fun getReply() {
        binding?.let { it ->
            it.rcReply.layoutManager = LinearLayoutManager(context)
            it.rcReply.itemAnimator = DefaultItemAnimator()
            it.rcReply.adapter = adapterReply
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWRequestBookContentPresenter()
        presenter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun getRequestContentSuccess(result: SWRequestBookContentLibraryResponse) {
        binding?.let { it ->
            it.tvUserName.text = result.data?.customer?.nameFormated
            it.tvUserEmail.text = result.data?.customer?.email
            it.tvDate.text = result.data?.createdOnUtcFormated
            it.tvRequestTitle.text = result.data?.name
            it.tvAuthor.text = result.data?.authorName
            it.tvContent.text = result.data?.body
            it.tvNumRep.text = result.data?.numPosts.toString()
            it.tvStatus.text = result.data?.status

            Log.d("TAG", "getRequestContentSuccess: $isMore  ${result.data?.statusId}")
            if (isMore == true && (result.data?.statusId == 0 || result.data?.statusId == 2)) {
                it.btnDelete.visibility = View.VISIBLE
            }

            when(result.data?.statusId) {
                0 -> it.tvStatus.setBackgroundResource(R.drawable.bg_text_view_status_pending)
                1 -> it.tvStatus.setBackgroundResource(R.drawable.bg_text_view_status_approved)
                2 -> it.tvStatus.setBackgroundResource(R.drawable.bg_text_view_status_rejected)
                3 -> it.tvStatus.setBackgroundResource(R.drawable.bg_text_view_status_updated)
            }

            if (result.data?.statusId == 0 || result.data?.statusId == 2) {
                it.btnReply.visibility = View.GONE
            }

            if (result.data?.authorName == null || result.data?.authorName == "") {
                it.tvAuthor.visibility = View.GONE
            }

            if (result.data?.body == null || result.data?.body == "") {
                it.tvContent.visibility = View.GONE
            }

            val urls = ArrayList<String>()
            for (i in result.data?.pictures!!.indices) {
                urls.add(result.data?.pictures!![i].imageUrl.toString())
            }
            adapterImage = SWImageRequestBookAdapter(urls)
            val manager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            it.rcImage.layoutManager = manager
            it.rcImage.itemAnimator = DefaultItemAnimator()
            it.rcImage.adapter = adapterImage

            Glide.get(requireContext()).clearMemory()
            if (result.data?.customer?.avatarUrl != "") {
                Glide.with(requireContext())
                    .load(result.data?.customer?.avatarUrl)
                    .into(it.imgAvatar)
            } else {
                Glide.with(requireContext())
                    .load(R.drawable.ic_avatar_not_login)
                    .into(it.imgAvatar)
            }
        }

        userName = result.data?.customer?.nameFormated
        adapterReply = SWRequestBookContentAdapter(result.data?.bookRequestPosts)
        getReply()
    }

    override fun replyRequestSuccess(result: SWAddShoppingCartWishListResponse) {
        presenter?.getRequest(context, idRequest)
        dialog?.dismiss()
    }

    override fun deleteRequestSuccess(result: SWAddShoppingCartWishListResponse) {
        (activity as SWMainActivity).onBackPressed()
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun showIndicator() {
        showLoading(true,
            getString(R.string.request_reply_content),
            getString(R.string.dialog_loading_content))
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

    override fun onReplyListener(content: String?, multipartBody: List<MultipartBody.Part>?) {
        val postContent = RequestBody.create(MultipartBody.FORM, content.toString())
        val id = RequestBody.create(MultipartBody.FORM, idRequest.toString())
        presenter?.replyRequest(context, id, postContent, multipartBody as ArrayList<MultipartBody.Part>)
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btnReply -> {
                dialog = SWDialogReplyRequest(userName)
                dialog?.isCancelable = true
                fragmentManager?.let { dialog?.show(it, "") }

                dialog?.onCallBacReply(this)
            }
            R.id.btnDelete -> {
                showDialogConfirm(getString(R.string.request_book_remove_confirmation), false)
                onCallBackConfirm(this)
            }
        }
    }

    override fun onConfirmListener() {
        presenter?.deleteRequest(context, idRequest)
    }

}