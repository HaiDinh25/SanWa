package com.sanwashoseki.bookskozuchi.business.filter.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.filter.models.responses.SWPublisherResponse
import kotlinx.android.synthetic.main.recycle_item_filter_sub_category.view.*

@Suppress("UNREACHABLE_CODE")
class SWPublisherAdapter(private var publishers: List<SWPublisherResponse.Data?>?, private var selecteds: ArrayList<SWPublisherResponse.Data?>?) :
    RecyclerView.Adapter<SWPublisherAdapter.ViewHolder>() {

    private var listener: OnItemCheckedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SWPublisherAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycle_item_filter_sub_category, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return publishers!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binItems(publishers?.get(position), position)
        holder.itemView.width
        holder.itemView.checkBox.setOnClickListener {
            if (holder.itemView.checkBox.isChecked) {
                listener?.onCheckedItemListener(publishers?.get(position))
            } else {
                listener?.onUnCheckedItemListener(publishers?.get(position))
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(item: SWPublisherResponse.Data?, position: Int) {
            itemView.tvCategory.text = item?.vendorName

            if (selecteds != null) {
                for (i in 0 until selecteds!!.size) {
                    if (selecteds!![i]?.id == item?.id) {
                        itemView.checkBox.isChecked = true
                        listener?.onCheckedItemListener(publishers?.get(position))
                    }
                }
            }
        }
    }

    fun onCallBackCheckedItem(listener: OnItemCheckedListener) {
        this.listener = listener
    }

    interface OnItemCheckedListener {
        fun onCheckedItemListener(publisher: SWPublisherResponse.Data?)

        fun onUnCheckedItemListener(publisher: SWPublisherResponse.Data?)
    }

}