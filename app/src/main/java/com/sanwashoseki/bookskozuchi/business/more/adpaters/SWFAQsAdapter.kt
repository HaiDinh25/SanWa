package com.sanwashoseki.bookskozuchi.business.more.adpaters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWFAQsResponse
import kotlinx.android.synthetic.main.recycle_item_faqs.view.*

@Suppress("UNREACHABLE_CODE")
class SWFAQsAdapter(private var faqs: List<SWFAQsResponse.Data.Content>?) : RecyclerView.Adapter<SWFAQsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SWFAQsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycle_item_faqs, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return faqs!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val holderBookStore: ViewHolder = holder
        holderBookStore.binItems(faqs!![position], position)
        holder.itemView.width
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(item: SWFAQsResponse.Data.Content, position: Int) {
            itemView.tvTitle.text = item.title
            itemView.tvContent.text = item.question
            if (position == faqs?.size!! - 1) {
                itemView.view.visibility = View.GONE
            }

            itemView.btnShow.setOnClickListener {
                if (itemView.tvContent.isShown) {
                    itemView.btnShow.rotation = ((0).toFloat())
                    itemView.tvContent.visibility = View.GONE
                } else {
                    itemView.btnShow.rotation = ((-180).toFloat())
                    itemView.tvContent.visibility = View.VISIBLE
                }
            }
        }
    }

}