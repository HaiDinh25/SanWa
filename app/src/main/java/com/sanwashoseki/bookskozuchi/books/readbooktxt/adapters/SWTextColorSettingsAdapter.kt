package com.sanwashoseki.bookskozuchi.books.readbooktxt.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.databinding.ItemColorViewBinding
import kotlinx.android.synthetic.main.item_color_view.view.*

class SWTextColorSettingsAdapter(var list: MutableList<Int>, val callback: Callback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface Callback {
        fun onColorClicked(color: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemColorViewBinding>(inflater, R.layout.item_color_view, parent, false)

        return RecyclerViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val view = holder.itemView
        val item = list[position]

        view.btnColor.setBackground(item)

        view.setOnClickListener {
            Log.d("TAG", "onBindViewHolder: ${list.size}    $position")
            if (view.btnColor.getChecked()) {
                view.btnColor.setChecked(false)
            } else {
                view.btnColor.setChecked(true)
            }
            callback.onColorClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class RecyclerViewHolder(val binding: ItemColorViewBinding) :
        RecyclerView.ViewHolder(binding.root)

}
