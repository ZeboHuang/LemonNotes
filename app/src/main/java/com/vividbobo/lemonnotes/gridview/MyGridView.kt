package com.vividbobo.lemonnotes.gridview

import android.content.Context
import android.util.AttributeSet
import android.widget.GridView


/**
 * @author: vibrantBobo
 * @date: 2021/9/20
 * @description:
 */

class MyGridView(context: Context?, attrs: AttributeSet?) : GridView(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //解决加载不完全
        val height = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, height)
    }
}