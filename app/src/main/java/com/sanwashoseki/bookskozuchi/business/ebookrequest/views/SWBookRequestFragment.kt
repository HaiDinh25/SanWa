package com.sanwashoseki.bookskozuchi.business.ebookrequest.views

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.business.ebookrequest.adapters.SWRequestBookAdapter
import com.sanwashoseki.bookskozuchi.business.ebookrequest.models.responses.SWRequestBookLibraryResponse
import com.sanwashoseki.bookskozuchi.business.ebookrequest.services.SWRequestBookInterface
import com.sanwashoseki.bookskozuchi.business.ebookrequest.services.SWRequestBookPresenter
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWAddShoppingCartWishListResponse
import com.sanwashoseki.bookskozuchi.databinding.FragmentBookRequestBinding
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.gson.Gson
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.utilities.NetworkUtil
import com.sanwashoseki.bookskozuchi.utilities.SWApplication
import com.sanwashoseki.bookskozuchi.utilities.SWBookCacheManager
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class SWBookRequestFragment : SWBaseFragment(), View.OnClickListener, SWRequestBookInterface,
    SWRequestBookAdapter.OnItemClickListener, SWBaseFragment.OnConfirmListener {

    private var binding: FragmentBookRequestBinding? = null
    private var presenter: SWRequestBookPresenter? = null
    private var adapter: SWRequestBookAdapter? = null

    private var isMore: Boolean? = null
    private var idRequest: Int? = null
    private var requests: SWRequestBookLibraryResponse? = null

    private var skeletonScreen: SkeletonScreen? = null
    private var isSwipe = false

    companion object {
        @JvmStatic
        fun newInstance(more: Boolean?) =
            SWBookRequestFragment().apply {
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
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_book_request,
            container,
            false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        binding?.let { it ->
            if (isMore == false) {
                (activity as SWMainActivity?)?.hideBottomNavigationMenu(false)
                (activity as SWMainActivity).setHeaderInMainMenu(getString(R.string.navigation_request_book),
                    hideButton = true)
                presenter?.getRequest(context)
            } else {
                (activity as SWMainActivity?)?.hideBottomNavigationMenu(true)
                (activity as SWMainActivity).setHeaderInChildrenScreen(getString(R.string.more_my_book_request),
                    isSearch = false,
                    isFilter = false)
                presenter?.getMyRequest(context)
            }
            if (Sharepref.getUserToken(context) == "") {
                it.btnAdd.visibility = View.GONE
            }
            it.swipeRefresh.setOnRefreshListener {
                isSwipe = true
                if (isMore == false) {
                    presenter?.getRequest(context)
                } else {
                    presenter?.getMyRequest(context)
                }
            }

            it.edtSearchContent.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    val str = it.edtSearchContent.text.toString()
                    searchResult(str)
                }
            })

            it.btnAdd.setOnClickListener(this)
            it.btnRemoveContent.setOnClickListener(this)
        }
    }

    private fun getRequest() {
        binding?.let { it ->
            it.rcRequest.layoutManager = LinearLayoutManager(context)
            it.rcRequest.itemAnimator = DefaultItemAnimator()
            it.rcRequest.adapter = adapter
        }
        adapter?.setOnCallBackListener(this)
    }

    private fun searchResult(key: String?) {
        val list = ArrayList<SWRequestBookLibraryResponse.Data>()
        requests?.data?.let { it ->
            if (it.isNotEmpty()) {
                for (i in it.indices) {
                    if (it[i].authorName.toString().toLowerCase(Locale.ROOT).contains(key.toString().toLowerCase(Locale.ROOT))
                        || it[i].name.toString().toLowerCase(Locale.ROOT).contains(key.toString().toLowerCase(Locale.ROOT))) {
                        list.add(it[i])
                    }
                }
            }
        }

        if (list.isEmpty()) {
            binding?.tvNull?.visibility = View.VISIBLE
            binding?.rcRequest?.visibility = View.GONE
        } else {
            binding?.tvNull?.visibility = View.GONE
            binding?.rcRequest?.visibility = View.VISIBLE
            adapter = SWRequestBookAdapter(context, list, isMore)
            getRequest()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWRequestBookPresenter()
        presenter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    override fun onBackPressed(): Boolean {
        if (isMore == false) {
            requireActivity().finish()
        }
        return false
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnAdd -> {
                if (NetworkUtil.isNetworkConnected(context)) {
                    replaceFragment(SWNewRequestBookFragment.newInstance(isMore), R.id.container, false, null)
                } else {
                    showLoading(
                        false,
                        getString(R.string.dialog_error_network_title),
                        getString(R.string.dialog_error_network_content)
                    )
                }
            }
            R.id.btnRemoveContent -> {
                binding?.edtSearchContent?.text?.clear()
            }
        }
    }

    override fun getRequestSuccess(result: SWRequestBookLibraryResponse) {
        binding?.let { it ->
            if (isSwipe) {
                it.swipeRefresh.isRefreshing = false
            }
            if (result.data?.size == 0) {
                it.rcRequest.visibility = View.GONE
                it.tvPlaceHolder.visibility = View.VISIBLE
            } else {
                it.rcRequest.visibility = View.VISIBLE
                it.tvPlaceHolder.visibility = View.GONE
            }
        }
        SWBookCacheManager.cacheBookHome(result.toString(), Const.FOLDER_CACHE, Const.FILE_REQUEST)
        requests = result
        adapter = SWRequestBookAdapter(context, result.data, isMore)
        getRequest()
    }

    override fun getMyRequestSuccess(result: SWRequestBookLibraryResponse) {
        binding?.let { it ->
            if (isSwipe) {
                it.swipeRefresh.isRefreshing = false
            }
            if (result.data?.size == 0) {
                it.rcRequest.visibility = View.GONE
                it.tvPlaceHolder.visibility = View.VISIBLE
            } else {
                it.rcRequest.visibility = View.VISIBLE
                it.tvPlaceHolder.visibility = View.GONE
            }
            it.layoutSearch.visibility = View.VISIBLE
        }
        requests = result
        adapter = SWRequestBookAdapter(context, result.data, isMore)
        getRequest()
    }

    override fun deleteRequestSuccess(result: SWAddShoppingCartWishListResponse) {
        presenter?.getMyRequest(context)
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun showIndicator() {
        if (!isSwipe) {
            binding?.rcRequest?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            skeletonScreen = Skeleton.bind(binding?.rcRequest)
                .adapter(adapter)
                .shimmer(true)
                .angle(20)
                .frozen(false)
                .duration(1200)
                .count(10)
                .load(R.layout.skeleton_recycle_item_request_book)
                .show()
        }

//        showLoading(true, getString(R.string.navigation_request_book), getString(R.string.dialog_loading_content))
    }

    override fun hideIndicator() {
        hideLoading()
    }

    override fun showNetworkError() {
        val fRequest = File(SWApplication.cacheDir, "${Const.FOLDER_SANWA}/${Const.FOLDER_CACHE}/${Const.FILE_REQUEST}")
        if (fRequest.exists()) {
            val requestModel: SWRequestBookLibraryResponse = Gson().fromJson(SWBookCacheManager.readFileCache(fRequest), SWRequestBookLibraryResponse::class.java)
            adapter = SWRequestBookAdapter(context, requestModel.data, false)
            getRequest()
        }
        binding?.let { it ->
            it.swipeRefresh.isEnabled = false
            it.layoutSearch.visibility = View.GONE
        }
    }

    override fun onDetailRequestListener(id: Int?) {
        if (NetworkUtil.isNetworkConnected(context)) {
            if (Sharepref.getUserToken(context) == "") {
                (activity as SWMainActivity).showSignIn()
            } else {
                replaceFragment(SWRequestBookContentFragment.newInstance(id, isMore), R.id.container, false, null)
            }
        } else {
            showLoading(
                false,
                getString(R.string.dialog_error_network_title),
                getString(R.string.dialog_error_network_content)
            )
        }
    }

    override fun onDeleteRequestListener(id: Int?) {
        idRequest = id
        showDialogConfirm(getString(R.string.request_book_remove_confirmation), false)
        onCallBackConfirm(this)
    }

    override fun onConfirmListener() {
        presenter?.deleteRequest(context, idRequest)
    }

}