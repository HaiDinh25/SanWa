package com.sanwashoseki.bookskozuchi.business.notifications.adapters

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.notifications.models.responses.SWNotificationResponse
import kotlinx.android.synthetic.main.recycle_item_notification.view.*

@Suppress("UNREACHABLE_CODE")
class SWNotificationAdapter(private var notifications: List<SWNotificationResponse.Data>?) :
    RecyclerView.Adapter<SWNotificationAdapter.ViewHolder>() {

    private var listener: OnItemClickedListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SWNotificationAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycle_item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notifications!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val holderBookStore: ViewHolder = holder
        holderBookStore.binItems(notifications!![position])
        holder.itemView.width

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n", "UseCompatLoadingForColorStateLists")
        fun binItems(item: SWNotificationResponse.Data) {
            if (item.isRead == true) {
                itemView.setBackgroundResource(R.color.colorPrimary)
            } else {
                itemView.setBackgroundResource(R.color.colorNotAvailable)
            }

            itemView.tvTitle.text = item.title
            itemView.tvContent.text = item.body
            itemView.tvDate.text = item.createdDateFormated

            itemView.setOnClickListener {
                listener?.onClickedListener(item)
            }
        }
    }

    fun onCallbackListener(listener: OnItemClickedListener) {
        this.listener = listener
    }

    interface OnItemClickedListener {
        fun onClickedListener(detail: SWNotificationResponse.Data?)
    }

}