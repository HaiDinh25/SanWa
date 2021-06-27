package com.sanwashoseki.bookskozuchi.business.ebookstore.adapters

/*
 * Created by Dinh.Van.Hai on 17/11/2020
 * Mobile 0931670595
 */

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
import kotlinx.android.synthetic.main.book_store_item_latest_books.view.*
import kotlinx.android.synthetic.main.recycle_item_book_store_top_seller.view.*
import kotlinx.android.synthetic.main.recycle_item_book_store_top_seller.view.imageViewSeller
import kotlinx.android.synthetic.main.recycle_item_book_store_top_seller.view.ratingBar
import kotlinx.android.synthetic.main.recycle_item_book_store_top_seller.view.tvAuthor
import kotlinx.android.synthetic.main.recycle_item_book_store_top_seller.view.tvName
import kotlinx.android.synthetic.main.recycle_item_book_store_top_seller.view.tvPrice
import kotlinx.android.synthetic.main.recycle_item_book_store_top_seller.view.icListener
import kotlinx.android.synthetic.main.recycle_item_top_seller_books.view.*

@Suppress("UNREACHABLE_CODE")
class SWTopSellerBooksAdapter(
    private val type: Int,
    private var sellers: List<SWBookInfoResponse>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listener: OnItemClickListener? = null

    init {
        if (sellers == null) sellers = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycle_item_book_store_top_seller, parent, false)
                ViewHolderBookStoreItem(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycle_item_top_seller_books, parent, false)
                ViewHolderTopSellerItem(view)
            }
        }
    }

    fun setData(result:  List<SWBookInfoResponse>?) {
        sellers = result
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (sellers!= null) {
            sellers!!.size
        } else {
            0
        }
    }

    override fun getItemViewType(position: Int): Int {
        return type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            1 -> {
                val holderBookStore: ViewHolderBookStoreItem = holder as ViewHolderBookStoreItem
                holderBookStore.binItems(sellers?.get(position), position)
                holder.itemView.width
                holder.itemView.setOnClickListener {
                    this@SWTopSellerBooksAdapter.listener?.onTopSellerItemClicked(sellers?.get(position))
                }
            }
            2 -> {
                val holderTopSeller: ViewHolderTopSellerItem = holder as ViewHolderTopSellerItem
                holderTopSeller.binItems(sellers?.get(position), position)
                holder.itemView.setOnClickListener {
                    this@SWTopSellerBooksAdapter.listener?.onTopSellerItemClicked(sellers?.get(position))
                }
            }
        }

    }

    fun setOnCallBackListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onTopSellerItemClicked(model: SWBookInfoResponse?)
    }

    inner class ViewHolderBookStoreItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(topSeller: SWBookInfoResponse?, position: Int?) {
            itemView.tvName.text  = topSeller?.productName
            itemView.tvAuthor.text = topSeller?.authorName
            itemView.tvNum.text = (position?.plus(1)).toString()
            if (topSeller?.ratingSum!! < 1) {
                itemView.ratingBar.visibility = View.GONE
            } else {
                itemView.ratingBar.visibility = View.VISIBLE
                itemView.ratingBar.rating = topSeller.ratingSum!!.toFloat()
            }
            itemView.tvPrice.text = itemView.context.getString(R.string.currency) + " " + SWUIHelper.formatTextPrice(topSeller.priceValue?.toDouble())
            if (topSeller.audioReading == false) {
                itemView.icListener.visibility = View.GONE
            }

            Glide.get(itemView.context).clearMemory()
            Glide.with(itemView.context)
                .load(topSeller.defaultPictureModel?.imageUrl)
                .into(itemView.imageViewSeller)
        }
    }

    inner class ViewHolderTopSellerItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(item: SWBookInfoResponse?, position: Int?) {
            itemView.tvName.text = item?.productName
            itemView.tvAuthor.text = item?.authorName
            itemView.tvNumItem.text = (position?.plus(1)).toString()
            if (item?.ratingSum!! < 1) {
                itemView.ratingBar.visibility = View.GONE
            } else {
                itemView.ratingBar.visibility = View.VISIBLE
                itemView.ratingBar.rating = item.ratingSum!!.toFloat()
            }
            itemView.tvPrice.text = itemView.context.getString(R.string.currency) + " " + SWUIHelper.formatTextPrice(item.priceValue?.toDouble())
            if (item.audioReading == false) {
                itemView.icListenerItem.visibility = View.GONE
            }

            Glide.get(itemView.context).clearMemory()
            Glide.with(itemView.context)
                .load(item.defaultPictureModel?.imageUrl)
                .into(itemView.imageViewSeller)

            itemView.btnWishList.visibility = View.GONE
        }
    }

}