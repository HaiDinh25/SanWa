package com.sanwashoseki.bookskozuchi.books.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.books.models.SWBookmarksResponse
import kotlinx.android.synthetic.main.item_bookmark_view.view.*

class SWContentsAdapter(var list: ArrayList<SWBookmarksResponse.Data>) :
    RecyclerView.Adapter<SWContentsAdapter.ViewHolder>() {

    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SWContentsAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_bookmark_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SWContentsAdapter.ViewHolder, position: Int) {
        holder.binItems(list[position], position)
        holder.itemView.width
    }

    fun setOnCallBackListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onMenuClickedListener(id: Int?, position: Int?, view: View)

        fun onItemClickedListener(bookMark: SWBookmarksResponse.Data?)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.btnMenu.visibility = View.GONE
        }

        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(item: SWBookmarksResponse.Data, position: Int?) {
            var title = itemView.context.getString(R.string.bookmark_page) + "${(item.pageIndex ?: 0) + 1}"
            item.chapterIndex?.let {
                title = itemView.context.getString(R.string.bookmark_chapter) + (it + 1) + title
            }
            itemView.tvBookmarkPage.text = title
            itemView.tvContent.text = item.note
            itemView.tvDateTime.text = null
            itemView.setOnClickListener { listener?.onItemClickedListener(item) }
        }
    }
}