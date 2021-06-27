package com.sanwashoseki.bookskozuchi.business.ebookrequest.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.ebookrequest.models.responses.SWRequestBookContentLibraryResponse
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.recycle_item_request_book.view.*

@Suppress("UNREACHABLE_CODE")
class SWRequestBookContentAdapter(private var replys: List<SWRequestBookContentLibraryResponse.Data.BookRequestPosts>?) :
    RecyclerView.Adapter<SWRequestBookContentAdapter.ViewHolder>() {

    private var listener: OnItemClickListener? = null
    private var adapter: SWImageRequestBookAdapter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycle_item_request_book, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return replys!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val holderBookStore: ViewHolder = holder
        holderBookStore.binItems(replys!![position])
        holder.itemView.width

        if (position == replys?.size?.minus(1)) {
            holder.itemView.view.visibility = View.GONE
        }

    }

    fun setOnCallBackListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onDetailRequestListener(id: Int?)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(reply: SWRequestBookContentLibraryResponse.Data.BookRequestPosts?) {
            itemView.tvUserName.text = reply?.customer?.nameFormated
            itemView.tvUserEmail.text = reply?.customer?.email
            itemView.tvDate.text = reply?.createdOnUtcFormated
            itemView.tvContent.text = reply?.text

            Glide.get(itemView.context).clearMemory()
            if (reply?.customer?.avatarUrl != "") {
                Glide.with(itemView.context)
                    .load(reply?.customer?.avatarUrl)
                    .into(itemView.imgAvatar)
            } else {
                Glide.with(itemView.context)
                    .load(R.drawable.ic_avatar_not_login)
                    .into(itemView.imgAvatar)
            }

            itemView.layoutReply.visibility = View.GONE
            itemView.tvRequestTitle.visibility = View.GONE
            itemView.tvAuthor.visibility = View.GONE

            if (reply?.picture != null) {
                val urls = ArrayList<String>()
                urls.add(reply.picture?.imageUrl.toString())

                adapter = SWImageRequestBookAdapter(urls)
                val manager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                itemView.rcImage.layoutManager = manager
                itemView.rcImage.itemAnimator = DefaultItemAnimator()
                itemView.rcImage.adapter = adapter
            } else {
                itemView.rcImage.visibility = View.GONE
            }
        }
    }

}