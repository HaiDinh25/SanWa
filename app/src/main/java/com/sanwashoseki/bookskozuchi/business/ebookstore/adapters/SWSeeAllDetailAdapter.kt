package com.sanwashoseki.bookskozuchi.business.ebookstore.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWBookDetailResponse
import kotlinx.android.synthetic.main.recycle_item_info_book_in_detail_book.view.*

@Suppress("UNREACHABLE_CODE")
class SWSeeAllDetailAdapter(private var infos: List<SWBookDetailResponse.Data.ProductInfoBooks>?) :
    RecyclerView.Adapter<SWSeeAllDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycle_item_see_all_detail_book, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return infos!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binItems(infos?.get(position))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(item: SWBookDetailResponse.Data.ProductInfoBooks?) {
            itemView.tvKey.text = item?.key + ":"
            itemView.tvValue.text = item?.value
        }
    }

}