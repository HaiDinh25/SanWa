package com.sanwashoseki.bookskozuchi.business.shoppingcart.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.requests.SWShoppingCartRequest
import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.response.SWGetShoppingCartResponse
import com.sanwashoseki.bookskozuchi.utilities.SWUIHelper
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.recycle_item_confirm_shopping_cart.view.imageView
import kotlinx.android.synthetic.main.recycle_item_confirm_shopping_cart.view.ratingBar
import kotlinx.android.synthetic.main.recycle_item_confirm_shopping_cart.view.tvAuthor
import kotlinx.android.synthetic.main.recycle_item_confirm_shopping_cart.view.tvName
import kotlinx.android.synthetic.main.recycle_item_confirm_shopping_cart.view.tvPrice
import kotlinx.android.synthetic.main.recycle_item_shopping_cart.view.*

@Suppress("UNREACHABLE_CODE")
class SWShoppingCartAdapter(
    private var cartResponses: SWGetShoppingCartResponse.Data?,
    private var cartRequests: ArrayList<SWShoppingCartRequest>?,
    private val type: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycle_item_shopping_cart, parent, false)
                ViewHolderShopping(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycle_item_confirm_shopping_cart, parent, false)
                ViewHolderConfirmShopping(view)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return type
    }

    override fun getItemCount(): Int {
        return if (type == 1) {
            cartResponses!!.items!!.size
        } else {
            cartRequests!!.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            1 -> {
                val holderShopping: ViewHolderShopping = holder as ViewHolderShopping
                holderShopping.binItems(cartResponses?.items!![position], position)
                holderShopping.itemView.width
            }
            else -> {
                val holderConfirm: ViewHolderConfirmShopping = holder as ViewHolderConfirmShopping
                holderConfirm.binItems(cartRequests!![position])
                holderConfirm.itemView.width
            }
        }
    }

    fun onCallBackCheckBoxClicked(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onCheckedListener(id: Int?)

        fun onUnCheckedListener(id: Int?)

        fun onBookDetailListener(id: Int?)

        fun onSearchSimilarListener(id: Int?)

        fun onRemoveBookInShoppingCart(id: Int?)
    }

    inner class ViewHolderShopping(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(item: SWGetShoppingCartResponse.Data.Item?, position: Int) {
            itemView.tvName.text = item?.bookName
            itemView.tvAuthor.text = item?.authorName
            if (item?.ratingSum!! < 1) {
                itemView.ratingBar.visibility = View.GONE
            } else {
                itemView.ratingBar.visibility = View.VISIBLE
                itemView.ratingBar.rating = item.ratingSum?.toFloat()!!
            }
            itemView.tvPrice.text = itemView.context.getString(R.string.currency) + " " + SWUIHelper.formatTextPrice(item.price)

            Glide.get(itemView.context).clearMemory()
            Glide.with(itemView.context)
                .load(item.pictureMobileModel?.imageUrl)
                .into(itemView.imageView)

            for (i in cartRequests!!.indices) {
                Log.d("TAG", "binItems: " + cartRequests!![i].book.shoppingCartItemId + " " + item.shoppingCartItemId)
                if (cartRequests!![i].book.shoppingCartItemId == item.shoppingCartItemId) {
                    itemView.checkBox.isChecked = true
                    listener?.onCheckedListener(position)
                }
            }

            itemView.imageView.setOnClickListener { listener?.onBookDetailListener(item.productId!!) }

            itemView.checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cartRequests?.add(SWShoppingCartRequest(cartResponses!!.items!![position]))
                    Log.d("TAG", "onBindViewHolder: $cartRequests")
                    listener?.onCheckedListener(position)
                } else {
                    val book = SWShoppingCartRequest(cartResponses!!.items!![position])
                    cartRequests?.remove(book)
                    listener?.onUnCheckedListener(position)
                }
            }
            itemView.checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    listener?.onCheckedListener(cartResponses!!.items!![position].shoppingCartItemId)
                } else {
                    listener?.onUnCheckedListener(cartResponses!!.items!![position].shoppingCartItemId)
                }
            }

            if (item.published == false || item.deleted == true) {
                itemView.layoutHide.visibility = View.VISIBLE
                itemView.checkBox.isEnabled = false
                itemView.btnSearchSimilar?.setOnClickListener { listener?.onSearchSimilarListener(cartResponses!!.items!![position].productId) }
            } else {
                itemView.layoutHide.visibility = View.GONE
            }
        }
    }

    inner class ViewHolderConfirmShopping(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(item: SWShoppingCartRequest) {
            itemView.tvName.text = item.book.bookName
            itemView.tvAuthor.text = item.book.authorName
            if (item.book.ratingSum!! < 1) {
                itemView.ratingBar.visibility = View.GONE
            } else {
                itemView.ratingBar.visibility = View.VISIBLE
                itemView.ratingBar.rating = item.book.ratingSum?.toFloat()!!
            }
            itemView.tvPrice.text = itemView.context.getString(R.string.currency) + " " + SWUIHelper.formatTextPrice(item.book.price)

            Glide.get(itemView.context).clearMemory()
            Glide.with(itemView.context)
                .load(item.book.pictureMobileModel?.imageUrl)
                .into(itemView.imageView)
        }
    }
}

