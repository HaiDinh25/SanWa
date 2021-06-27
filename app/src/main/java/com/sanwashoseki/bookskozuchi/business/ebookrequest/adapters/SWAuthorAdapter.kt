package com.sanwashoseki.bookskozuchi.business.ebookrequest.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWBookDetailResponse
import kotlinx.android.synthetic.main.recycle_item_author.view.*

@Suppress("UNREACHABLE_CODE")
class SWAuthorAdapter(
    private var authors: List<SWBookDetailResponse.Data.Authors>?,
) : RecyclerView.Adapter<SWAuthorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycle_item_author, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (authors != null) {
            authors!!.size
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val holderBookStore: ViewHolder = holder
        holderBookStore.binItems(authors!![position])
        holder.itemView.width
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(item: SWBookDetailResponse.Data.Authors?) {
            itemView.tvName.text = item?.name
        }
    }
}