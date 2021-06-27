package com.sanwashoseki.bookskozuchi.business.ebookstore.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.Html
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookInfoResponse
import com.sanwashoseki.bookskozuchi.utilities.SWUIHelper
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.book_store_item_latest_books.view.*
import kotlinx.android.synthetic.main.book_store_item_latest_books.view.imageViewBookStore
import kotlinx.android.synthetic.main.book_store_item_latest_books.view.ratingBar
import kotlinx.android.synthetic.main.book_store_item_latest_books.view.tvAuthor
import kotlinx.android.synthetic.main.book_store_item_latest_books.view.tvName
import kotlinx.android.synthetic.main.book_store_item_latest_books.view.tvPrice
import kotlinx.android.synthetic.main.recycle_item_high_light_books.view.*

@Suppress("UNREACHABLE_CODE")
class SWLatestBooksAdapter(
    private val type: Int,
    private var latestBooks: List<SWBookInfoResponse>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listener: OnItemClickListener? = null
    private var screenWidth = 0

    init {
        if (latestBooks == null) latestBooks = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> {
                val displayMetrics = DisplayMetrics()
//                context.windowManager.defaultDisplay.getMetrics(displayMetrics)
//                screenWidth = displayMetrics.widthPixels

                (parent.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                    .defaultDisplay.getMetrics(displayMetrics)
                screenWidth = displayMetrics.widthPixels

                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.book_store_item_latest_books,
                    parent,
                    false
                )
                ViewHolderBookStoreItem(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycle_item_high_light_books, parent, false)
                ViewHolderLatestItem(view)
            }
        }
    }

    fun setData(result: List<SWBookInfoResponse>) {
        latestBooks = result
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return latestBooks!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return type
    }

    fun addMoreData(result: List<SWBookInfoResponse>) {
        val oldLastIndex = latestBooks?.lastIndex
        val extendedList = latestBooks as ArrayList<SWBookInfoResponse>
        extendedList.addAll(result.asIterable())
        latestBooks = extendedList
        notifyItemRangeInserted(oldLastIndex!! + 1 , result.size)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            1 -> {
                val itemWidth = screenWidth / 1.5
                val lp = holder.itemView.layout.layoutParams
                lp.height = lp.height
                lp.width = itemWidth.toInt()
                holder.itemView.layoutParams = lp

                val holderBookStore: ViewHolderBookStoreItem = holder as ViewHolderBookStoreItem
                holderBookStore.binItems(latestBooks?.get(position))
                holder.itemView.width
                holder.itemView.imageViewBookStore.setOnClickListener {
                    this@SWLatestBooksAdapter.listener?.onLatestItemClicked(latestBooks?.get(position))
                }
            }
            2 -> {
                val holderLatest: ViewHolderLatestItem = holder as ViewHolderLatestItem
                holderLatest.binItems(latestBooks?.get(position))
                holder.itemView.imageViewHighlight.setOnClickListener {
                    this@SWLatestBooksAdapter.listener?.onLatestItemClicked(latestBooks?.get(position))
                }
            }
        }
    }

    fun setOnCallBackListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onLatestItemClicked(model: SWBookInfoResponse?)
    }

    inner class ViewHolderBookStoreItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(latest: SWBookInfoResponse?) {
            itemView.tvName.text = latest?.productName
            itemView.tvAuthor.text = latest?.authorName
            if (latest?.ratingSum!! < 1) {
                itemView.ratingBar.visibility = View.GONE
            } else {
                itemView.ratingBar.visibility = View.VISIBLE
                itemView.ratingBar.rating = latest.ratingSum!!.toFloat()
            }
//            val sp = Html.fromHtml(detail?.data?.productReviewOverview?.fullDescription)
            if (latest.fullDescription != null) {
                itemView.tvContent.text = Html.fromHtml(latest.fullDescription)
            } else {
                itemView.tvContent.text = latest.fullDescription
            }
            itemView.tvPrice.text = itemView.context.getString(R.string.currency) + " " + SWUIHelper.formatTextPrice(latest.priceValue?.toDouble())
            if (latest.audioReading == false) {
                itemView.icListener.visibility = View.GONE
            }

            Glide.get(itemView.context).clearMemory()
            Glide.with(itemView.context)
                .load(latest.defaultPictureModel?.imageUrl)
                .into(itemView.imageViewBookStore)
        }
    }

    inner class ViewHolderLatestItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(latest: SWBookInfoResponse?) {
            itemView.tvName.text = latest?.productName
            itemView.tvAuthor.text = latest?.authorName
            if (latest?.ratingSum!! < 1) {
                itemView.ratingBar.visibility = View.GONE
            } else {
                itemView.ratingBar.visibility = View.VISIBLE
                itemView.ratingBar.rating = latest.ratingSum!!.toFloat()
            }
            itemView.tvPrice.text = itemView.context.getString(R.string.currency) + " " + SWUIHelper.formatTextPrice(latest.priceValue?.toDouble())
            if (latest.audioReading == false) {
                itemView.icListenerItem.visibility = View.GONE
            }

            Glide.get(itemView.context).clearMemory()
            Glide.with(itemView.context)
                .load(latest.defaultPictureModel?.imageUrl)
                .into(itemView.imageViewHighlight)
        }
    }
}