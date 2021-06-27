package com.sanwashoseki.bookskozuchi.business.filter.views

import android.content.Context
import android.os.Bundle
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
import com.sanwashoseki.bookskozuchi.business.filter.adapters.SWPublisherAdapter
import com.sanwashoseki.bookskozuchi.business.filter.models.requests.SWFilterBookRequest
import com.sanwashoseki.bookskozuchi.business.filter.models.responses.SWPublisherResponse
import com.sanwashoseki.bookskozuchi.business.filter.services.SWPublisherInterface
import com.sanwashoseki.bookskozuchi.business.filter.services.SWPublisherPresenter
import com.sanwashoseki.bookskozuchi.databinding.FragmentPublisherBinding
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import java.util.*
import kotlin.collections.ArrayList


class SWPublisherFragment : SWBaseFragment(), SWPublisherInterface,
    SWPublisherAdapter.OnItemCheckedListener, View.OnClickListener {

    private var binding: FragmentPublisherBinding? = null
    private var presenter: SWPublisherPresenter? = null
    private var adapter: SWPublisherAdapter? = null

    private var publishers = ArrayList<SWPublisherResponse.Data?>()
    private var listSearch: List<SWPublisherResponse.Data?>? = null
    private var filterModel: SWFilterBookRequest? = null

    private var isCategory: Boolean? = null
    private var keyFilter: String? = null

    private var skeletonScreen: SkeletonScreen? = null

    companion object {
        @JvmStatic
        fun newInstance(filter: SWFilterBookRequest?, category: Boolean?, key: String?) =
            SWPublisherFragment().apply {
                arguments = Bundle().apply {
                    isCategory = category
                    keyFilter = key
                    filterModel = filter
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_publisher, container, false)

        initUI()
        return binding?.root
    }

    fun initUI() {
        (activity as SWMainActivity).setHeaderInChildrenScreen(getString(R.string.filter_publisher), isSearch = false, isFilter = false)

        presenter?.getPublisher(context)

        binding?.let { it ->
            it.btnApply.isEnabled = true
            it.btnApply.setBackgroundResource(R.drawable.bg_button_authentication)

            it.edtSearchContent.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    adapter = SWPublisherAdapter(search(it.edtSearchContent.text.toString()), filterModel?.publishers)
                    getPublisher()
                }
            })
            it.btnRemoveContent.setOnClickListener(this)
            it.btnApply.setOnClickListener(this)
        }
    }

    private fun search(key: String?) : List<SWPublisherResponse.Data?> {
        val list = ArrayList<SWPublisherResponse.Data?>()
        if (listSearch!!.isNotEmpty()) {
            for (i in listSearch!!.indices) {
                if (listSearch!![i]?.vendorName!!.toLowerCase(Locale.ROOT).contains(key.toString().toLowerCase(Locale.ROOT))) {
                    list.add(listSearch!![i])
                }
            }
        }
        return list
    }

    private fun getPublisher() {
        binding?.let { it ->
            it.rcPublisher.layoutManager = LinearLayoutManager(context)
            it.rcPublisher.itemAnimator = DefaultItemAnimator()
            it.rcPublisher.adapter = adapter

            adapter?.onCallBackCheckedItem(this)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWPublisherPresenter()
        presenter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    override fun onBackPressed(): Boolean {
        val frm = SWFilterBookFragment.newInstance(isCategory!!, keyFilter, filterModel)
        val args = Bundle()
        frm.arguments = args
        replaceFragment(frm, R.id.container, true, null)

        return true
    }

    override fun getPublisherSuccess(result: SWPublisherResponse) {
        listSearch = result.data
        adapter = SWPublisherAdapter(result.data, filterModel?.publishers)
        getPublisher()
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun validButton(isValid: Boolean) {
        if (isValid) {
            binding?.btnApply?.isEnabled = true
            binding?.btnApply?.setBackgroundResource(R.drawable.bg_button_authentication)
        } else {
            binding?.btnApply?.isEnabled = false
            binding?.btnApply?.setBackgroundResource(R.drawable.bg_button_inactive)
        }
    }

    override fun showIndicator() {
        binding?.rcPublisher?.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,
            false)
        skeletonScreen = Skeleton.bind(binding?.rcPublisher)
            .adapter(adapter)
            .shimmer(true)
            .angle(20)
            .frozen(false)
            .duration(1200)
            .count(10)
            .load(R.layout.skeleton_recycle_item_filter_sub_category)
            .show()

//        showLoading(true, getString(R.string.filter_publisher), getString(R.string.dialog_loading_content))
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

    override fun onCheckedItemListener(publisher: SWPublisherResponse.Data?) {
        if (!publishers.contains(publisher)) {
            publishers.add(publisher)
//            presenter?.validButton(publishers.size)
        }
    }

    override fun onUnCheckedItemListener(publisher: SWPublisherResponse.Data?) {
        for (i in 0 until publishers.size) {
            if (publishers[i] == publisher) {
                publishers.removeAt(i)
                break
            }
        }
        filterModel = SWFilterBookRequest(filterModel?.categoryIds, filterModel?.bookType, filterModel?.priceMin, filterModel?.priceMax, publishers)
//        presenter?.validButton(publishers.size)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnApply -> {
                filterModel = SWFilterBookRequest(filterModel?.categoryIds, filterModel?.bookType, filterModel?.priceMin, filterModel?.priceMax, publishers)
                onBackPressed()
            }
            R.id.btnRemoveContent -> binding?.edtSearchContent?.text?.clear()
        }
    }
}