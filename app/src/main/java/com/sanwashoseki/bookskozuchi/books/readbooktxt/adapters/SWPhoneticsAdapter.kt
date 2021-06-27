package com.sanwashoseki.bookskozuchi.books.readbooktxt.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWListItemType
import com.sanwashoseki.bookskozuchi.databinding.ItemLoadingBinding
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.models.SWPhoneticModel
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.utils.OnLoadMoreListener
import com.sanwashoseki.bookskozuchi.databinding.ItemAdjustTextReadingBinding
import kotlinx.android.synthetic.main.item_adjust_text_reading.view.*

class SWPhoneticsAdapter(
    var list: MutableList<SWPhoneticModel?>,
    recyclerView: RecyclerView,
    private val loadMore: OnLoadMoreListener,
    private val callback: Callback,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var isLoading = false
    private var lastVisibleItem = 0
    private var totalItemCount = 0
    private val visibleThreshold = 5

    interface Callback {

        fun onItemClicked(dic: SWPhoneticModel)

        fun onVoidOriginalClicked(text: String)

        fun onVoidFurigana(text: String)

        fun onScrolled(pos: Int)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == SWListItemType.LOADED.rawValue) {
            val inflater = LayoutInflater.from(parent.context)
            val binding: ItemAdjustTextReadingBinding =
                DataBindingUtil.inflate(inflater, R.layout.item_adjust_text_reading, parent, false)
            RecyclerViewHolder(binding)
        } else {
            val inflater = LayoutInflater.from(parent.context)
            val binding: ItemLoadingBinding =
                DataBindingUtil.inflate(inflater, R.layout.item_loading, parent, false)
            DraftRecyclerViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RecyclerViewHolder) {
            val item = list[position]
            val view = holder.itemView

            holder.binding.data = item
//
//            view.bt_menu.setOnClickListener {
//                callback.onMenuClicked(position, it)
//            }

            view.setOnClickListener {
                item?.let {
                    callback.onItemClicked(item)
                }
            }

            view.btnVoiceOriginal.setOnClickListener {
                item?.vocabulary?.let { it1 -> callback.onVoidOriginalClicked(it1) }
            }

            view.btnVoiceFurigana.setOnClickListener {
                item?.pronounce?.let { it1 -> callback.onVoidOriginalClicked(it1) }
            }
        }
    }

    override fun getItemCount() = list.size

    override fun getItemViewType(position: Int) =
        if (list[position] == null) SWListItemType.LOADING.rawValue else SWListItemType.LOADED.rawValue

    fun setLoaded() {
        isLoading = false
    }

    inner class RecyclerViewHolder(val binding: ItemAdjustTextReadingBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class DraftRecyclerViewHolder(binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root)

    init {
        recyclerView.setOnScrollChangeListener { _, _, _, _, _ ->
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            if (list.size > 0) {
                totalItemCount = list.size
                lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val pos = layoutManager.findFirstVisibleItemPosition()
                callback.onScrolled(pos)
                if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
                    isLoading = true
                    loadMore.onLoadMore()
                }
            }
        }
    }

}
