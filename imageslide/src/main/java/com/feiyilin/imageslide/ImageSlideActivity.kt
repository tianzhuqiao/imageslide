package com.feiyilin.imageslide

import androidx.appcompat.app.AppCompatActivity

open class ImageSlideActivity : AppCompatActivity() {

    var imageSlideFragment: ImageSlideFragment? = null

    fun initImageSlideFragment(id: Int, tag: String = "image_slide") {
        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            val fragment = ImageSlideFragment()
            supportFragmentManager
                .beginTransaction()
                .add(id, fragment, tag)
                .hide(fragment)
                .commit()
            imageSlideFragment = fragment

        } else {
            imageSlideFragment = supportFragmentManager.findFragmentByTag(tag) as? ImageSlideFragment
        }
        imageSlideFragment?.setCallBack(imageSlideCallback)
        imageSlideFragment?.close()
        supportFragmentManager.executePendingTransactions()

    }

    open val imageSlideCallback: ImageSlideCallBack? = null
}