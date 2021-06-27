package com.sanwashoseki.bookskozuchi.business.more.adpaters

/*
 * Created by Dinh.Van.Hai on 04/12/2020
 * Mobile 0931670595
 */

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookInfoResponse
import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWPurchaseHistoryResponse
import kotlinx.android.synthetic.main.recycle_item_purchase_history.view.*

@Suppress("UNREACHABLE_CODE")
class SWPurchaseHistoryAdapter(private var purchases: List<SWPurchaseHistoryResponse.Data>?) :
    RecyclerView.Adapter<SWPurchaseHistoryAdapter.ViewHolder>() {

    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SWPurchaseHistoryAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycle_item_purchase_history, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return purchases!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val holderBookStore: ViewHolder = holder
        holderBookStore.binItems(purchases!![position])
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
        SWPurchaseHistoryChildAdapter.OnItemClickListener {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(item: SWPurchaseHistoryResponse.Data?) {
            itemView.tvCode.text = item?.customOrderNumber.toString()
            itemView.tvOrderPaid.text = item?.orderStatus.toString()
            itemView.tvPurchaseOn.text = item?.createdOn
            itemView.tvPayAmount.text = item?.amount + " JPY"
            itemView.tvPayBy.text = item?.paymentMethod

            var books: List<SWBookInfoResponse>? = null
            books = item?.products
            val adapter = SWPurchaseHistoryChildAdapter(books)
            val manager: RecyclerView.LayoutManager = LinearLayoutManager(itemView.context)
            itemView.rcBook.layoutManager = manager
            itemView.rcBook.itemAnimator = DefaultItemAnimator()
            itemView.rcBook.adapter = adapter

            adapter.onCallBackClicked(this)
        }

        override fun onWriteReviewListener(id: Int?) {
            listener?.onWriteReviewListener(id)
        }

        override fun onReadReviewListener(id: Int?) {
            listener?.onReadReviewListener(id)
        }

        override fun onReadNowListener(id: Int?) {
            listener?.onReadNowListener(id)
        }

        override fun onDetailListener(id: Int?) {
            listener?.onDetailListener(id)
        }
    }

}