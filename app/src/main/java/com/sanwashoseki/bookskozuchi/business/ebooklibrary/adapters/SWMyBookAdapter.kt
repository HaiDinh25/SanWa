package com.sanwashoseki.bookskozuchi.business.ebooklibrary.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.ebooklibrary.models.responses.SWMyBookResponse
import com.sanwashoseki.bookskozuchi.utilities.SWUIHelper
import kotlinx.android.synthetic.main.recycle_item_top_seller_books.view.*

@Suppress("UNREACHABLE_CODE")
class SWMyBookAdapter(private var mybooks: List<SWMyBookResponse.Data>?) : RecyclerView.Adapter<SWMyBookAdapter.ViewHolder>() {

    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SWMyBookAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_item_top_seller_books, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mybooks!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binItems(mybooks?.get(position), position)
        holder.itemView.width
    }

    fun setOnCallBackListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onDetailListener(id: Int?)
    }

    fun addMoreBooks(result: List<SWMyBookResponse.Data>) {
        val oldLastIndex = mybooks?.lastIndex
        val extendedList = mybooks as ArrayList<SWMyBookResponse.Data>
        extendedList.addAll(result)
        mybooks = extendedList
        if (oldLastIndex != null) {
            notifyItemRangeInserted(oldLastIndex + 1, result.size)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(item: SWMyBookResponse.Data?, position: Int?) {
            itemView.tvNumItem.text = (position?.plus(1)).toString()
            itemView.tvName.text = item?.name
            itemView.tvPrice.text = itemView.context.getString(R.string.currency) + " " + SWUIHelper.formatTextPrice(item?.price)
            var author = ""
            for (i in item?.authors!!.indices) {
                if (i == 0) {
                    author = item.authors!![i].name.toString()
                } else {
                    if (author != "") {
                        author = author + "、 " + item.authors!![i].name.toString()
                    }
                }
            }
            itemView.tvAuthor.text = author
            if (item.ratingSum!! < 1) {
                itemView.ratingBar.visibility = View.GONE
            } else {
                itemView.ratingBar.rating = item.ratingSum!!.toFloat()
            }

            if (item.audioReading == false) {
                itemView.icListenerItem.visibility = View.GONE
            }

            Glide.get(itemView.context).clearMemory()
            Glide.with(itemView.context)
                .load(item.defaultPictureModel?.imageUrl)
                .into(itemView.imageViewSeller)

            itemView.btnWishList.visibility = View.GONE
            itemView.tvNumItem.visibility = View.GONE
            itemView.ratingBar.visibility = View.GONE
            itemView.tvPrice.visibility = View.GONE

            itemView.setOnClickListener { listener?.onDetailListener(item.id) }
        }
    }

}