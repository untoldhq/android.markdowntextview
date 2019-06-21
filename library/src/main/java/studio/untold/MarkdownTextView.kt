/*
 * MarkdownTextView.kt
 * markdowntextview
 *
 * Created by Alberto Martinez on 06/17/19.
 * Copyright © 2019 Untold Studio. All rights reserved.
 */

package studio.untold

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import android.text.Layout
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.QuoteSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import studio.untold.markdowntextview.BuildConfig
import studio.untold.markdowntextview.R
import studio.untold.internal.markdowntextview.AdjustableLeadingMarginSpan
import studio.untold.internal.markdowntextview.MarkdownManager
import studio.untold.internal.markdowntextview.MarkdownParser
import studio.untold.internal.markdowntextview.MarkdownTagHandler
import studio.untold.internal.markdowntextview.render.base.BackgroundRenderer
import studio.untold.internal.markdowntextview.render.BlockQuoteLineRenderer
import studio.untold.internal.markdowntextview.render.CodeBlockRenderer
import studio.untold.internal.markdowntextview.render.MultiLineRenderer
import studio.untold.internal.markdowntextview.render.SingleLineRenderer

class MarkdownTextView : AppCompatTextView {
    // the following fields are not lazy, we get these from defaults or override in xml
    private var mPxHorizontal: Int = 0
    private var mPxVertical: Int = 0

    // drawables for inline code blocks
    private lateinit var mRoundedTextDrawable: Drawable
    private lateinit var mRoundedTextDrawableLeft: Drawable
    private lateinit var mRoundedTextDrawableMid: Drawable
    private lateinit var mRoundedTextDrawableRight: Drawable

    private var mInlineCodeTextColor = Color.RED
    private var mBlockquoteLineColor = Color.RED

    private val mCodeBlockRenderer by lazy {
        CodeBlockRenderer(
            pxPaddingHorizontal = mPxHorizontal,
            pxPaddingVertical = mPxVertical,
            drawable = mRoundedTextDrawable
        )
    }
    private val mSingleLineRenderer: BackgroundRenderer by lazy {
        SingleLineRenderer(
            pxPaddingHorizontal = mPxHorizontal,
            pxPaddingVertical = mPxVertical,
            drawable = mRoundedTextDrawable
        )
    }

    private val mMultiLineRenderer: BackgroundRenderer by lazy {
        MultiLineRenderer(
            pxPaddingHorizontal = mPxHorizontal,
            pxPaddingVertical = mPxVertical,
            drawableLeft = mRoundedTextDrawableLeft,
            drawableMid = mRoundedTextDrawableMid,
            drawableRight = mRoundedTextDrawableRight
        )
    }
    private val mBlockQuoteLineRenderer: BackgroundRenderer by lazy {
        BlockQuoteLineRenderer(
            blockQuoteLineColor = mBlockquoteLineColor,
            paddingHorizontal = mPxHorizontal,
            paddingVertical = mPxVertical
        )
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = android.R.attr.textViewStyle
    ) : super(context, attrs, defStyleAttr) {
        initialize(context, attrs)
    }

    fun setMarkdownTextAsync(messageId: String, inString: String) {
        // tag handler needs to be new each time
        // keeping an instance to reuse has a chance to be left
        // in a state where indents stack up (tracked internally)
        // there is no way to reliably reset it's state
        val tagHandler = MarkdownTagHandler(mInlineCodeTextColor, mPxHorizontal)
        MarkdownManager.loadMessageText(this, tagHandler, messageId, inString)
    }

    var currentMarkdownText: String = ""
        private set
    fun setMarkdownText(inString: String) {
        // tag handler needs to be new each time
        // keeping an instance to reuse has a chance to be left
        // in a state where indents stack up (tracked internally)
        // there is no way to reliably reset it's state
        val tagHandler = MarkdownTagHandler(mInlineCodeTextColor, mPxHorizontal)
        var spanned: Spanned? = null
        try {
            val toHtmlMarkdown = MarkdownParser.process(inString)
            spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val imageGetter = Html.ImageGetter { null }
                Html.fromHtml(toHtmlMarkdown, Html.FROM_HTML_MODE_COMPACT, imageGetter, tagHandler)
            } else {
                @Suppress("DEPRECATION")
                Html.fromHtml(toHtmlMarkdown)
            }
        } catch(err: Exception) {
            if (BuildConfig.DEBUG) err.printStackTrace()
        }
        currentMarkdownText = inString
        text = spanned?:""
    }


    private fun initialize(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(
                attrs,
            R.styleable.MarkdownTextView,
                0,
            R.style.MarkdownTextView
        )

        mPxHorizontal = typedArray.getDimensionPixelSize(R.styleable.MarkdownTextView_markdownHorizontalPadding, 0)
        mPxVertical = typedArray.getDimensionPixelSize(R.styleable.MarkdownTextView_markdownVerticalPadding, 0)

        mInlineCodeTextColor = typedArray.getColor(R.styleable.MarkdownTextView_colorInlineCodeText, Color.RED)
        mBlockquoteLineColor = typedArray.getColor(R.styleable.MarkdownTextView_colorBlockquoteText, Color.RED)

        mRoundedTextDrawable = typedArray.getDrawable(R.styleable.MarkdownTextView_bgTextDrawable)!!
        mRoundedTextDrawableLeft = typedArray.getDrawable(R.styleable.MarkdownTextView_bgTextDrawableLeft)!!
        mRoundedTextDrawableMid = typedArray.getDrawable(R.styleable.MarkdownTextView_bgTextDrawableMid)!!
        mRoundedTextDrawableRight = typedArray.getDrawable(R.styleable.MarkdownTextView_bgTextDrawableRight)!!
        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        // bg first
        if (text is Spanned && layout != null) {
            canvas.save()
            canvas.translate(totalPaddingLeft.toFloat(), totalPaddingTop.toFloat())
            onDrawRoundedRects(canvas, text as Spanned, layout)
            onDrawBlockQuoteLine(canvas, text as Spanned, layout)
            canvas.restore()
        }
        // then text will render here
        super.onDraw(canvas)
    }

    private fun onDrawRoundedRects(canvas: Canvas, text: Spanned, layout: Layout) {
        val foregroundColorSpans = text.getSpans(0, text.length, ForegroundColorSpan::class.java)
        foregroundColorSpans.reversed().forEach { span ->
            try {
                if (span.foregroundColor == mInlineCodeTextColor) {
                    val spanStart = text.getSpanStart(span)
                    val spanEnd = text.getSpanEnd(span)
                    val startLine = layout.getLineForOffset(spanStart)
                    val endLine = layout.getLineForOffset(spanEnd)

                    // start can be on the left or on the right depending on the language direction.
                    val startOffset = (layout.getPrimaryHorizontal(spanStart) + -1 * layout.getParagraphDirection(startLine) * 1).toInt()

                    // end can be on the left or on the right depending on the language direction.
                    val endOffset = (layout.getPrimaryHorizontal(spanEnd) + layout.getParagraphDirection(endLine) * 1).toInt()

                    val renderer = if (startLine == endLine) mSingleLineRenderer else mMultiLineRenderer
                    renderer.draw(canvas, layout, startLine, endLine, startOffset, endOffset)
                }
            } catch(err: Exception) { // wrapping this up in safety allows ellipsized text blocks
                if (BuildConfig.DEBUG) err.printStackTrace()
            }
        }
        val almSpan = text.getSpans(0, text.length, AdjustableLeadingMarginSpan::class.java)
        almSpan.reversed().forEach { span ->
            try {
                val spanStart = text.getSpanStart(span)
                val spanEnd = text.getSpanEnd(span)
                val startLine = layout.getLineForOffset(spanStart)
                val endLine = layout.getLineForOffset(spanEnd)

                // start can be on the left or on the right depending on the language direction.
                val startOffset = paddingStart/2
                val endOffset = paddingEnd/2

                mCodeBlockRenderer.levels = span.levelsIn+1
                mCodeBlockRenderer.draw(canvas, layout, startLine, endLine, startOffset, endOffset)
            } catch(err: Exception) { // wrapping this up in safety allows ellipsized text blocks
                if (BuildConfig.DEBUG) err.printStackTrace()
            }
        }
    }

    private fun onDrawBlockQuoteLine(canvas: Canvas, text: Spanned, layout: Layout) {
        val quoteSpans = text.getSpans(0, text.length, QuoteSpan::class.java)
        quoteSpans.reversed().forEach { span ->
            try {
                val spanStart = text.getSpanStart(span)
                val spanEnd = text.getSpanEnd(span)
                val startLine = layout.getLineForOffset(spanStart)
                val endLine = layout.getLineForOffset(spanEnd)

                // start can be on the left or on the right depending on the language direction.
                val startOffset = (layout.getPrimaryHorizontal(spanStart) + -1 * layout.getParagraphDirection(startLine) * mPxHorizontal).toInt()

                // end can be on the left or on the right depending on the language direction.
                val endOffset = (layout.getPrimaryHorizontal(spanEnd) + layout.getParagraphDirection(endLine) * mPxHorizontal).toInt()

                // draw line on start-hand-side
                mBlockQuoteLineRenderer.draw(canvas, layout, startLine, endLine, startOffset, endOffset)
            } catch(err: Exception) { // wrapping this up in safety allows ellipsized text blocks
                if (BuildConfig.DEBUG) err.printStackTrace()
            }
        }
    }
}