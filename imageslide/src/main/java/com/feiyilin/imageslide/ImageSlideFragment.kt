package com.feiyilin.imageslide

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.asksira.loopingviewpager.LoopingPagerAdapter
import com.ortiz.touchview.TouchImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_image_slide.*
import kotlin.collections.ArrayList


class ImageSlideItem {
    var resId: Int = 0
    var path: String = ""
    var placeholder: Int = 0
    var error: Int = 0
}

class PullFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr) {
    private val dragger: ViewDragHelper
    private val minimumFlingVelocity: Int
    private var callback: Callback? = null

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return dragger.shouldInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragger.processTouchEvent(event)
        return true
    }

    override fun computeScroll() {
        if (dragger.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    private inner class ViewDragCallback : ViewDragHelper.Callback() {
        override fun tryCaptureView(
            child: View,
            pointerId: Int
        ): Boolean {
            return callback?.onPullEnabled() ?: false
        }

        override fun clampViewPositionHorizontal(
            child: View,
            left: Int,
            dx: Int
        ): Int {
            return 0
        }

        override fun clampViewPositionVertical(
            child: View,
            top: Int,
            dy: Int
        ): Int {
            return 0.coerceAtLeast(top)
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return 0
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return this@PullFrameLayout.height
        }

        override fun onViewCaptured(
            capturedChild: View,
            activePointerId: Int
        ) {
            if (callback != null) {
                callback!!.onPullStart()
            }
        }

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
            if (callback != null) {
                callback!!.onPull(top.toFloat() / this@PullFrameLayout.height.toFloat())
            }
        }

        override fun onViewReleased(
            releasedChild: View,
            xvel: Float,
            yvel: Float
        ) {
            val slop =
                if (yvel > minimumFlingVelocity.toFloat()) this@PullFrameLayout.height / 6 else this@PullFrameLayout.height / 3
            if (releasedChild.top > slop) {
                if (callback != null) {
                    callback!!.onPullComplete()
                }
            } else {
                if (callback != null) {
                    callback!!.onPullCancel()
                }
                dragger.settleCapturedViewAt(0, 0)
                this@PullFrameLayout.invalidate()
            }
        }
    }

    interface Callback {
        fun onPullEnabled(): Boolean
        fun onPullStart()
        fun onPull(var1: Float)
        fun onPullCancel()
        fun onPullComplete()
    }

    init {
        dragger = ViewDragHelper.create(
            this,
            0.125f,
            ViewDragCallback()
        )
        minimumFlingVelocity = ViewConfiguration.get(context).scaledMinimumFlingVelocity
    }
}

open class ImageSlideFragment : Fragment(), PullFrameLayout.Callback {
    interface ImageSlideCallBack {
        fun onImageSlideHide(hide: Boolean)
        fun onImageSlideLongClick(image: ImageSlideItem, index: Int)
        fun onImageSlideSelected(image: ImageSlideItem, index: Int)
    }

    private var callback: ImageSlideCallBack? = null
    private var images = arrayListOf<ImageSlideItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_slide, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

        slide_close_btn.setOnClickListener {
            this.activity?.supportFragmentManager?.beginTransaction()?.hide(this)?.commit()
        }
    }

    fun setImages(images: ArrayList<ImageSlideItem>) {
        this.images = images
        val adapter = viewPager?.adapter as? ImageViewAdapter
        adapter?.setImages(images)
        pageIndicatorView?.count = viewPager?.indicatorCount ?: 0
    }

    fun setSelected(index: Int) {
        if (0 <= index && index < this.images.count()) {
            viewPager?.setCurrentItem(index + 1, true)
        }
    }

    private fun initViews() {
        viewPagePuller.setCallback(this)

        viewPager?.adapter = ImageViewAdapter(this.context!!, images, true)
        val adapt = viewPager?.adapter as? ImageViewAdapter
        adapt?.setCallBack(callback)
        pageIndicatorView?.count = viewPager?.indicatorCount ?: 0
        viewPager?.onIndicatorProgress = { selectingPosition, progress ->
            pageIndicatorView?.onPageScrolled(selectingPosition, progress, 0)
        }

        viewPager?.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position > 0 && position <= this@ImageSlideFragment.images.size) {
                    val index = position - 1
                    callback?.onImageSlideSelected(this@ImageSlideFragment.images[index], index)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun onPullEnabled(): Boolean {
        val view =
            viewPager?.findViewWithTag("slide_" + (pageIndicatorView?.selection ?: 0)) as? View
        if (view != null) {
            val imgView = view.findViewById(R.id.image_view) as? TouchImageView
            return !(imgView?.isZoomed ?: false)
        }
        return true
    }

    override fun onPullStart() {
        pageIndicatorView.visibility = View.GONE
        slide_close_btn.visibility = View.GONE
    }

    override fun onPull(var1: Float) {
        image_slide_top.alpha = 1 - var1
    }

    override fun onPullCancel() {
        pageIndicatorView.visibility = View.VISIBLE
        slide_close_btn.visibility = View.VISIBLE
    }

    override fun onPullComplete() {
        this.activity?.supportFragmentManager?.beginTransaction()?.hide(this)?.commit()
    }

    fun setCallBack(callback: ImageSlideCallBack?) {
        this.callback = callback
        val adapt = viewPager?.adapter as? ImageViewAdapter
        adapt?.setCallBack(callback)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        callback?.onImageSlideHide(hidden)

        if (!hidden) {
            // reset views
            viewPager?.top = 0
            image_slide_top?.alpha = 1.0f
            pageIndicatorView.visibility = View.VISIBLE
            slide_close_btn.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        if (this.isVisible) {
            callback?.onImageSlideHide(false)
        }
    }
}


class ImageViewAdapter(context: Context, itemList: ArrayList<ImageSlideItem>, isInfinite: Boolean) :
    LoopingPagerAdapter<ImageSlideItem>(context, itemList, isInfinite) {

    private var callback: ImageSlideFragment.ImageSlideCallBack? = null
    fun setCallBack(callback: ImageSlideFragment.ImageSlideCallBack?) {
        this.callback = callback
    }

    //This method will be triggered if the item View has not been inflated before.
    override fun inflateView(viewType: Int, container: ViewGroup, listPosition: Int): View {
        return LayoutInflater.from(context).inflate(R.layout.image_slide_page, container, false)
    }

    override fun bindView(convertView: View, listPosition: Int, viewType: Int) {
        convertView.tag = "slide_$listPosition"
        val view = convertView.findViewById(R.id.image_view) as ImageView

        itemList?.get(listPosition)?.let { image ->

            view.loadImageItem(image)

            view.setOnLongClickListener {
                callback?.onImageSlideLongClick(image, listPosition)
                true
            }
        }
    }

    fun setImages(images: ArrayList<ImageSlideItem>) {
        this.itemList = images
        notifyDataSetChanged()
    }
}

fun ImageView.loadImageItem(image: ImageSlideItem) {
    var placeholder = image.placeholder
    if (placeholder == 0) {
        placeholder = R.drawable.default_image_placeholder
    }

    var error = image.error
    if (error == 0) {
        error = R.drawable.default_image_placeholder
    }

    try {
        Picasso.get().cancelRequest(this)
        if (image.path.isNotEmpty()) {
            Picasso.get().load(image.path).noFade()
                .placeholder(placeholder)
                .error(error)
                .fit()
                .centerInside()
                .into(this)
        } else {
            Picasso.get().load(image.resId).noFade()
                .placeholder(placeholder)
                .error(error)
                .fit()
                .centerInside()
                .into(this)
        }
    } catch (e: Exception) {
    }
}


open class ImageSlideActivity : AppCompatActivity(), ImageSlideFragment.ImageSlideCallBack {

    protected val imageSlideFragment: ImageSlideFragment = ImageSlideFragment()
    protected var systemUiVisibility: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        systemUiVisibility = this.window.decorView.systemUiVisibility
    }

    fun initImageSlideFragment(id: Int) {
        if (supportFragmentManager.findFragmentByTag("image_slide") == null) {
            supportFragmentManager
                .beginTransaction()
                .add(id, imageSlideFragment, "image_slide")
                .hide(imageSlideFragment)
                .commit()
            imageSlideFragment.setCallBack(this)
        }
        supportFragmentManager.executePendingTransactions()
    }

    fun setFullscreen(fullscreen: Boolean) {
        var flags = systemUiVisibility
        if (fullscreen) {
            flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
        this.window.decorView.systemUiVisibility = flags
    }

    fun showImageSlide(images: ArrayList<ImageSlideItem>, index: Int) {
        imageSlideFragment.setImages(images)
        imageSlideFragment.setSelected(index)
        this.supportFragmentManager.beginTransaction().show(imageSlideFragment)
            .commit()
    }

    override fun onImageSlideHide(hide: Boolean) {
        if (hide) {
            this.setFullscreen(false)
            this.window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        } else {
            this.setFullscreen(true)
            this.window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    override fun onImageSlideLongClick(image: ImageSlideItem, index: Int) {
    }

    override fun onImageSlideSelected(image: ImageSlideItem, index: Int) {
    }
}