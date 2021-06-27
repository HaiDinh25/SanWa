package com.sanwashoseki.bookskozuchi.business.more.adpaters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookInfoResponse
import com.sanwashoseki.bookskozuchi.business.more.views.quickaction.ActionItem
import com.sanwashoseki.bookskozuchi.business.more.views.quickaction.QuickAction
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.recycle_item_purchase_history_child.view.*


@Suppress("UNREACHABLE_CODE")
class SWPurchaseHistoryChildAdapter(private var books: List<SWBookInfoResponse>?) :
    RecyclerView.Adapter<SWPurchaseHistoryChildAdapter.ViewHolder>() {

    private var listener: OnItemClickListener? = null
    private var quickAction: QuickAction? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SWPurchaseHistoryChildAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.recycle_item_purchase_history_child,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return books!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binItems(books!![position])
        holder.itemView.width
    }

    fun onCallBackClicked(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onWriteReviewListener(id: Int?)

        fun onReadReviewListener(id: Int?)

        fun onReadNowListener(id: Int?)

        fun onDetailListener(id: Int?)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        QuickAction.OnActionItemClickListener {
        var writeReviewItem: ActionItem? = null
        var readReviewItem: ActionItem? = null
        var readNowItem: ActionItem? = null
        var idProduct: Int? = null
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(item: SWBookInfoResponse?) {
            itemView.tvTitle.text = item?.productName
            itemView.tvAuthor.text = item?.authorName
            itemView.tvPrice.text = item?.priceValue.toString()
            itemView.tvUser.text = item?.publisherName

            idProduct = item?.id

            Glide.get(itemView.context).clearMemory()
            Glide.with(itemView.context)
                .load(item?.defaultPictureModel?.imageUrl)
                .into(itemView.imageView)

            if (item?.audioReading == false) {
                itemView.icListener.visibility = View.GONE
            }

            itemView.setOnClickListener{ listener?.onDetailListener(item?.id)}

            itemView.btnMore.setOnClickListener {
                QuickAction.setDefaultColor(ResourcesCompat.getColor(itemView.context.resources, R.color.colorAccent, null))
                QuickAction.setDefaultTextColor(Color.BLACK)

                readReviewItem = ActionItem(1, itemView.context.getString(R.string.purchase_read_review), R.drawable.ic_read_review_purchase)
                writeReviewItem = ActionItem(2, itemView.context.getString(R.string.purchase_write_review), R.drawable.ic_write_review_purchase)
                readNowItem = ActionItem(3, itemView.context.getString(R.string.purchase_read_now), R.drawable.ic_read_review_now)

                writeReviewItem?.isSticky = true
                readReviewItem?.isSticky = true
                readNowItem?.isSticky = true

                quickAction = QuickAction(itemView.context, QuickAction.VERTICAL)
                quickAction!!.setColorRes(R.color.colorPrimary)
                quickAction!!.setTextColorRes(R.color.colorTextPrimary)

                if (item?.myProductReview!!.isEmpty()) {
                    quickAction!!.addActionItem(writeReviewItem!!)
                } else {
                    quickAction!!.addActionItem(readReviewItem!!)
                }
                quickAction!!.addActionItem(readNowItem!!)
                quickAction!!.setTextColor(Color.YELLOW)

                quickAction!!.setOnActionItemClickListener(this)
                quickAction!!.show(itemView.btnMore)
            }
        }

        override fun onItemClick(item: ActionItem?) {
            when (item) {
                writeReviewItem -> listener?.onWriteReviewListener(idProduct)
                readReviewItem -> listener?.onReadReviewListener(idProduct)
                else -> listener?.onReadNowListener(idProduct)
            }
            quickAction!!.dismiss()
        }
    }

}