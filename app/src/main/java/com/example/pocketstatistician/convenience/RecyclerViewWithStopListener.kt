package com.example.pocketstatistician.convenience

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewWithStopListener: RecyclerView {

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr)

    init {
        initListener()
    }

    interface OnScrollStoppedListener {
        fun onScrollStopped()
    }

    private val NEW_CHECK_INTERVAL = 100
    private var mOnScrollStoppedListener: OnScrollStoppedListener? = null
    private var mScrollerTask: Runnable? = null
    private var mInitialPosition = 0
    var isHorizontal = true

    private fun initListener() {
        mScrollerTask = Runnable {
            val newPosition = getScroll()
            if (mInitialPosition - newPosition == 0) { //has stopped
                mOnScrollStoppedListener?.onScrollStopped()
            } else {
                startScrollerTask()
            }
        }
    }

    fun setOnScrollStoppedListener(listener: OnScrollStoppedListener) {
        mOnScrollStoppedListener = listener
    }

    fun startScrollerTask() {
        mInitialPosition = getScroll()
        postDelayed(mScrollerTask, NEW_CHECK_INTERVAL.toLong())
    }

    fun stopScrollTask() {
        removeCallbacks(mScrollerTask)
    }

    fun getScroll(): Int {
        return if (isHorizontal) scrollX
            else scrollY
    }

}