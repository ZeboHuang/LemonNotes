package com.vividbobo.lemonnotes.richtext

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.*
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.graphics.drawable.toDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.vividbobo.lemonnotes.entity.Note
import com.vividbobo.lemonnotes.entity.Span


class SimpleRichEditText(context: Context, attrs: AttributeSet?) :
    AppCompatEditText(context, attrs) {

    private val TAG = "SimpleRichEditText"


    /**
     * 获取 toHtml 时，或者 getText 无法获取到图片的 url
     * */
    private val images = ArrayList<String>()

    /**
     *    以下设置跟字体相关的
     * */
    fun setBold() {
        //字体加粗
        setStyle(StyleSpan(Typeface.BOLD))
    }

    fun getBold(): Boolean {
        return getStyleStatus(StyleSpan(Typeface.BOLD))
    }

    fun setItalic() {
        setStyle(StyleSpan(Typeface.ITALIC))
    }

    fun setUnderline() {
        setStyle(UnderlineSpan())
    }

    fun setStrikethroughLine() {
        setStyle(StrikethroughSpan())
    }

    fun setFontSize(size: Int) {
        val spannableString = SpannableString(this.text)
        val start = this.selectionStart
        val end = this.selectionEnd
        if (end >= start)       //放置从右往左选择出错
            spannableString.setSpan(RelativeSizeSpan(2f), start, end, 0)
        else
            spannableString.setSpan(RelativeSizeSpan(2f), end, start, 0)
        this.setText(spannableString)
    }

    fun setStyle(styleSpan: CharacterStyle) {

        /**
         * 这里的逻辑最好为
         * start != end 时，表明选择
         * start==end时，表明接下来要输入的字符都是bold字体
         *
         * 通过设置 flag 来表明是否 在前后输入时，自动为 bold样式
         * */

        val spannableString = SpannableString(this.text)
        val start = this.selectionStart
        val end = this.selectionEnd
        if (end > start)       //防止从右往左选择出错
        {
            spannableString.setSpan(styleSpan, start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        } else if (end < start) {
            spannableString.setSpan(styleSpan, end, start, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        } else {
            spannableString.setSpan(styleSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
        this.setText(spannableString)
        this.setSelection(end)
    }

    //光标所在位置，选择段，样式图标的状态
    fun getStyleStatus(styleSpan: CharacterStyle): Boolean {
        val spannableString = SpannableString(this.text)
        var start = this.selectionStart
        var end = this.selectionEnd
        if (start == end && start == 0) {
            //光标所在位置
            //光标为首字符 false
            return false

        } else {
            //取前一个字符状况，可能含有多种span
            if (start == end) {
                start = start - 1;
            }
            if (start > end) {
                val t = start
                start = end
                end = t
            }
            Log.d(TAG, "getStyleStatus: [$start, $end]")
            val spans: Array<CharacterStyle> =
                spannableString.getSpans(start, end, CharacterStyle::class.java)
            for (span in spans) {
                when (span) {
                    is StyleSpan -> {
                        (styleSpan as StyleSpan).apply {
                            if (this.style == Typeface.BOLD) {
                                Log.d(TAG, "getStyleStatus: bold")
                                return true
                            }
                            if (this.style == Typeface.ITALIC) {
                                Log.d(TAG, "getStyleStatus: italic")
                                return true
                            }
                        }
                    }
                    is UnderlineSpan -> {
                        if (styleSpan is UnderlineSpan) {
                            Log.d(TAG, "getStyleStatus: underline")
                            return true
                        }
                    }
                    is StrikethroughSpan -> {
                        if (styleSpan is StrikethroughSpan) {
                            Log.d(TAG, "getStyleStatus: strikethrough")
                            return true
                        }
                    }
                }
            }
        }
        return false
    }


    //span 方式插入图片
    fun insertImage(url: String) {
        val spannableString = SpannableString(this.text)
        val start = this.selectionStart
        val end = this.selectionEnd
        getBitmap(url)?.let {
            spannableString.setSpan(ImageSpan(context, it), start, end, 0)
        }
        Log.d(TAG, "insertImage: " + spannableString.toString())
        this.setText(spannableString)
    }

    //借助Glide 获取bitmap
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
                        val screenWidth = context.resources.displayMetrics.widthPixels
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

    //获取spannableString
    fun getSpannableString(): SpannableString {
        return SpannableString(text)
    }

    fun toHtml(): String {
        return Html.toHtml(getSpannableString())
    }

    fun fromHtml(source: String) {
        val imageGetter = object : Html.ImageGetter {
            override fun getDrawable(p0: String?): Drawable {
                return p0?.let { getBitmap(it)?.toDrawable(resources) } as Drawable
            }
        }
        setText(Html.fromHtml(source, Html.FROM_HTML_MODE_COMPACT))
    }

    fun getSpans(): List<Span> {
        val spannableString = getSpannableString()
        val spanDatas = ArrayList<Span>()
        var currIndex = 0
        var nextIndex = 0
        while (currIndex < spannableString.length) {
            nextIndex = spannableString.nextSpanTransition(
                currIndex,
                spannableString.length,
                CharacterStyle::class.java
            )
            val spans: Array<CharacterStyle> =
                spannableString.getSpans(currIndex, nextIndex, CharacterStyle::class.java)
            Log.d(
                TAG,
                "------------> spans size: ${spans.size}   [$currIndex, $nextIndex]"
            )
            for (span in spans) {
                //后面再改为when
                if (span is UnderlineSpan) {
                    spanDatas.add(Span(Span.UNDERLINE, currIndex, nextIndex))
                    Log.d(TAG, "Underline")
                }
                if (span is StyleSpan) {
                    if (span.style == Typeface.BOLD) {
                        spanDatas.add(Span(Span.BOLD, currIndex, nextIndex))
                        Log.d(TAG, "bold")
                    }
                    if (span.style == Typeface.ITALIC) {
                        spanDatas.add(Span(Span.ITALIC, currIndex, nextIndex))
                        Log.d(TAG, "italic")
                    }
                }
                if (span is AbsoluteSizeSpan) {
                    spanDatas.add(Span(Span.FONT_SIZE_BIGGER, currIndex, nextIndex))
                    Log.d(TAG, "font size: ${span.size}")
                }
                if (span is RelativeSizeSpan) {
                    spanDatas.add(Span(Span.FONT_SIZE_BIGGER, currIndex, nextIndex))
                    Log.d(TAG, "font relative size: ${span.sizeChange}")
                }
                if (span is ImageSpan) {
                    Log.d(TAG, "image source: ${span.source}")
                }
            }
            Log.d(TAG, "<---------------")
            currIndex = nextIndex
        }
        Log.d(TAG, "getSpans: spanDatas size: ${spanDatas.size}")
        return spanDatas
    }

    /**
     * @param spans 要赋值的所有样式
     * @sample 返回一个SpannableString
     */
    fun setSpans(spans: List<Span>): SpannableString {
        /**
         * setSpans
         * 不能直接获取spannableString
         * 重新获取文本，重新赋值样式，
         * */
        Log.d(TAG, "setSpans: spans size: ${spans.size}")
        val spannableString = SpannableString(this.text.toString())
        for (span in spans) {
            when (span.type) {
                Span.BOLD -> {
                    Log.d(TAG, "setSpans: type: bold")
                    spannableString.setSpan(
                        StyleSpan(
                            Typeface.BOLD
                        ),
                        span.start,
                        span.end,
                        span.flags
                    )
                }
                Span.ITALIC -> {
                    Log.d(TAG, "setSpans: type: italic")

                    spannableString.setSpan(
                        StyleSpan(
                            Typeface.ITALIC
                        ),
                        span.start,
                        span.end,
                        span.flags
                    )
                }
                Span.UNDERLINE -> {
                    Log.d(TAG, "setSpans: type: underline")

                    spannableString.setSpan(
                        UnderlineSpan(),
                        span.start,
                        span.end,
                        span.flags
                    )
                }
                Span.FONT_SIZE_BIGGER -> {
                    Log.d(TAG, "setSpans: type: fontsizeup")

                    spannableString.setSpan(
                        //这个size 得改
                        AbsoluteSizeSpan(200),
                        span.start,
                        span.end,
                        span.flags
                    )
                }

                /**
                 * 还有好几项呢
                 * */

            }
        }
        return spannableString
    }

    fun getNote() {
        TODO("no implement")
    }

    //这个方法应该提到别的地方去
    fun setNote(note: Note) {
        note.spans?.let { setSpans(it) }
    }

    /**
     * @param spanStyle 要删除的样式
     * @param start 要删除的样式起点
     * @param end   要删除的样式终点
     * @param spans 所有span样式
     * @sample 在spans中删除区间段内的某一种样式
     * */
    private fun removeSpans(
        spanStyle: CharacterStyle,
        start: Int,
        end: Int,
        spans: List<Span>
    ) {
        val type = Span.getSpanType(spanStyle)
        Log.d(TAG, "removeSpansInList: type: $type")
        Log.d(TAG, "removeSpansInList: start, end: [$start, $end]")
        Log.d(TAG, "removeSpansInList: spans.size: ${spans.size}")


        val afterSplitSpans = ArrayList<Span>()
        val removeSpans = ArrayList<Span>()
        (spans as ArrayList<Span>).apply {
//光标，样式的解除，给他加个样式试试
            if (start == end) {
                Log.d(TAG, "removeSpans: start==end set normal?")
//                spans.add(Span(Span.NORMAL, start - 1, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE))
            } else {
                for (i in 0 until this.size step 1) {
                    if (this.get(i).type == type) {
                        afterSplitSpans.addAll(splitSpan(this.get(i), start, end))
                        Log.d(
                            TAG,
                            "removeSpansInList: afterSplitSpans.size: ${afterSplitSpans.size}"
                        )
                        removeSpans.add(this.get(i))
                    }
                }
                this.removeAll(removeSpans)
                this.addAll(afterSplitSpans)
            }
            //遍历出现问题？？
//            for (span in this) {
//                Log.d(
//                    TAG,
//                    "removeSpansInList: spaninfo: {${span.type}, [${span.start}, ${span.end}]}"
//                )
//                //存进去的跟我选中的区间不一致，区间分割
//                afterSplitSpans.addAll(splitSpan(span, start, end))
//                //删除本区间
//                this.remove(span)
//            }
//            this.addAll(afterSplitSpans)
        }
        Log.d(TAG, "removeSpansInList: spans size: ${spans.size}")
        //应该不用返回吧，传进来的对象，它内部就删除了
    }

    /**
     * 区间分割
     * 只有在区间中间，才需要将区间分拆成两段，两个if都满足的情况
     * */
    private fun splitSpan(span: Span, start: Int, end: Int): List<Span> {
        val afterSplit = ArrayList<Span>()
        var startIndex = start
        var endIndex = end
        //先吧超出本区间的范围截掉
        if (startIndex < span.start) {
            startIndex = span.start
        }
        if (endIndex > span.end) {
            endIndex = span.end
        }

        if (startIndex > span.start) {
            afterSplit.add(Span(span.type, span.start, startIndex, span.flags))
        }
        if (endIndex < span.end) {
            afterSplit.add(Span(span.type, endIndex, span.end, span.flags))
        }
        return afterSplit
    }

    /**
     * @param styleSpan 要删去的样式
     * @sample 从Spannable 中删去某种特定样式
     * */
    fun removeStyle(styleSpan: CharacterStyle) {
        var start = selectionStart
        var end = selectionEnd
        if (start > end) {
            val t = start
            start = end
            end = t
        }
        val spans = getSpans()
        removeSpans(styleSpan, start, end, spans)

        this.setText(setSpans(spans))
        this.setSelection(end)
    }

    fun removeBold() {
        removeStyle(StyleSpan(Typeface.BOLD))
    }

    fun removeItalic() {
        removeStyle(StyleSpan(Typeface.ITALIC))
    }

    fun removeUnderline() {
        removeStyle(UnderlineSpan())
    }

    fun removeStrikethroughLine() {
        removeStyle(StrikethroughSpan())
    }

    //test
    fun testRemoveBold() {
        var start = selectionStart
        var end = selectionEnd
        if (start > end) {
            val t = start
            start = end
            end = t
        }
        val spans = getSpans()
        removeSpans(StyleSpan(Typeface.BOLD), start, end, spans)
        //以下测试
        setSpans(spans)
    }
}
