package com.example.pocketstatistician.convenience

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.example.pocketstatistician.R


class LimitedView(context: Context, attributeSet: AttributeSet): LinearLayout(context, attributeSet) {

    var a = context.obtainStyledAttributes(attributeSet, R.styleable.LimitedView)
    var maxHeight: Int = a.getLayoutDimension(R.styleable.LimitedView_maxHeight, 100)
        set(value) {
            convertToPx(value, context)
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var newHeightMeasureSpec = 0
        if (maxHeight > 0) {
            val hSize = MeasureSpec.getSize(heightMeasureSpec)
            val hMode = MeasureSpec.getMode(heightMeasureSpec)
            when (hMode) {
                MeasureSpec.AT_MOST -> newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    Math.min(hSize, maxHeight),
                    MeasureSpec.AT_MOST
                )
                MeasureSpec.UNSPECIFIED -> newHeightMeasureSpec =
                    MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST)
                MeasureSpec.EXACTLY -> newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    Math.min(hSize, maxHeight),
                    MeasureSpec.EXACTLY
                )
            }
        }

        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec)
    }
}