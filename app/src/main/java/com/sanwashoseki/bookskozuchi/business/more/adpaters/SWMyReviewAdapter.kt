package com.sanwashoseki.bookskozuchi.business.more.adpaters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWMyReviewResponse
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.recycle_item_my_review.view.*

@Suppress("UNREACHABLE_CODE")
class SWMyReviewAdapter(private var myreviews: List<SWMyReviewResponse.Data>?) : RecyclerView.Adapter<SWMyReviewAdapter.ViewHolder>() {

    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SWMyReviewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycle_item_my_review, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return myreviews!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val holderBookStore: ViewHolder = holder
        holderBookStore.binItems(myreviews!![position], position)
        holder.itemView.width
    }

    fun onCallBackClicked(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onDeleteReviewListener(id: Int?)

        fun onDetailListener(id: Int?)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(item: SWMyReviewResponse.Data, position: Int) {
            itemView.tvTitle.text = item.bookName
            itemView.tvAuthor.text = item.authorName
            itemView.ratingBar.rating = item.rating!!
            itemView.tvDate.text = item.createdOn
            itemView.tvStatus.text = item.status
            itemView.tvContent.text = item.reviewText
            if (item.status != "Published") {
                itemView.btnDelete.visibility = View.VISIBLE
                itemView.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorTextMore))
            } else {
                itemView.btnDelete.visibility = View.GONE
                itemView.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorSigningBackground))
            }
            if (position == myreviews?.size!! - 1) {
                itemView.view.visibility = View.GONE
            }
            if (item.isAudioBook == true) {
                itemView.icListener.visibility = View.VISIBLE
            }

            Glide.with(itemView.context)
                .load(item.pictureModel?.imageUrl)
                .into(itemView.imageView)

            itemView.imageView.setOnClickListener { listener?.onDetailListener(item.productId) }
            itemView.btnDelete.setOnClickListener { listener?.onDeleteReviewListener(item.id) }
        }
    }

}