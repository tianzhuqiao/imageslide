package com.feiyilin.example

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView

import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

interface OnImageItemClickListener {
    fun onItemImageClick(index: Int)
}

class ImageListAdapter(
    private var images: List<Int>,
    private var listener: OnImageItemClickListener? = null
)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ImageViewHolder(
            inflater,
            R.layout.image_item,
            parent
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val m = images[position]
        when(holder) {
            is ImageViewHolder -> holder.bind(m, position, listener)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    fun setImages(images: List<Int>) {
        this.images = images
        notifyDataSetChanged()
    }
}

class ImageViewHolder(inflater: LayoutInflater, resource:Int, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(resource, parent, false)) {
    private var msgView: ImageView? = null

    init {
        msgView = itemView.findViewById(R.id.image_view)
    }


    fun bind(image: Int, index: Int, listener: OnImageItemClickListener?) {
        msgView?.setOnClickListener { listener?.onItemImageClick(index) }
        msgView?.let {
            Picasso.get().load(image).noFade()
                .placeholder(R.drawable.ic_imageslide_placeholder)
                .error(R.drawable.ic_imageslide_placeholder)
                .fit()
                .centerInside()
                .into(it)
        }
    }
}