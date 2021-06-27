package com.sanwashoseki.bookskozuchi.books.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.books.models.SWBookmarksResponse
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.utils.DateTimeUtils
import kotlinx.android.synthetic.main.item_bookmark_view.view.*

class SWBookmarksAdapter(var list: ArrayList<SWBookmarksResponse.Data>?) : RecyclerView.Adapter<SWBookmarksAdapter.ViewHolder>() {

    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bookmark_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binItems(list!!.get(position), position)
        holder.itemView.width
    }

    fun setOnCallBackListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onMenuClickedListener(id: Int?, position: Int?, view: View)

        fun onItemClickedListener(bookMark: SWBookmarksResponse.Data?)
    }

    fun deleteItem(position: Int) {
        list!!.removeAt(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(item: SWBookmarksResponse.Data?, position: Int?) {
            itemView.tvBookmarkPage.text = itemView.context.getString(R.string.bookmark_page) + "${(item?.pageIndex ?: 0) + 1}"
            itemView.tvContent.text = item?.note
            itemView.tvDateTime.text = DateTimeUtils.getCreatedFormat(item?.createdDate.toString())

            itemView.btnMenu?.setOnClickListener { listener?.onMenuClickedListener(item?.id, position, it) }

            itemView.setOnClickListener { listener?.onItemClickedListener(item) }
        }
    }

}
