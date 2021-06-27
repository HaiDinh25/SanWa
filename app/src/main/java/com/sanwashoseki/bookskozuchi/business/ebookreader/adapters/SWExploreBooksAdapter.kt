package com.sanwashoseki.bookskozuchi.business.ebookreader.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.ebookreader.models.response.SWReadingNowResponse
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.recycle_item_top_seller_books.view.*
import kotlinx.android.synthetic.main.recycle_item_top_seller_books.view.imageViewSeller
import kotlinx.android.synthetic.main.recycle_item_top_seller_books.view.ratingBar
import kotlinx.android.synthetic.main.recycle_item_top_seller_books.view.tvAuthor
import kotlinx.android.synthetic.main.recycle_item_top_seller_books.view.tvName

@Suppress("UNREACHABLE_CODE")
class SWExploreBooksAdapter(
    private var explores: List<SWReadingNowResponse.Data>?,
) : RecyclerView.Adapter<SWExploreBooksAdapter.ViewHolder>() {

    private var listener: OnBookDetailListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SWExploreBooksAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycle_item_top_seller_books, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {

        return explores!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binItems(explores?.get(position))
        holder.itemView.setOnClickListener {
            listener?.onDetailBookListener(explores?.get(position)?.id)
        }
    }

    fun onCallBackDetailBook(listener: OnBookDetailListener) {
        this.listener = listener
    }

    interface OnBookDetailListener {
        fun onDetailBookListener(id: Int?)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(explore: SWReadingNowResponse.Data?) {
            itemView.tvNumItem.visibility = View.GONE
            itemView.btnWishList.visibility = View.GONE
            itemView.tvName.text = explore?.name
            var author = ""
            for (i in explore?.authors!!.indices) {
                if (i == 0) {
                    author = explore.authors!![i].name.toString()
                } else {
                    if (author != "") {
                        author = author + "、 " + explore.authors!![i].name.toString()
                    }
                }
            }
            itemView.tvAuthor.text = author
            if (explore.ratingSum!! < 1) {
                itemView.ratingBar.visibility = View.GONE
            } else {
                itemView.ratingBar.rating = explore.ratingSum!!.toFloat()
            }
            if (explore.audioReading == false) {
                itemView.icListenerItem.visibility = View.GONE
            }

            Glide.get(itemView.context).clearMemory()
            Glide.with(itemView.context)
                .load(explore.defaultPictureModel?.imageUrl)
                .into(itemView.imageViewSeller)
        }
    }

}