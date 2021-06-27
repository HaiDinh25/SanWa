package com.sanwashoseki.bookskozuchi.business.ebooklibrary.views

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
import com.sanwashoseki.bookskozuchi.business.ebooklibrary.adapters.SWWishListAdapter
import com.sanwashoseki.bookskozuchi.business.ebooklibrary.services.SWWishListInterface
import com.sanwashoseki.bookskozuchi.business.ebooklibrary.services.SWWishListPresenter
import com.sanwashoseki.bookskozuchi.business.ebookstore.views.SWBookDetailFragment
import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.response.SWGetShoppingCartResponse
import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.response.SWRemoveShoppingCartResponse
import com.sanwashoseki.bookskozuchi.databinding.FragmentWishListBinding
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

class SWWishListFragment : SWBaseFragment(), SWWishListAdapter.OnItemClickListener,
    SWWishListInterface, SWBaseFragment.OnConfirmListener, View.OnClickListener {

    private var binding: FragmentWishListBinding? = null
    private var adapter: SWWishListAdapter? = null
    private var presenter: SWWishListPresenter? = null
    private var wishList: SWGetShoppingCartResponse.Data? = null

    private var idWishList: Int? = null

    private var skeletonScreen: SkeletonScreen? = null
    private var isSwipe = false

    companion object {
        @JvmStatic
        fun newInstance() =
            SWWishListFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wish_list, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        presenter?.getWishList(context)
        binding?.let { it ->
            it.edtSearchContent.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    val str = it.edtSearchContent.text.toString()
                    if (NetworkUtil.isNetworkConnected(context)) {
                        searchResult(str)
                    }
                }
            })
            it.swipeRefresh.setOnRefreshListener {
                isSwipe = true
                it.edtSearchContent.text.clear()
                presenter?.getWishList(context)
            }
            it.btnRemoveContent.setOnClickListener(this)
        }
    }

    private fun getWishList() {
        binding?.rcWishList?.layoutManager = LinearLayoutManager(context)
        binding?.rcWishList?.itemAnimator = DefaultItemAnimator()
        binding?.rcWishList?.adapter = adapter

        adapter?.setOnCallBackListener(this)
    }

    private fun searchResult(key: String?) {
        val list = ArrayList<SWGetShoppingCartResponse.Data.Item>()
        if (wishList!= null) {
            if (wishList?.items!!.isNotEmpty()) {
                for (i in wishList?.items!!.indices) {
                    if (wishList?.items!![i].bookName?.toLowerCase(Locale.ROOT)!!.contains(key.toString().toLowerCase(Locale.ROOT))
                        || wishList?.items!![i].authorName?.toLowerCase(Locale.ROOT)!!.contains(key.toString().toLowerCase(Locale.ROOT))) {
                        list.add(wishList?.items!![i])
                    }
                }
            }
        }
        binding?.let { it ->
            if (list.isEmpty()) {
                it.tvNull.visibility = View.VISIBLE
                it.rcWishList.visibility = View.GONE
            } else {
                it.tvNull.visibility = View.GONE
                it.rcWishList.visibility = View.VISIBLE
                adapter = SWWishListAdapter(list)
                getWishList()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWWishListPresenter()
        presenter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    override fun onDetailListener(id: Int?) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.container, SWBookDetailFragment.newInstance(id, false))
            ?.addToBackStack(null)?.commit()
    }

    override fun onUnWishListListener(id: Int?) {
        if (NetworkUtil.isNetworkConnected(context)) {
            idWishList = id
            showDialogConfirm(getString(R.string.dialog_confirm_delete_wish_list), false)
            onCallBackConfirm(this)
        } else {
            showLoading(
                false,
                getString(R.string.dialog_error_network_title),
                getString(R.string.dialog_error_network_content)
            )
        }
    }

    override fun getWishListSuccess(result: SWGetShoppingCartResponse) {
        wishList = result.data
        binding?.let { it ->
            if (isSwipe) {
                it.swipeRefresh.isRefreshing = false
            }
            if (result.data?.items!!.isEmpty()) {
                it.layoutEmpty.visibility = View.VISIBLE
            }
            adapter = SWWishListAdapter(result.data?.items)
            getWishList()

            if (it.edtSearchContent.text != null) {
                searchResult(it.edtSearchContent.text.toString())
            }
        }
        SWBookCacheManager.cacheBookHome(result.toString(), Const.FOLDER_CACHE, Const.FILE_WISH_LIST)
    }

    override fun unWishListSuccess(result: SWRemoveShoppingCartResponse) {
        presenter?.getWishList(context)
        getWishList()
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun showIndicator() {
        if (!isSwipe) {
            binding?.rcWishList?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            skeletonScreen = Skeleton.bind(binding?.rcWishList)
                .adapter(adapter)
                .shimmer(true)
                .angle(20)
                .frozen(false)
                .duration(1200)
                .count(10)
                .load(R.layout.skeleton_recycle_item_top_seller_books)
                .show()
        }

//        showLoading(true, getString(R.string.library_tab_wish_list), getString(R.string.dialog_loading_content))
    }

    override fun hideIndicator() {
        hideLoading()
    }

    override fun showNetworkError() {
        val fMyBook = File(SWApplication.cacheDir, "${Const.FOLDER_SANWA}/${Const.FOLDER_CACHE}/${Const.FILE_WISH_LIST}")
        if (fMyBook.exists()) {
            val wishListModel: SWGetShoppingCartResponse = Gson().fromJson(SWBookCacheManager.readFileCache(fMyBook), SWGetShoppingCartResponse::class.java)
            adapter = SWWishListAdapter(wishListModel.data?.items)
            getWishList()
        }
        binding?.let { it ->
            it.swipeRefresh.isEnabled = false
            it.layoutSearch.visibility = View.GONE
        }
    }

    override fun onConfirmListener() {
        presenter?.unWishList(context, idWishList)
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btnRemoveContent -> binding?.edtSearchContent?.text?.clear()
        }
    }

}