package com.sanwashoseki.bookskozuchi.business.filter.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWCategoriesResponse
import kotlinx.android.synthetic.main.recycle_item_filter_category.view.*

@Suppress("UNREACHABLE_CODE")
class SWCategoryInFilterAdapter(private var categorys: List<SWCategoriesResponse.Data>?, private var selectIds: List<Int>?) :
    RecyclerView.Adapter<SWCategoryInFilterAdapter.ViewHolder>() {

    private var listener: OnCategoryItemClicked? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SWCategoryInFilterAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycle_item_filter_category, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categorys!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binItems(categorys?.get(position), categorys?.get(position)?.id)
        holder.itemView.width
        holder.itemView.tvCategory.setOnClickListener { listener?.onCategoryClickedListener(categorys?.get(position)?.id, categorys?.get(position)?.name, categorys?.get(position)) }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(item: SWCategoriesResponse.Data?, idCategory: Int?) {
            if (item?.subCategories?.size == 0) {
                itemView.tvCategory.visibility = View.GONE
                itemView.tvCategoryEmpty.visibility = View.VISIBLE
                itemView.tvCategoryEmpty.text = item.name
            } else {
                itemView.tvCategory.visibility = View.VISIBLE
                itemView.tvCategoryEmpty.visibility = View.GONE
                itemView.tvCategory.text = item?.name
            }

            for (i in selectIds!!.indices) {
                itemView.checkBox.isChecked = item?.id == selectIds!![i]
            }

            itemView.checkBox.setOnClickListener {
                if (itemView.checkBox.isChecked) {
                    if (item?.subCategories!!.isNotEmpty()) {
                        itemView.tvSubCategory.visibility = View.VISIBLE
                        var subContent = ""
                        for (i in item.subCategories!!.indices) {
                            if (i == 0) {
                                subContent = item.subCategories!![i].name.toString()
                            } else {
                                if (subContent != "") {
                                    subContent = subContent + "、 " + item.subCategories!![i].name
                                }
                            }
                        }
                        itemView.tvSubCategory.text = subContent
                    }
                    listener?.onCheckAllListener(idCategory, item.subCategories)
                } else {
                    itemView.tvSubCategory.visibility = View.GONE
                    listener?.onUnCheckAllListener(idCategory, item?.subCategories)
                }
            }

            val ids = ArrayList<Int?>()
            for (i in item?.subCategories!!.indices) {
                ids.add(item.subCategories!![i].id)
            }
            Log.d("TAG", "binItems: $ids  $selectIds")
            if (selectIds?.size != 0) {
                if (item.subCategories!!.isNotEmpty()) {
                    itemView.checkBox.isChecked = selectIds!!.containsAll(ids)
                    val names = ArrayList<String?>()
                    var subContent = ""
                    for (i in selectIds!!.indices) {
                        for (j in item.subCategories!!.indices) {
                            if (selectIds!![i] == item.subCategories!![j].id) {
                                itemView.tvSubCategory.visibility = View.VISIBLE
                                names.add(item.subCategories!![j].name)
                            }
                        }
                    }
                    for (i in 0 until names.size) {
                        if (i == 0) {
                            subContent =names[i].toString()
                        } else {
                            if (subContent != "") {
                                subContent = subContent + "、 " + names[i]
                            }
                        }
                    }
                    itemView.tvSubCategory.text = subContent
                }
            }
        }
    }

    fun onCallbackListener(listener: OnCategoryItemClicked) {
        this.listener = listener
    }

    interface OnCategoryItemClicked {
        fun onCategoryClickedListener(idCategory: Int?, name: String?, subCategory: SWCategoriesResponse.Data?)

        fun onCheckAllListener(idCategory: Int?, subCategory: List<SWCategoriesResponse.Data.SubCategories>?)

        fun onUnCheckAllListener(idCategory: Int?, subCategory: List<SWCategoriesResponse.Data.SubCategories>?)
    }

}