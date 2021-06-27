package com.sanwashoseki.bookskozuchi.books.fragments

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
import com.sanwashoseki.bookskozuchi.books.models.SWBookmarksResponse
import com.sanwashoseki.bookskozuchi.books.services.SWBookmarksInterface
import com.sanwashoseki.bookskozuchi.books.services.SWBookmarksPresenter
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.navigation.SWBookNavigationActivity
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.navigation.SWBookmarkMenuPopup
import com.sanwashoseki.bookskozuchi.books.adapters.SWBookmarksAdapter
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.utils.SWCommonResponse
import com.sanwashoseki.bookskozuchi.databinding.FragmentBookmarkBinding
import org.greenrobot.eventbus.EventBus

class SWBookmarksFragment : SWBaseFragment(), SWBookmarksInterface,
    SWBookmarksAdapter.OnItemClickListener {

    private var binding: FragmentBookmarkBinding? = null
    private var presenter: SWBookmarksPresenter? = null
    private lateinit var adapter: SWBookmarksAdapter

    private var delPos: Int? = null
    private var productId: Int? = null

    companion object {
        @JvmStatic
        fun newInstance(id: Int?) =
            SWBookmarksFragment().apply {
                arguments = Bundle().apply {
                    productId = id
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bookmark, container, false)
        return binding?.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWBookmarksPresenter()
        presenter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    private var page: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter?.getBookmarks(productId, page, SWConstants.LIMIT)
    }

    override fun getBookMarkSuccess(result: SWBookmarksResponse) {
        Log.d("TAG", "getBookMarkSuccess: $result")
        adapter = SWBookmarksAdapter(result.data as ArrayList<SWBookmarksResponse.Data>?)
        binding?.let { it ->
            it.rcBookmark.layoutManager = LinearLayoutManager(context)
            it.rcBookmark.itemAnimator = DefaultItemAnimator()
            it.rcBookmark.adapter = adapter
        }
        adapter.setOnCallBackListener(this)
    }

    override fun deleteBookMarkSuccess(result: SWCommonResponse<Any>) {
        delPos?.let { adapter.deleteItem(it) }
        adapter.notifyDataSetChanged()
    }

    override fun showMessageError(msg: String) {
        TODO("Not yet implemented")
    }

    override fun showIndicator() {
        showLoading(true, "", "")
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

    override fun onMenuClickedListener(id: Int?, position: Int?, view: View) {
        SWBookmarkMenuPopup(requireContext(), view, object : SWBookmarkMenuPopup.Callback {
            override fun onDeleteClicked() {
                delPos = position
                presenter?.deleteBookmarks(id)
            }
        })
    }

    override fun onItemClickedListener(bookMark: SWBookmarksResponse.Data?) {
        bookMark?.let {
            EventBus.getDefault().post(SWBookNavigationActivity.SWBookMarkEvent(it))
        }
        activity?.finish()
    }
}