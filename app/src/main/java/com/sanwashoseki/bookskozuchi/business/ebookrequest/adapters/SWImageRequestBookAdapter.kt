package com.sanwashoseki.bookskozuchi.business.ebookrequest.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.business.ebookrequest.dialogs.SWDialogViewImage
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.recycle_item_image_request_book.view.*


@Suppress("UNREACHABLE_CODE")
class SWImageRequestBookAdapter(private var urls: List<String>?) : RecyclerView.Adapter<SWImageRequestBookAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_item_image_request_book,
            parent,
            false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return urls!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val holderBookStore: ViewHolder = holder
        holderBookStore.binItems(urls!![position])
        holder.itemView.width
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun binItems(url: String?) {
            Glide.get(itemView.context).clearMemory()
            Glide.with(itemView.context)
                .load(url)
                .into(itemView.imageView)

            itemView.imageView.setOnClickListener {
                val dialog = SWDialogViewImage(itemView.context, url)
                dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
                dialog.show()
            }
        }
    }

}