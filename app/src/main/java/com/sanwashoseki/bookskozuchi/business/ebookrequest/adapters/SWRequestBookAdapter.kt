package com.sanwashoseki.bookskozuchi.business.ebookrequest.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.ebookrequest.models.responses.SWRequestBookLibraryResponse
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.recycle_item_request_book.view.*

@Suppress("UNREACHABLE_CODE")
class SWRequestBookAdapter(
    private var context: Context?,
    private var requests: List<SWRequestBookLibraryResponse.Data>?,
    private var isMore: Boolean?,
) : RecyclerView.Adapter<SWRequestBookAdapter.ViewHolder>() {

    private var listener: OnItemClickListener? = null
    private var adapter: SWImageRequestBookAdapter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycle_item_request_book, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return requests!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val holderBookStore: ViewHolder = holder
        holderBookStore.binItems(requests!![position])
        holder.itemView.width

        if (position == requests?.size?.minus(1)) {
            holder.itemView.view.visibility = View.GONE
        }
    }

    fun setOnCallBackListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onDetailRequestListener(id: Int?)

        fun onDeleteRequestListener(id: Int?)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(request: SWRequestBookLibraryResponse.Data?) {
            itemView.tvUserName.text = request?.customer?.nameFormated
            itemView.tvUserEmail.text = request?.customer?.email
            itemView.tvDate.text = request?.createdOnUtcFormated
            itemView.tvRequestTitle.text = context?.getString(R.string.book_request_name) + request?.name
            itemView.tvAuthor.text = request?.authorName
            itemView.tvContent.text = request?.body
            itemView.tvNumRep.text = request?.numPosts.toString()
            itemView.tvStatus.text = request?.status

            when(request?.statusId) {
                0 -> itemView.tvStatus.setBackgroundResource(R.drawable.bg_text_view_status_pending)
                1 -> itemView.tvStatus.setBackgroundResource(R.drawable.bg_text_view_status_approved)
                2 -> itemView.tvStatus.setBackgroundResource(R.drawable.bg_text_view_status_rejected)
                3 -> itemView.tvStatus.setBackgroundResource(R.drawable.bg_text_view_status_updated)
            }

            if (request?.body == null || request.body == "") {
                itemView.tvContent.visibility = View.GONE
            } else {
                itemView.tvContent.visibility = View.VISIBLE
            }

            if (request?.authorName == null || request.authorName == "") {
                itemView.tvAuthor.visibility = View.GONE
            } else {
                itemView.tvAuthor.visibility = View.VISIBLE
            }

            if (request?.pictures!!.isEmpty()) {
                itemView.rcImage.visibility = View.GONE
            } else {
                itemView.rcImage.visibility = View.VISIBLE
                val urls = ArrayList<String>()
                for (i in request.pictures!!.indices) {
                    urls.add(request.pictures!![i].imageUrl.toString())
                }
                adapter = SWImageRequestBookAdapter(urls)
                val manager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                itemView.rcImage.layoutManager = manager
                itemView.rcImage.itemAnimator = DefaultItemAnimator()
                itemView.rcImage.adapter = adapter
            }

            if (isMore == true) {
                if (request.statusId == 1 || request.statusId == 3) {
                    itemView.btnDelete.visibility = View.GONE
                    itemView.btnReply.visibility = View.VISIBLE
                    itemView.tvNumRep.visibility = View.VISIBLE
                } else {
                    itemView.btnReply.visibility = View.GONE
                    itemView.tvNumRep.visibility = View.GONE
                    if (request.statusId == 2) {
                        itemView.tvNote.visibility = View.VISIBLE
                        itemView.tvNote.text = request.note
                    } else {
                        itemView.tvNote.visibility = View.GONE
                    }
                    itemView.btnDelete.visibility = View.VISIBLE
                    itemView.btnDelete.setOnClickListener { listener?.onDeleteRequestListener(request.id) }
                }
            }

            Glide.get(itemView.context).clearMemory()
            if (request.customer?.avatarUrl != "") {
                Glide.with(itemView.context)
                    .load(request.customer?.avatarUrl)
                    .into(itemView.imgAvatar)
            } else {
                Glide.with(itemView.context)
                    .load(R.drawable.ic_avatar_not_login)
                    .into(itemView.imgAvatar)
            }

            itemView.btnReply.setOnClickListener { listener?.onDetailRequestListener(request.id) }
            itemView.tvRequestTitle.setOnClickListener { listener?.onDetailRequestListener(request.id) }
        }
    }

}