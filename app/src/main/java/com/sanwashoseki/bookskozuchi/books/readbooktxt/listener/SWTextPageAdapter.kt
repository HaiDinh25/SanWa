package com.sanwashoseki.bookskozuchi.books.readbooktxt.listener

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.pxTosp
import com.sanwashoseki.bookskozuchi.books.readbooktxt.views.SWPageView
import com.sanwashoseki.bookskozuchi.books.readbooktxt.views.VTextView

class SWTextPageAdapter(private var vTextView: VTextView, val clickListener: OnClickListener) :
    RecyclerView.Adapter<SWTextPageAdapter.ViewHolder>() {

    interface OnClickListener {
        fun onClickedItem(vTextView: VTextView, pageIndex: Int)
        fun onDisplayedItem(vTextView: VTextView, pageIndex: Int)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val pageView: SWPageView = view.findViewById(R.id.pageView)
        private val bookListenerProgressTextView: TextView = view.findViewById(R.id.bookListenerProgressTextView)

        init {

        }

        @SuppressLint("SetTextI18n")
        fun bindData(vTextView: VTextView, pageIndex: Int) {
            pageView.setData(vTextView, pageIndex)
            bookListenerProgressTextView.setTextColor(vTextView.rubyStyle.paint.color)
            bookListenerProgressTextView.typeface = vTextView.rubyStyle.paint.typeface
            bookListenerProgressTextView.textSize = (vTextView.rubyStyle.paint.textSize).toInt().pxTosp.toFloat()
            bookListenerProgressTextView.text = "${pageIndex + 1}/${vTextView.totalPage}"
        }

    }

    fun updateData(vTextView: VTextView) {
        this.vTextView = vTextView
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_text_page, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pageIndex = if (vTextView.isVertical) vTextView.totalPage - 1 - position else position
        holder.itemView.setOnClickListener {
            clickListener.onClickedItem(vTextView, pageIndex)
        }
        holder.bindData(vTextView, pageIndex)
        clickListener.onDisplayedItem(vTextView, pageIndex)
    }

    override fun getItemCount(): Int {
        return vTextView.totalPage
    }
}