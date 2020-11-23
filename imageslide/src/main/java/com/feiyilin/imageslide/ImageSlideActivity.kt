package com.feiyilin.imageslide

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

open class ImageSlideActivity : AppCompatActivity() {

    protected val imageSlideFragment: ImageSlideFragment = ImageSlideFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun initImageSlideFragment(id: Int) {
        if (supportFragmentManager.findFragmentByTag("image_slide") == null) {
            supportFragmentManager
                .beginTransaction()
                .add(id, imageSlideFragment, "image_slide")
                .hide(imageSlideFragment)
                .commit()
            imageSlideFragment.setCallBack(imageSlideCallback)
        }
        supportFragmentManager.executePendingTransactions()
    }


    fun showImageSlide(images: ArrayList<ImageSlideItem>, index: Int) {
        imageSlideFragment.setImages(images)
        imageSlideFragment.setSelected(index)
        this.supportFragmentManager.beginTransaction().show(imageSlideFragment)
            .commit()
    }

    open val imageSlideCallback : ImageSlideCallBack? = null
}