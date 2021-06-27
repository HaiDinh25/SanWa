package com.sanwashoseki.bookskozuchi.business.search.adapters

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
import kotlinx.android.synthetic.main.book_store_item_high_light_books.view.ratingBar
import kotlinx.android.synthetic.main.book_store_item_high_light_books.view.tvAuthor
import kotlinx.android.synthetic.main.book_store_item_high_light_books.view.tvName
import kotlinx.android.synthetic.main.book_store_item_high_light_books.view.tvPrice
import kotlinx.android.synthetic.main.book_store_item_latest_books.view.*
import kotlinx.android.synthetic.main.recycle_item_high_light_books.view.*

class SWSearchBookAdapter (private var books: List<SWBookInfoResponse>?) :
    RecyclerView.Adapter<SWSearchBookAdapter.ViewHolder>() {

    private var listener: OnItemClickedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SWSearchBookAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_item_search_book, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SWSearchBookAdapter.ViewHolder, position: Int) {
        holder.binItems(books?.get(position))
        holder.itemView.setOnClickListener {
            this@SWSearchBookAdapter.listener?.onItemClickedListener(books?.get(position))
        }
    }

    override fun getItemCount(): Int {
        return books!!.size
    }

    fun addMoreData(result: List<SWBookInfoResponse>) {
        val oldLastIndex = books?.lastIndex
        val extendedList = books as ArrayList<SWBookInfoResponse>
        extendedList.addAll(result.asIterable())
        books = extendedList
        notifyItemRangeInserted(oldLastIndex!! + 1 , result.size)
    }

    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun binItems(book: SWBookInfoResponse?) {
            itemView.tvName.text = book?.productName
            itemView.tvAuthor.text = book?.authorName
            if (book?.ratingSum!! < 1) {
                itemView.ratingBar.visibility = View.GONE
            } else {
                itemView.ratingBar.visibility = View.VISIBLE
                itemView.ratingBar.rating = book.ratingSum!!.toFloat()
            }
            itemView.tvPrice.text = "￥ " + SWUIHelper.formatTextPrice(book.priceValue?.toDouble())
            if (book.audioReading == false) {
                itemView.icListenerItem.visibility = View.GONE
            }

            Glide.get(itemView.context).clearMemory()
            Glide.with(itemView.context)
                .load(book.defaultPictureModel?.imageUrl)
                .into(itemView.imageViewHighlight)
        }
    }

    fun onCallBackListener(listener: OnItemClickedListener) {
        this.listener = listener
    }

    interface OnItemClickedListener {
        fun onItemClickedListener(result: SWBookInfoResponse?)
    }
}