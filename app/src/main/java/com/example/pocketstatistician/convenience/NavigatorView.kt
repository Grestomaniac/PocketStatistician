package com.example.pocketstatistician.convenience

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout

class NavigatorView(context: Context, attributeSet: AttributeSet): LinearLayout(context, attributeSet) {
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        dispatchGenericMotionEvent(ev)
        log("intercepting ${ev?.actionMasked}")
        return super.onInterceptTouchEvent(ev)
    }
}