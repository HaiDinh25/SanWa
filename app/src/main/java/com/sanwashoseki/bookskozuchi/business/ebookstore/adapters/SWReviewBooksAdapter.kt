package com.sanwashoseki.bookskozuchi.business.ebookstore.adapters

/*
 * Created by Dinh.Van.Hai on 03/12/2020
 * Mobile 0931670595
 */

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWBookDetailResponse
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.book_store_item_high_light_books.view.ratingBar
import kotlinx.android.synthetic.main.recycle_item_book_review.view.*

@Suppress("UNREACHABLE_CODE")
class SWReviewBooksAdapter(private var reviews: List<SWBookDetailResponse.Data.ProductReviewsModel>?) : RecyclerView.Adapter<SWReviewBooksAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycle_item_book_review, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.d("TAG", "getItemCount: ${reviews!!.size}")
        return reviews!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binItems(reviews!![position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(review: SWBookDetailResponse.Data.ProductReviewsModel?) {

            itemView.tvUser.text = review?.customerName
            itemView.ratingBar.rating = review?.rating!!.toFloat()
            itemView.tvDate.text = review.writtenOnStr
            itemView.tvContent.text = review.reviewText

            Glide.get(itemView.context).clearMemory()
            Glide.with(itemView.context)
                .load(review.customerAvatarUrl)
                .into(itemView.imgAvatar)
        }
    }

}