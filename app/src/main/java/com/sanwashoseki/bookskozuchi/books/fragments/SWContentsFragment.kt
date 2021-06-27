package com.sanwashoseki.bookskozuchi.books.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.books.adapters.SWContentsAdapter
import com.sanwashoseki.bookskozuchi.books.models.SWBookmarksResponse
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.navigation.SWBookNavigationActivity
import com.sanwashoseki.bookskozuchi.databinding.FragmentContentBinding
import org.greenrobot.eventbus.EventBus

class SWContentsFragment : SWBaseFragment(),
    SWContentsAdapter.OnItemClickListener {

    private var binding: FragmentContentBinding? = null
    private lateinit var adapter: SWContentsAdapter

    private var bookmarksData: ArrayList<SWBookmarksResponse.Data>? = null

    companion object {
        @JvmStatic
        fun newInstance(bookmarks: ArrayList<SWBookmarksResponse.Data>) =
            SWContentsFragment().apply {
                arguments = Bundle().apply {
                    bookmarksData = bookmarks
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_content, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = SWContentsAdapter(bookmarksData ?: arrayListOf())
        binding?.let { it ->
            it.rcContent.layoutManager = LinearLayoutManager(context)
            it.rcContent.itemAnimator = DefaultItemAnimator()
            it.rcContent.adapter = adapter
        }
        adapter.setOnCallBackListener(this)
    }

    override fun onMenuClickedListener(id: Int?, position: Int?, view: View) {

    }

    override fun onItemClickedListener(bookMark: SWBookmarksResponse.Data?) {
        bookMark?.let {
            EventBus.getDefault().post(SWBookNavigationActivity.SWBookMarkEvent(it))
        }
        activity?.finish()
    }
}