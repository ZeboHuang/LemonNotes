package com.vividbobo.lemonnotes.entity

import android.graphics.Typeface
import android.text.Spannable
import android.text.style.*
import javax.net.ssl.SSLContext


/**
 * @param type 样式的种类，字体加粗，斜体，或是 图片。。
 * @param start end 起点与重点
 * @param flags 对应setSpan的flags，是() [) (] []
 * */
class Span(
    val type: Int = NORMAL,
    val start: Int,
    val end: Int,
    val flags: Int = Spannable.SPAN_EXCLUSIVE_INCLUSIVE
) {
    //spanType
    companion object {
        val NORMAL = 0    //没有任何样式效果
        val BOLD = 1
        val ITALIC = 2
        val UNDERLINE = 3
        val STRIKETHROUGHLINE = 4
        val FONT_SIZE_DEFAULT = 5
        val FONT_SIZE_BIGGER = 6
        val FONT_SIZE_SMALLER = 7
        val IMAGE = 8


        val VALUE_FONT_BIGGER = 2f
        val VALUE_FONT_SMALLER = 0.5f

        fun getSpanType(spanStyle: CharacterStyle) = when (spanStyle) {
            is StyleSpan -> {
                if (spanStyle.style == Typeface.BOLD)
                    BOLD
                else        //这里只有两种样式，所以直接用else了
                    ITALIC
            }
            is UnderlineSpan -> UNDERLINE
            is StrikethroughSpan -> STRIKETHROUGHLINE
            is RelativeSizeSpan -> {
//                if(spanStyle.sizeChange)
                //这里还需要做一下判断，后面看要做几种字体大小，再说吧
                FONT_SIZE_DEFAULT
            }
            is ImageSpan -> IMAGE
            else -> NORMAL
        }

        fun getSpanStyle(type: Int): CharacterStyle {
            return when (type) {
                BOLD -> StyleSpan(Typeface.BOLD)
                ITALIC -> StyleSpan(Typeface.ITALIC)
                UNDERLINE -> UnderlineSpan()
                STRIKETHROUGHLINE -> StrikethroughSpan()
                FONT_SIZE_DEFAULT -> RelativeSizeSpan(1f)
                FONT_SIZE_SMALLER -> RelativeSizeSpan(VALUE_FONT_SMALLER)
                FONT_SIZE_BIGGER -> RelativeSizeSpan(VALUE_FONT_BIGGER)
//                IMAGE -> ImageSpan()
                else -> StyleSpan(Typeface.NORMAL)
            }
        }

    }

}