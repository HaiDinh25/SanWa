package com.sanwashoseki.bookskozuchi.business.ebooklibrary.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.response.SWGetShoppingCartResponse
import com.sanwashoseki.bookskozuchi.utilities.SWUIHelper
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.recycle_item_top_seller_books.view.*

@Suppress("UNREACHABLE_CODE")
class SWWishListAdapter(private var wishlists: List<SWGetShoppingCartResponse.Data.Item>?) : RecyclerView.Adapter<SWWishListAdapter.ViewHolder>() {

    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SWWishListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_item_top_seller_books, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return wishlists!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val holderBookStore: ViewHolder = holder
        holderBookStore.binItems(wishlists!![position], position)
        holder.itemView.width
    }

    fun setOnCallBackListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onDetailListener(id: Int?)

        fun onUnWishListListener(id: Int?)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(wishList: SWGetShoppingCartResponse.Data.Item?, position: Int) {
            itemView.tvNumItem.text = (position + 1).toString()
            itemView.tvNumItem.visibility = View.GONE
            itemView.tvName.text = wishList?.bookName
            itemView.tvAuthor.text = wishList?.authorName
            if (wishList?.ratingSum!! < 1) {
                itemView.ratingBar.visibility = View.GONE
            } else {
                itemView.ratingBar.rating = wishList.ratingSum!!.toFloat()
            }
            itemView.tvPrice.text = itemView.context.getString(R.string.currency) + " " + SWUIHelper.formatTextPrice(wishList.price)
            if (wishList.audioReading!!) {
                itemView.icListenerItem.visibility = View.VISIBLE
            } else {
                itemView.icListenerItem.visibility = View.GONE
            }

            Glide.get(itemView.context).clearMemory()
            Glide.with(itemView.context)
                .load(wishList.pictureMobileModel?.imageUrl)
                .into(itemView.imageViewSeller)

            itemView.btnWishList.visibility = View.VISIBLE

            itemView.setOnClickListener { listener?.onDetailListener(wishList.productId) }
            itemView.btnWishList.setOnClickListener { listener?.onUnWishListListener(wishList.shoppingCartItemId) }
        }
    }

}