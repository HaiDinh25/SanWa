package com.sanwashoseki.bookskozuchi.business.filter.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWCategoriesResponse
import com.sanwashoseki.bookskozuchi.business.filter.models.requests.SWFilterBookRequest
import kotlinx.android.synthetic.main.recycle_item_filter_sub_category.view.*

@Suppress("UNREACHABLE_CODE")
class SWSubCategoryInFilterAdapter(private var categorys: List<SWCategoriesResponse.Data.SubCategories>?, private var selecteds: SWFilterBookRequest?) :
    RecyclerView.Adapter<SWSubCategoryInFilterAdapter.ViewHolder>() {

    private var listener: OnItemCheckedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SWSubCategoryInFilterAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycle_item_filter_sub_category, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categorys!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binItems(categorys?.get(position), position)
        holder.itemView.width
        holder.itemView.checkBox.setOnClickListener {
            if (holder.itemView.checkBox.isChecked) {
                listener?.onCheckedItemListener(categorys?.get(position)?.id)
            } else {
                listener?.onUnCheckedItemListener(categorys?.get(position)?.id)
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(item: SWCategoriesResponse.Data.SubCategories?, position: Int) {
            itemView.tvCategory.text = item?.name

            if (selecteds != null) {
                if (selecteds?.categoryIds != null && selecteds?.categoryIds!!.isNotEmpty()) {
                    for (element in selecteds?.categoryIds!!) {
                        if (element == item?.id) {
                            itemView.checkBox.isChecked = true
                            listener?.onCheckedItemListener(categorys?.get(position)?.id)
                        }
                    }
                }
            }
        }
    }

    fun onCallBackCheckedItem(listener: OnItemCheckedListener) {
        this.listener = listener
    }

    interface OnItemCheckedListener {
        fun onCheckedItemListener(id: Int?)

        fun onUnCheckedItemListener(id: Int?)
    }

}