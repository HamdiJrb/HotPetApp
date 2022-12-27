package com.example.hotpet.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hotpet.R
import com.example.hotpet.utils.Constants


class ImageAdapter(private var items: ArrayList<String>, private var showMenu: (String) -> Unit) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_image, parent, false)
        return ImageViewHolder(view, deleteItem)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bindView(items[position], showMenu)
    }

    override fun getItemCount(): Int = items.size

    private val deleteItem = fun(image: String) {
        items.remove(image)
        this.notifyDataSetChanged()
    }

    class ImageViewHolder(view: View, val deleteFunction: (String) -> Unit) :
        RecyclerView.ViewHolder(view) {

        // VIEW BINDING
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bindView(image: String, showMenu: (String) -> Unit) {

            itemView.setOnClickListener { showMenu(image) }

            Glide.with(itemView.context)
                .load(Constants.BASE_URL_IMAGES + image)
                .into(imageView)
        }
    }
}