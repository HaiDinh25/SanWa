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
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.business.ebooklibrary.adapters.SWMyBookAdapter
import com.sanwashoseki.bookskozuchi.business.ebooklibrary.models.responses.SWMyBookResponse
import com.sanwashoseki.bookskozuchi.business.ebooklibrary.services.SWMyBookInterface
import com.sanwashoseki.bookskozuchi.business.ebooklibrary.services.SWMyBookPresenter
import com.sanwashoseki.bookskozuchi.business.ebookstore.views.SWBookDetailFragment
import com.sanwashoseki.bookskozuchi.databinding.FragmentMyBookBinding
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

class SWMyBookFragment : SWBaseFragment(), SWMyBookAdapter.OnItemClickListener, SWMyBookInterface,
    View.OnClickListener {

    private var binding: FragmentMyBookBinding? = null
    private var adapter: SWMyBookAdapter? = null
    private var presenter: SWMyBookPresenter? = null

    private var myBooks: List<SWMyBookResponse.Data>? = null

    private var skeletonScreen: SkeletonScreen? = null
    private var isSwipe = false

    companion object {
        @JvmStatic
        fun newInstance() =
            SWMyBookFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_book, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        presenter?.getMyBook(context)
        binding?.let { it ->
            it.edtSearchContent.addTextChangedListener(object : TextWatcher {
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
                isSwipe =true
                it.edtSearchContent.text.clear()
                presenter?.getMyBook(context)
            }
            it.btnFindBook.setOnClickListener(this)
            it.btnRemoveContent.setOnClickListener(this)
        }

        binding?.rcMyBook?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val currentItem = getCurrentItem()
                if (currentItem  == (adapter?.itemCount?.minus( 8))) {
                    presenter?.loadMore(context)
                }
            }
        })
    }

    private fun getCurrentItem(): Int {
        return (binding?.rcMyBook?.getLayoutManager() as LinearLayoutManager)
            .findFirstVisibleItemPosition()
    }

    private fun getMyBook() {
        binding?.let { it ->
            it.rcMyBook.layoutManager = LinearLayoutManager(context)
            it.rcMyBook.itemAnimator = DefaultItemAnimator()
            it.rcMyBook.adapter = adapter
        }
        adapter?.setOnCallBackListener(this)
    }

    private fun searchResult(key: String?) {
        val list = ArrayList<SWMyBookResponse.Data>()
        if (myBooks != null) {
            if (myBooks!!.isNotEmpty()) {
                for (i in myBooks!!.indices) {
                    if (myBooks!![i].name!!.toLowerCase(Locale.ROOT).contains(key.toString().toLowerCase(Locale.ROOT))) {
                        list.add(myBooks!![i])
                    }
                    for (j in myBooks!![i].authors!!.indices) {
                        if (myBooks!![i].authors!![j].name!!.toLowerCase(Locale.ROOT).contains(key.toString().toLowerCase(Locale.ROOT))) {
                            if (!list.contains(myBooks!![i]))
                            list.add(myBooks!![i])
                        }
                    }
                }
            }
        }
        binding?.let { it ->
            if (list.isEmpty()) {
                it.tvNull.visibility = View.VISIBLE
                it.rcMyBook.visibility = View.GONE
            } else {
                it.tvNull.visibility = View.GONE
                it.rcMyBook.visibility = View.VISIBLE
                adapter = SWMyBookAdapter(list)
                getMyBook()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWMyBookPresenter()
        presenter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onDetailListener(id: Int?) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.container, SWBookDetailFragment.newInstance(id, false))
            ?.addToBackStack(null)?.commit()
    }

    override fun getMyBookSuccess(result: SWMyBookResponse) {
        myBooks = result.data
        binding?.let { it ->
            if (isSwipe) {
                it.swipeRefresh.isRefreshing = false
            }
            if (result.data?.size == 0) {
                it.layoutEmpty.visibility = View.VISIBLE
                it.layoutData.visibility = View.GONE
            } else {
                it.layoutEmpty.visibility = View.GONE
                it.layoutData.visibility = View.VISIBLE
            }
        }
        if (result.data != null) {
            adapter = SWMyBookAdapter(result.data)
            getMyBook()
        } else {
            skeletonScreen?.hide()
        }
        SWBookCacheManager.cacheBookHome(result.toString(), Const.FOLDER_CACHE, Const.FILE_MY_BOOK)
    }

    override fun loadMoreSuccess(result: SWMyBookResponse) {
        result.data?.let { adapter?.addMoreBooks(it) }
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun showIndicator() {
        if (!isSwipe) {
            binding?.rcMyBook?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            skeletonScreen = Skeleton.bind(binding?.rcMyBook)
                .adapter(adapter)
                .shimmer(true)
                .angle(20)
                .frozen(false)
                .duration(1200)
                .count(10)
                .load(R.layout.skeleton_recycle_item_top_seller_books)
                .show()
        }
//        showLoading(true, getString(R.string.library_tab_my_book), getString(R.string.dialog_loading_content))
    }

    override fun hideIndicator() {
        hideLoading()
    }

    override fun showNetworkError() {
//        showLoading(
//            false,
//            getString(R.string.dialog_error_network_title),
//            getString(R.string.dialog_error_network_content)
//        )
        val fMyBook = File(SWApplication.cacheDir, "${Const.FOLDER_SANWA}/${Const.FOLDER_CACHE}/${Const.FILE_MY_BOOK}")
        if (fMyBook.exists()) {
            val myBookModel: SWMyBookResponse = Gson().fromJson(SWBookCacheManager.readFileCache(fMyBook), SWMyBookResponse::class.java)
            adapter = SWMyBookAdapter(myBookModel.data)
            getMyBook()
        }
        binding?.let { it ->
            it.swipeRefresh.isEnabled = false
            it.layoutSearch.visibility = View.GONE
        }
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btnFindBook -> {
                (activity as SWMainActivity).replaceActivity(SWMainActivity::class.java)
                (activity as SWMainActivity).finish()
            }
            R.id.btnRemoveContent -> binding?.edtSearchContent?.text?.clear()
        }
    }

}