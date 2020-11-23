package com.feiyilin.imageslide

import androidx.appcompat.app.AppCompatActivity

open class ImageSlideActivity : AppCompatActivity() {

    val imageSlideFragment: ImageSlideFragment = ImageSlideFragment()

    fun initImageSlideFragment(id: Int, tag: String = "image_slide") {
        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            supportFragmentManager
                .beginTransaction()
                .add(id, imageSlideFragment, tag)
                .hide(imageSlideFragment)
                .commit()
            imageSlideFragment.setCallBack(imageSlideCallback)
        }
        supportFragmentManager.executePendingTransactions()
    }

    open val imageSlideCallback: ImageSlideCallBack? = null
}