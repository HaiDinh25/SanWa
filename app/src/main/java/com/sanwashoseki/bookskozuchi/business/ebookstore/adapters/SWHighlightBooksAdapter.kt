package com.sanwashoseki.bookskozuchi.business.ebookstore.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookInfoResponse
import com.sanwashoseki.bookskozuchi.utilities.SWUIHelper
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.book_store_item_high_light_books.view.*
import kotlinx.android.synthetic.main.book_store_item_high_light_books.view.imageViewBookStore
import kotlinx.android.synthetic.main.book_store_item_high_light_books.view.ratingBar
import kotlinx.android.synthetic.main.book_store_item_high_light_books.view.tvAuthor
import kotlinx.android.synthetic.main.book_store_item_high_light_books.view.tvName
import kotlinx.android.synthetic.main.book_store_item_high_light_books.view.tvPrice
import kotlinx.android.synthetic.main.recycle_item_high_light_books.view.*

@Suppress("UNREACHABLE_CODE")
class SWHighlightBooksAdapter(
    private val type: Int,
    private var highlights: List<SWBookInfoResponse>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listener: OnItemClickListener? = null

    init {
        if (highlights == null) highlights = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            1 -> {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.book_store_item_high_light_books,
                    parent,
                    false
                )
                return ViewHolderBookStoreItem(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.recycle_item_high_light_books,
                    parent,
                    false
                )
                return ViewHolderHighlightItem(view)
            }
        }
    }

    fun setData(result: List<SWBookInfoResponse>) {
        highlights = result
        notifyDataSetChanged()
    }

    fun addMoreData(result: List<SWBookInfoResponse>) {
        val oldLastIndex = highlights?.lastIndex
        val extendedList = highlights as ArrayList<SWBookInfoResponse>
        extendedList.addAll(result.asIterable())
        highlights = extendedList
        notifyItemRangeInserted(oldLastIndex!! + 1 , result.size)
    }

    override fun getItemCount(): Int {
        return highlights!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val read: Boolean = position % 2 == 0
        when (holder.itemViewType) {
            1 -> {
                val holderBookStore: ViewHolderBookStoreItem = holder as ViewHolderBookStoreItem
                holderBookStore.binItems(highlights?.get(position))
                holder.itemView.setOnClickListener {
                    this@SWHighlightBooksAdapter.listener?.onHighlightItemClicked(highlights?.get(position), read)
                }
            }
            2 -> {
                val holderHighLight: ViewHolderHighlightItem = holder as ViewHolderHighlightItem
                holderHighLight.binItems(highlights?.get(position))
                holder.itemView.setOnClickListener {
                    this@SWHighlightBooksAdapter.listener?.onHighlightItemClicked(highlights?.get(position), read)
                }
            }
        }

    }

    fun setOnCallBackListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onHighlightItemClicked(model: SWBookInfoResponse?, read: Boolean)
    }

    inner class ViewHolderBookStoreItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(highLight: SWBookInfoResponse?) {
            itemView.tvName.text = highLight?.productName
            itemView.tvAuthor.text = highLight?.authorName
            if (highLight?.ratingSum!! < 1) {
                itemView.ratingBar.visibility = View.GONE
            } else {
                itemView.ratingBar.visibility = View.VISIBLE
                itemView.ratingBar.rating = highLight.ratingSum!!.toFloat()
            }
            itemView.tvPrice.text = itemView.context.getString(R.string.currency) + " " + SWUIHelper.formatTextPrice(highLight.priceValue?.toDouble())
            if (highLight.audioReading == false) {
                itemView.icListener.visibility = View.GONE
            }

            Glide.get(itemView.context).clearMemory()
            Glide.with(itemView.context)
                .load(highLight.defaultPictureModel?.imageUrl)
                .into(itemView.imageViewBookStore)
        }
    }

    inner class ViewHolderHighlightItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(highLight: SWBookInfoResponse?) {
            itemView.tvName.text = highLight?.productName
            itemView.tvAuthor.text = highLight?.authorName
            if (highLight?.ratingSum!! < 1) {
                itemView.ratingBar.visibility = View.GONE
            } else {
                itemView.ratingBar.visibility = View.VISIBLE
                itemView.ratingBar.rating = highLight.ratingSum!!.toFloat()
            }
            itemView.tvPrice.text = itemView.context.getString(R.string.currency) + " " + SWUIHelper.formatTextPrice(highLight.priceValue?.toDouble())
            if (highLight.audioReading == false) {
                itemView.icListenerItem.visibility = View.GONE
            }

            Glide.get(itemView.context).clearMemory()
            Glide.with(itemView.context)
                .load(highLight.defaultPictureModel?.imageUrl)
                .into(itemView.imageViewHighlight)
        }
    }
}