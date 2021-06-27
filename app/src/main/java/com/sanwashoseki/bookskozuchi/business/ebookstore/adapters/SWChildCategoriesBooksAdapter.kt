package com.sanwashoseki.bookskozuchi.business.ebookstore.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookInfoResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWChildCategoriesBookResponse
import kotlinx.android.synthetic.main.recycle_item_category.view.*
import kotlinx.android.synthetic.main.recycle_item_category.view.tvName

@Suppress("UNREACHABLE_CODE")
class SWChildCategoriesBooksAdapter(
    private var type: Int,
    private var categories: List<SWChildCategoriesBookResponse.Data.SubCategories>?,
    private var product: SWChildCategoriesBookResponse.Data?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listener: OnCategoriesItemClickedListener? = null
    private var adapter: SWHighlightBooksAdapter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycle_item_category, parent, false)
                ViewHolderWithSubCategory(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycle_item_category, parent, false)
                ViewHolderWithProduct(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (type == 1) {
            categories!!.size
        } else {
            Log.d("TAG", "getItemCount: " + product!!.products!!.size)
//            product!!.products!!.size
            1
        }

    }

    override fun getItemViewType(position: Int): Int {
        return type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType) {
            1 -> {
                val holderCategory: ViewHolderWithSubCategory = holder as ViewHolderWithSubCategory
                holderCategory.binItems(categories?.get(position), position)
                holderCategory.itemView.btnMore.setOnClickListener {
                    listener?.onSeeMoreClicked(categories?.get(position)?.id) }
            }
            2 -> {
                val holderProduct: ViewHolderWithProduct = holder as ViewHolderWithProduct
                holderProduct.binItems(product)
//                holderProduct.itemView.setOnClickListener { listener?.onCategoriesClickedListener(categories?.get(position)?.id) }
            }
        }
    }

    interface OnCategoriesItemClickedListener {
        fun onSeeMoreClicked(id: Int?)

        fun onDetailBookClickedListener(detail: SWBookInfoResponse?)
    }

    fun onCallBackCategoriesClicked(listener: OnCategoriesItemClickedListener) {
        this.listener = listener
    }


    inner class ViewHolderWithSubCategory(itemView: View) : RecyclerView.ViewHolder(itemView),
        SWHighlightBooksAdapter.OnItemClickListener {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(item: SWChildCategoriesBookResponse.Data.SubCategories?, position: Int) {
            itemView.layoutMore.visibility = View.VISIBLE
            val manager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = SWHighlightBooksAdapter(1, item?.products)

            itemView.tvName.text = "#" + item?.name

            Log.d("TAG", "binItems: $categories")
            if (categories!![position].products!!.isEmpty()) {
                itemView.tvNull.visibility = View.VISIBLE
                itemView.rcBook.visibility = View.GONE
                itemView.btnMore.visibility = View.GONE
            } else {
                itemView.tvNull.visibility = View.GONE
                itemView.rcBook.visibility = View.VISIBLE
                itemView.btnMore.visibility = View.VISIBLE

                itemView.rcBook?.layoutManager = manager
                itemView.rcBook?.itemAnimator = DefaultItemAnimator()
                itemView.rcBook?.adapter = adapter
            }
            adapter?.setOnCallBackListener(this)
        }

        override fun onHighlightItemClicked(model: SWBookInfoResponse?, read: Boolean) {
            listener?.onDetailBookClickedListener(model)
        }
    }

    inner class ViewHolderWithProduct(itemView: View) : RecyclerView.ViewHolder(itemView),
        SWHighlightBooksAdapter.OnItemClickListener {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(item: SWChildCategoriesBookResponse.Data?) {
            itemView.layoutMore.visibility = View.GONE
            val manager = GridLayoutManager(itemView.context, 2)
            adapter = SWHighlightBooksAdapter(2, item?.products)

            itemView.rcBook?.layoutManager = manager
            itemView.rcBook?.itemAnimator = DefaultItemAnimator()
            itemView.rcBook?.adapter = adapter

            adapter?.setOnCallBackListener(this)
        }

        override fun onHighlightItemClicked(model: SWBookInfoResponse?, read: Boolean) {
            listener?.onDetailBookClickedListener(model)
        }
    }

}