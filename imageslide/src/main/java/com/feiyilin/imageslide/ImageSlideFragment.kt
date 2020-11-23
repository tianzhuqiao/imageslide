package com.feiyilin.imageslide

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.asksira.loopingviewpager.LoopingPagerAdapter
import com.ortiz.touchview.TouchImageView
import kotlinx.android.synthetic.main.imageslide_fragment.*

interface ImageSlideCallBack {
    fun onImageSlideHide(hide: Boolean) {}
    fun onImageSlideLongClick(image: ImageSlideItem, index: Int) {}
    fun onImageSlideSelected(image: ImageSlideItem, index: Int) {}
}

open class ImageSlideFragment : Fragment() {

    protected var systemUiVisibility: Int? = null

    protected var imageSlideCallback: ImageSlideCallBack? = null
    var _images = listOf<ImageSlideItem>()
    var images: List<ImageSlideItem>
        get() = _images
        set(value) {
                this._images = value
                adapter?.setImages(value)
                pageIndicatorView?.count = viewPager?.indicatorCount ?: 0
            }

    protected val adapter: ImageViewAdapter?
        get() {
            return viewPager?.adapter as? ImageViewAdapter
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.imageslide_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

        imageSlideCloseBtn.setOnClickListener {
            close()
        }
    }

    fun close() {
        this.activity?.supportFragmentManager?.beginTransaction()?.hide(this)?.commit()
    }

    fun setSelected(index: Int) {
        if (0 <= index && index < this.images.count()) {
            viewPager?.setCurrentItem(index + 1, true)
        }
    }

    protected fun initViews() {
        viewPagePuller.callback = onPullFrameCallback
        viewPager?.adapter = ImageViewAdapter(this.requireContext(), images, true)

        adapter?.setCallBack(imageSlideCallback)
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
                    imageSlideCallback?.onImageSlideSelected(this@ImageSlideFragment.images[index], index)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    protected var onPullFrameCallback = object : PullFrameLayout.Callback {
        // pull frame layout callback
        override fun onPullEnabled(): Boolean {
            val view =
                viewPager?.findViewWithTag("slide_" + (pageIndicatorView?.selection ?: 0)) as? View
            if (view != null) {
                // disable pull down to close if current image is in zoomed state
                val imgView = view.findViewById(R.id.image_view) as? TouchImageView
                return !(imgView?.isZoomed ?: false)
            }
            return true
        }

        override fun onPullStart() {
            pageIndicatorView.visibility = View.GONE
            imageSlideCloseBtn.visibility = View.GONE
        }

        override fun onPull(var1: Float) {
            image_slide_top.alpha = 1 - var1
        }

        override fun onPullCancel() {
            pageIndicatorView.visibility = View.VISIBLE
            imageSlideCloseBtn.visibility = View.VISIBLE
        }

        override fun onPullComplete() {
            close()
        }
    }

    fun setCallBack(callback: ImageSlideCallBack?) {
        this.imageSlideCallback = callback
        adapter?.setCallBack(callback)
    }

    fun setFullscreen(fullscreen: Boolean) {
        var flags = systemUiVisibility
        if (fullscreen) {
            flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
        if (flags != null) {
            activity?.window?.decorView?.systemUiVisibility = flags
        }
    }

     protected fun onImageSlideHide(hide: Boolean) {
         setFullscreen(!hide)
         if (hide) {
             activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
         } else {
             activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
         }
         imageSlideCallback?.onImageSlideHide(hide)
     }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if(!hidden) {
            // save the flag for later
            systemUiVisibility = activity?.window?.decorView?.systemUiVisibility
        }

        onImageSlideHide(hidden)

        if (!hidden) {
            // reset views
            viewPager?.top = 0
            image_slide_top?.alpha = 1.0f
            pageIndicatorView.visibility = View.VISIBLE
            imageSlideCloseBtn.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        if (this.isVisible) {
            onImageSlideHide(false)
        }
    }

    fun show(images: List<ImageSlideItem>, index: Int) {
        this.images = images
        setSelected(index)
        activity?.let {
            it.supportFragmentManager.beginTransaction().show(this).commit()
        }
    }
}


class ImageViewAdapter(context: Context, itemList: List<ImageSlideItem>, isInfinite: Boolean) :
    LoopingPagerAdapter<ImageSlideItem>(context, itemList, isInfinite) {

    private var callback: ImageSlideCallBack? = null
    fun setCallBack(callback: ImageSlideCallBack?) {
        this.callback = callback
    }

    //This method will be triggered if the item View has not been inflated before.
    override fun inflateView(viewType: Int, container: ViewGroup, listPosition: Int): View {
        return LayoutInflater.from(context).inflate(R.layout.imageslide_page, container, false)
    }

    override fun bindView(convertView: View, listPosition: Int, viewType: Int) {
        convertView.tag = "slide_$listPosition"
        val view = convertView.findViewById(R.id.image_view) as? ImageView

        itemList?.get(listPosition)?.let { image ->
            image.load(view)

            view?.setOnLongClickListener {
                callback?.onImageSlideLongClick(image, listPosition)
                true
            }
        }
    }

    fun setImages(images: List<ImageSlideItem>) {
        this.itemList = images
        notifyDataSetChanged()
    }
}