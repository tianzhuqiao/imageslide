package com.feiyilin.imageslide

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper

class PullFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr) {
    private val dragger: ViewDragHelper
    private val minimumFlingVelocity: Int
    var callback: Callback? = null

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