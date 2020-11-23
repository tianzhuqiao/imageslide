package com.feiyilin.imageslide

import android.widget.ImageView
import com.squareup.picasso.Picasso

open class ImageSlideItem {
    var placeholder: Int = R.drawable.ic_imageslide_placeholder
    var error: Int = R.drawable.ic_imageslide_placeholder

    open fun load(view: ImageView?) {
    }
}

fun <T : ImageSlideItem> T.placeholder(res: Int) = apply {
    this.placeholder = res
}

fun <T : ImageSlideItem> T.error(res: Int) = apply {
    this.error = res
}

open class ImageSlideResItem: ImageSlideItem() {
    var image: Int = 0

    override fun load(view: ImageView?) {
        super.load(view)
        if (view == null) {
            return
        }
        try {
            Picasso.get().cancelRequest(view)
            Picasso.get().load(image).noFade()
                .placeholder(placeholder)
                .error(error)
                .fit()
                .centerInside()
                .into(view)

        } catch (e: Exception) {
        }
    }
}

fun <T : ImageSlideResItem> T.image(res: Int) = apply {
    this.image = res
}

open class ImageSlidePathItem: ImageSlideItem() {
    var image: String = ""

    override fun load(view: ImageView?) {
        super.load(view)
        if (view == null) {
            return
        }
        try {
            Picasso.get().cancelRequest(view)
            Picasso.get().load(image).noFade()
                .placeholder(placeholder)
                .error(error)
                .fit()
                .centerInside()
                .into(view)

        } catch (e: Exception) {
        }
    }
}

fun <T : ImageSlidePathItem> T.image(path: String) = apply {
    this.image = path
}
