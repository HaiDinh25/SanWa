package com.sanwashoseki.bookskozuchi.business.ebookstore.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWCategoriesResponse
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.book_store_item_categories.view.*

@Suppress("UNREACHABLE_CODE")
class SWCategoriesBooksAdapter(private var categories: List<SWCategoriesResponse.Data>?) : RecyclerView.Adapter<SWCategoriesBooksAdapter.ViewHolder>() {

    var listener: OnCategoriesItemClickedListener? = null
    init {
        if (categories == null) categories = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.book_store_item_categories, parent, false)

        return ViewHolder(view)
    }

    fun setData(data: List<SWCategoriesResponse.Data>?) {
        categories = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return categories!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binItems(categories?.get(position))
        holder.itemView.setOnClickListener { listener?.onCategoriesClickedListener(categories?.get(position)?.id) }
    }

    interface OnCategoriesItemClickedListener {
        fun onCategoriesClickedListener(id: Int?)
    }

    fun onCallBackCategoriesClicked(listener: OnCategoriesItemClickedListener) {
        this.listener = listener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(item: SWCategoriesResponse.Data?) {

            itemView.tvName.text = item?.name

            Glide.get(itemView.context).clearMemory()
            Glide.with(itemView.context)
                .load(item?.iconUrl)
                .into(itemView.imgIcon)
        }
    }

}