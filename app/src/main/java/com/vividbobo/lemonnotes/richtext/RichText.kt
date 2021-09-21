package com.vividbobo.lemonnotes.richtext

import android.content.Context
import android.graphics.*
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.*
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.vividbobo.lemonnotes.entity.Span
import kotlin.concurrent.thread
import kotlin.math.abs


/**
 * @author vibrantBobo
 * @date 2021/9/18
 * @description 提供简陋的富文本支持
 */
class RichText(context: Context, attrs: AttributeSet?) :
    AppCompatEditText(context, attrs) {

//    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
//        context,
//        attrs,
//        defStyleAttr
//    ) {
//    }


    val TAG = "RichText"

    var is_bold = false
    var is_italic = false
    var is_underline = false
    var is_strikethroughline = false


//    interface OnSelectionChangedListener {
//        fun OnSelectionChanged()
//    }
//
//    var mOnSelectionChangedListener: OnSelectionChangedListener? = null
//    fun setOnSelectionChanged(selectionChangedListener: OnSelectionChangedListener) {
//        mOnSelectionChangedListener = selectionChangedListener
//    }


    //记录上次光标所在的位置，避免连续输入时，连续获取isStyle()，影响性能
    //只有当光标变动较大的时候才会 获取 isStyle()
    var old_selStart: Int = 0

    //    移动光标监听，   方便更换上面的图标
//    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
////        mOnSelectionChangedListener?.OnSelectionChanged()
//
//
//        if (abs(old_selStart - selStart) > 1) {
//            is_bold = isStyle<StyleSpan>(selStart, selEnd, Span.BOLD)
//        }
//        old_selStart = selStart
//        super.onSelectionChanged(selStart, selEnd)
//    }

    /**
     * @sample 为选中的字符串进行样式设置，如果没有选中，
     *          选中默认后继吧，看看光标怎么处理
     */
    fun setStyle(start: Int, end: Int, style: CharacterStyle) {
        val spanString = SpannableString(this.text)

        if (start == end) {
            //光标位置 后继，应该要前后都要吧。。。
            spanString.setSpan(style, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        } else {
            spanString.setSpan(style, start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        }

        setText(spanString)
        setSelection(end)       //跳转光标
    }

    /**
     * @param contain 是否包含这个样式 传入 Span.Bold ...
     * @sample 获取选中文字的样式
     */
    inline fun <reified T : CharacterStyle> isStyle(
        start: Int,
        end: Int,
        spanType: Int = 0
    ): Boolean {
        /**
         * 获取选中文字段的所有样式，
         * 先判断该段内是否含有contain 样式，return true， 没有 false
         *  如果Start==end 光标
         *  判断光标所在是否目标span段内
         *  如果光标刚好在span段端点，判断端点处的flags是否由后继
         */
        Log.d(TAG, "isStyle: classType: " + T::class.java.toString())
        val spanString = SpannableString(text)
        val spans: Array<T> =
            spanString.getSpans(start, end, T::class.java)
        Log.d(TAG, "isStyle: spans size: ${spans.size}")

        if (spanType != 0) {
            Log.d(TAG, "isStyle: T is stylespan")
            (spans as Array<StyleSpan>).apply {
                for (span in spans) {
                    Log.d(TAG, "isStyle: span.type: ${span.style}   spanType: $spanType")
                    if (span.style == Typeface.BOLD && spanType == Span.BOLD) {
                        Log.d(TAG, "isStyle: bold")
                        return true
                    }
                    if (span.style == Typeface.ITALIC && spanType == Span.ITALIC) {
                        Log.d(TAG, "isStyle: italic")
                        return true
                    }
                }
            }
        } else if (spans.isNotEmpty()) {
            //除 StyleSpan 外，检测到含有 T 类型的，就包含了
            Log.d(TAG, "isStyle: true")
            return true
        }
        Log.d(TAG, "isStyle: false")
        return false
    }

    fun isBold(): Boolean {
        var start = selectionStart
        var end = selectionEnd
        if (start > end) {
            start = selectionEnd
            end = selectionStart
        }
        return isStyle<StyleSpan>(start, end, Span.BOLD)
    }

    /**
     * @param setBold 设置是否 bold   ture 为Bold  false 为 取消bold
     */
    fun setBold(setBold: Boolean) {
        var start = selectionStart
        var end = selectionEnd
        if (start > end) {
            start = selectionEnd
            end = selectionStart
        }
        if (setBold) {
            setStyle(start, end, StyleSpan(Typeface.BOLD))
        } else {
            removeOneSpan<StyleSpan>(start, end, Span.BOLD)
        }
    }

    fun isItalic(): Boolean {
        var start = selectionStart
        var end = selectionEnd
        if (start > end) {
            start = selectionEnd
            end = selectionStart
        }
        return isStyle<StyleSpan>(start, end, Span.ITALIC)
    }

    fun setItalic(setItalic: Boolean) {
        var start = selectionStart
        var end = selectionEnd
        if (start > end) {
            start = selectionEnd
            end = selectionStart
        }
        if (setItalic) {
            setStyle(start, end, StyleSpan(Typeface.ITALIC))
        } else {
            removeOneSpan<StyleSpan>(start, end, Span.ITALIC)
        }
    }

    fun isUnderline(): Boolean {
        var start = selectionStart
        var end = selectionEnd
        if (start > end) {
            start = selectionEnd
            end = selectionStart
        }
        return isStyle<UnderlineSpan>(start, end)
    }

    fun setUnderline(setUnderline: Boolean) {
        var start = selectionStart
        var end = selectionEnd
        if (start > end) {
            start = selectionEnd
            end = selectionStart
        }
        if (setUnderline) {
            setStyle(start, end, UnderlineSpan())
        } else {
            removeOneSpan<UnderlineSpan>(start, end)
        }
    }

    fun isStrikethroughLine(): Boolean {
        var start = selectionStart
        var end = selectionEnd
        if (start > end) {
            start = selectionEnd
            end = selectionStart
        }
        return isStyle<StrikethroughSpan>(start, end)
    }

    fun setStrikethroughLine(setStrikethroughLine: Boolean) {
        var start = selectionStart
        var end = selectionEnd
        if (start > end) {
            start = selectionEnd
            end = selectionStart
        }
        if (setStrikethroughLine) {
            setStyle(start, end, StrikethroughSpan())
        } else {
            removeOneSpan<StrikethroughSpan>(start, end)
        }
    }

    /**
     * @sample 根据图片地址可以插入本地图片或者网络图片。
     */
    fun setImage(url: String) {
        val spannableString = SpannableString(this.text)
        var start = selectionStart
        var end = selectionEnd
        if (start > end) {
            start = selectionEnd
            end = selectionStart
        }
        getBitmap(url)?.let {
            spannableString.setSpan(
                ImageSpan(context, it),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        Log.d(TAG, "insertImage: " + spannableString.toString())
        this.setText(spannableString)
    }

    private fun getBitmap(url: String): Bitmap? {
        var bitmap: Bitmap? = null
        Glide.with(context)
            .asBitmap()
            .load(url)
            .into(
                object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val screenWidth = context.resources.displayMetrics.widthPixels - 200
                        Log.d(TAG, "Screen pixels width: [$screenWidth]")

                        val imageWidth = resource.width
                        val imageHeight = resource.height
                        //缩放比例
                        val scale = screenWidth.toFloat() / imageWidth.toFloat()
                        val matrix = Matrix()
                        matrix.postScale(scale, scale)
                        bitmap = Bitmap.createBitmap(
                            resource,
                            0,
                            0,
                            imageWidth,
                            imageHeight,
                            matrix,
                            false
                        )
                    }
                }
            )
        return bitmap
    }


    /**
     * @param start
     * @param end  获取的区间段
     * @sample 获取指定区间[start, end]内的样式
     */
    fun getAllSpans(start: Int, end: Int, style: CharacterStyle): ArrayList<Span> {
        val spanString = SpannableString(text)
        val spanList = ArrayList<Span>()

        var currIndex = start
        var nextIndex = start
        while (currIndex < end) {
            nextIndex =
                spanString.nextSpanTransition(currIndex, end, CharacterStyle::class.java)

            val spans: Array<CharacterStyle> =
                spanString.getSpans(currIndex, nextIndex, CharacterStyle::class.java)
            Log.d(TAG, "getAllSpans: [$currIndex, $nextIndex] size: ${spans.size}")
            Log.d(TAG, "getAllSpans:             ---------------------->")
            /**
             *
             */
            for (span in spans) {
                Log.d(
                    TAG,
                    "getAllSpans:  spanclass: ${span.javaClass}    styleClass: ${style.javaClass}"
                )
                if (span.javaClass == style.javaClass) {
                    when (span) {
                        is StyleSpan -> {
                            (style as StyleSpan).apply {
                                if (span.style == Typeface.BOLD && span.style == this.style) {
                                    Log.d(TAG, "getAllSpans: bold")
                                    spanList.add(
                                        Span(
                                            Span.BOLD,
                                            currIndex,
                                            nextIndex
                                        )
                                    )
                                }
                                if (span.style == Typeface.ITALIC && span.style == this.style) {
                                    Log.d(TAG, "getAllSpans: italic")
                                    spanList.add(
                                        Span(
                                            Span.ITALIC,
                                            currIndex,
                                            nextIndex
                                        )
                                    )
                                }
                            }
                        }
                        is UnderlineSpan -> {
                            Log.d(TAG, "getAllSpans: underline")
                            spanList.add(Span(Span.UNDERLINE, currIndex, nextIndex))
                        }
                        is StrikethroughSpan -> {
                            Log.d(TAG, "getAllSpans: strikethroughline")
                            spanList.add(Span(Span.STRIKETHROUGHLINE, currIndex, nextIndex))
                        }
                        is RelativeSizeSpan -> {
                            Log.d(TAG, "getAllSpans: fontSize: ${span.sizeChange}")
                            spanList.add(Span(Span.FONT_SIZE_BIGGER, currIndex, nextIndex))
                        }
                        is ImageSpan -> {
                            Log.d(TAG, "getAllSpans: image")
                        }
                    }
                }
            }
            Log.d(TAG, "getAllSpans: <---------------------")
            Log.d(TAG, "")
            currIndex = nextIndex
        }
        Log.d(TAG, "getAllSpans: spanList size: ${spanList.size}")
        return spanList
    }

    /**
     *@sample 为指定区间重新设置样式
     *  只能先将获取整段text的String，再重新设置span效果
     */

    /**
     *  @param start
     *  @param end
     *  @param spanType 用来判断StyleSpan的具体style Span.Bold...  注意取值只有 0 1 2
     *  @sample 获取某段区间内的 spanlist 的更一般做法，这里使用泛型T，上限为CharacterStyle
     */
    inline fun <reified T : CharacterStyle> getSpans(
        start: Int,
        end: Int,
        spanType: Int = 0
    ): ArrayList<Span> {
        val spanString = SpannableString(text)
        val spanList = ArrayList<Span>()

        var currIndex = start
        var nextIndex = start

        while (currIndex < end) {
            nextIndex = spanString.nextSpanTransition(currIndex, end, T::class.java)
            val spans: Array<T> =
                spanString.getSpans(currIndex, nextIndex, T::class.java)

            //需要进一步判断style 是哪个
            if (spanType != 0) {
                (spans as Array<StyleSpan>).apply {
                    for (span in spans) {
                        if (span.style == Typeface.BOLD && spanType == Span.BOLD) {
                            Log.d(TAG, "getSpans: Span{ ${spanType}, $currIndex, $nextIndex}")
                            spanList.add(Span(Span.BOLD, currIndex, nextIndex))
                        }
                        if (span.style == Typeface.ITALIC && spanType == Span.ITALIC) {
                            Log.d(TAG, "getSpans: Span{ ${spanType}, $currIndex, $nextIndex}")
                            spanList.add(Span(Span.ITALIC, currIndex, nextIndex))
                        }
                    }
                }
            } else {
                for (span in spans) {
                    Log.d(
                        TAG,
                        "getSpans: Span{ ${Span.getSpanType(span)}, $currIndex, $nextIndex}"
                    )
                    spanList.add(Span(Span.getSpanType(span), currIndex, nextIndex))
                }
            }



            currIndex = nextIndex
        }

        return spanList
    }

    fun setSpans(spanString: SpannableString, spans: ArrayList<Span>): SpannableString {
        Log.d(TAG, "setSpans:  size: ${spans.size}")
        for (span in spans) {
            Log.d(TAG, "setSpans:           ------------------>")
            Log.d(TAG, "setSpans: type: ${span.type}")
            when (span.type) {
                Span.BOLD -> {
                    Log.d(TAG, "setSpans: bold")
                    spanString.setSpan(
                        StyleSpan(Typeface.BOLD),
                        span.start,
                        span.end,
                        span.flags
                    )
                }
                Span.ITALIC -> {
                    Log.d(TAG, "setSpans: italic")
                    spanString.setSpan(
                        StyleSpan(Typeface.ITALIC),
                        span.start,
                        span.end,
                        span.flags
                    )
                }
                Span.UNDERLINE -> {
                    Log.d(TAG, "setSpans: underline")
                    spanString.setSpan(
                        UnderlineSpan(),
                        span.start,
                        span.end,
                        span.flags
                    )
                }
                Span.STRIKETHROUGHLINE -> {
                    Log.d(TAG, "setSpans: strikethroughline")
                    spanString.setSpan(
                        StrikethroughSpan(),
                        span.start,
                        span.end,
                        span.flags
                    )
                }
                Span.FONT_SIZE_BIGGER -> {
                    Log.d(TAG, "setSpans: font bigger")
                    spanString.setSpan(
                        RelativeSizeSpan(Span.VALUE_FONT_BIGGER),
                        span.start,
                        span.end,
                        span.flags
                    )
                }
                Span.FONT_SIZE_SMALLER -> {
                    Log.d(TAG, "setSpans: font smaller")
                    spanString.setSpan(
                        RelativeSizeSpan(Span.VALUE_FONT_SMALLER),
                        span.start,
                        span.end,
                        span.flags
                    )
                }
                Span.IMAGE -> {
                    Log.d(TAG, "setSpans: image")

                }

            }
            Log.d(TAG, "setSpans:   <-------------------------")
            Log.d(TAG, "")
        }
//        setText(spanString)
        return spanString
    }

    /**
     *  @param spanString 要修该样式的样式串
     *  @param spans 样式效果
     *  @param style 样式类型
     *  @param spanType StyleSpan 时，的具体类型 Bold   Italic
     */
    fun setSpans2(
        spanString: SpannableString,
        spans: ArrayList<Span>
    ): SpannableString {
        for (span in spans) {
            spanString.setSpan(Span.getSpanStyle(span.type), span.start, span.end, span.flags)
        }
        return spanString
    }

    /**
     * @param spanType 用来判断StyleSpan的具体style Span.Bold...  注意取值只有 0 1 2
     * @sample 先获得该样式的 spanlist 在从中删去区间，删掉spanString中这个样式（一删删所有）。最后再给spanString重新setSpan
     */
    inline fun <reified T : CharacterStyle> removeOneSpan(
        start: Int,
        end: Int,
        spanType: Int = 0
    ) {
        val spanString = SpannableString(text)
        Log.d(TAG, "removeOneSpan: [$start, $end] $spanType")
        //获取该类型的所有样式  ArrayList<Span>
        val spanList = if (spanType == 1 || spanType == 2) {
            //bold
            getSpans<T>(0, spanString.length, spanType)
        } else {
            getSpans<T>(0, spanString.length)
        }
        Log.d(TAG, "removeOneSpan: spanList size: ${spanList.size}")
        //会删除所有文字的该样式，所以要将其余区间的文字样式保留下来
        val needRemoved = ArrayList<Span>()
        val splitSpans = ArrayList<Span>()
        val flags = if (start == end) {
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        } else {
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        }
        for (span in spanList) {
            /**
             *  spans由很多小区间组成，得删除从 start 到 end 的区间
             *  对于每个小区间而言，凡是包括start和end端点,不含两端，都得进行区间分割
             */
            if (start <= span.start && end >= span.end) {
                //StyleSpan 情况
                if (spanType == 0) {
                    needRemoved.add(span)
                } else {
                    if (spanType == span.type)
                        needRemoved.add(span)
                }
                Log.d(
                    TAG,
                    "removeOneSpan: remove { ${span.type}, ${span.start}, ${span.end}, ${span.flags} }"
                )
            } else if (start <= span.end && start >= span.start || end >= span.start && end <= span.end) {
                if (start > span.start) {
                    splitSpans.add(Span(span.type, span.start, start, flags))
                    Log.d(
                        TAG,
                        "removeOneSpan: add { ${span.type}, ${span.start}, $start, $flags }"
                    )
                }
                if (end < span.end) {
                    splitSpans.add(Span(span.type, end, span.end, flags))
                    Log.d(
                        TAG,
                        "removeOneSpan: add { ${span.type}, ${end}, ${span.end}, $flags }"
                    )
                }
                //易知上述只有一个满足
                needRemoved.add(span)
                Log.d(
                    TAG,
                    "removeOneSpan: remove { ${span.type}, ${span.start}, ${span.end}, ${span.flags} }"
                )
            }
        }
        if (needRemoved.isNotEmpty()) {
            spanList.removeAll(needRemoved)
        }
        if (splitSpans.isNotEmpty()) {
            spanList.addAll(splitSpans)
        }
        Log.d(TAG, "removeOneSpan: remove size: ${needRemoved.size}")
        Log.d(TAG, "removeOneSpan: add size: ${splitSpans.size}")

        //spanString中删除样式
        val spans: Array<T> = spanString.getSpans(0, spanString.length, T::class.java)
        if (spanType != 0) {
            (spans as Array<StyleSpan>).apply {
                for (span in spans) {
                    if (span.style == Typeface.BOLD && spanType == Span.BOLD) {
                        spanString.removeSpan(span)
                    }
                    if (span.style == Typeface.ITALIC && spanType == Span.ITALIC) {
                        spanString.removeSpan(span)
                    }
                }
            }
        } else {
            for (span in spans) {
                spanString.removeSpan(span)
            }
        }
        Log.d(TAG, "removeOneSpan: spans size: ${spans.size}")
        val afterRemoveSpans: Array<T> =
            spanString.getSpans(0, spanString.length, T::class.java)
        Log.d(TAG, "removeOneSpan: spans size: ${afterRemoveSpans.size}")

        Log.d(TAG, "removeOneSpan: spanList:")
        for (span in spanList) {
            Log.d(
                TAG,
                "removeOneSpan: { ${span.type}, ${span.start}, ${span.end}, ${span.flags} }"
            )
        }

//        setText(spanString)
        //给spanString 设置 spanstyle

        setText(setSpans2(spanString, spanList))
        setSelection(end)
    }
}