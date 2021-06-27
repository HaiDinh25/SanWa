package com.sanwashoseki.bookskozuchi.business.ebookrequest.adapters

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import kotlinx.android.synthetic.main.recycle_item_image_request_book.view.*

@Suppress("UNREACHABLE_CODE")
class SWImageRequestBookAdapter2(private var uris: List<Uri>?) :
    RecyclerView.Adapter<SWImageRequestBookAdapter2.ViewHolder>() {

    private var listener: OnRemoveImageListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycle_item_image_request_book, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return uris!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val holderBookStore: ViewHolder = holder
        holderBookStore.binItems(uris!![position])
        holder.itemView.width
    }

    fun onCallBackListener(listener: OnRemoveImageListener) {
        this.listener = listener
    }

    interface OnRemoveImageListener {
        fun onRemoveImageListener(uri: Uri?)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(uri: Uri?) {
            itemView.imageView.setImageURI(uri)

            itemView.btnRemove.visibility = View.VISIBLE
            itemView.btnRemove.setOnClickListener { listener?.onRemoveImageListener(uri) }
        }
    }

}