package com.sanwashoseki.bookskozuchi.books.readbooktxt.adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWFontsManager
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.models.SWTextFontSettingModel
import kotlinx.android.synthetic.main.item_font_view.view.*


class SWTextFontSettingsAdapter(
    var data: MutableList<SWTextFontSettingModel>,
    val callback: Callback,
) :
    RecyclerView.Adapter<SWTextFontSettingsAdapter.RecyclerViewHolder>() {

    interface Callback {
        fun onFontClicked(fontSettingModel: SWTextFontSettingModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_font_view, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val view = holder.itemView
        val item = data[position]

        view.tvFontPreview.text = "日本"
        view.tvFontName.text = data[position].name

        val typeface = SWFontsManager.instance.getTypeface(data[position].name)
        view.tvFontPreview.typeface = typeface
        view.tvFontName.typeface = typeface

        view.tvFontPreview.backgroundTintList = view.context.getColorStateList(if (item.isSelected) R.color.orange else R.color.white)
        view.tvFontPreview.setTextColor(view.context.resources.getColor(if (item.isSelected) R.color.white else R.color.gray))

        view.setOnClickListener {
            for (i in 0 until data.size) {
                data[i].isSelected = i == position
            }
            callback.onFontClicked(data[position])
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class RecyclerViewHolder(itemView: View) : ViewHolder(itemView)
}