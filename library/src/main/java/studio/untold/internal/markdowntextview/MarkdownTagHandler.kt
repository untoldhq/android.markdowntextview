/*
 * MarkdownTagHandler.kt
 * markdowntextview
 *
 * Created by Alberto Martinez on 06/17/19.
 * Copyright © 2019 Untold Studio. All rights reserved.
 */

package studio.untold.internal.markdowntextview

import android.graphics.Color
import android.text.*
import android.text.style.*
import org.xml.sax.XMLReader

internal class MarkdownTagHandler(private val inlineCodeColor: Int, private val mPxSingleLine: Int): Html.TagHandler {

    private var mBlockParents: Int = 0

    override fun handleTag(opening: Boolean, tag: String, output: Editable, xmlReader: XMLReader) {
        when (tag) {
            "url" -> {
                processUrl(opening, output)
            }
            "inlinecode" -> {
                processInlineCode(opening, output)
            }
            "fullcodeblock" -> {
                processBlock(opening, output)
            }
            "bquote" -> {
                processBlockQuote(opening, output)
            }
        }
    }

    private fun processBlock(opening: Boolean, output: Editable) {
        val len = output.length

        val indentSpan =
            AdjustableLeadingMarginSpan(mBlockParents, mPxSingleLine)
        val sizeSpan = RelativeSizeSpan(0.8f)
        val fontSpan = TypefaceSpan("monospace")

        if (opening) {
            output.setSpan(fontSpan, len, len, Spannable.SPAN_MARK_MARK)
            output.setSpan(indentSpan, len, len, Spannable.SPAN_MARK_MARK)
            output.setSpan(sizeSpan, len, len, Spannable.SPAN_MARK_MARK)
            mBlockParents++
        } else {
            val lastSizeSpan = getLast(output, RelativeSizeSpan::class.java) as RelativeSizeSpan
            val lastSpan = getLast(output, AdjustableLeadingMarginSpan::class.java) as AdjustableLeadingMarginSpan
            val lastFontSpan = getLast(output, TypefaceSpan::class.java) as TypefaceSpan
            val where = output.getSpanStart(lastSpan)
            output.removeSpan(lastSizeSpan)
            output.removeSpan(lastSpan)
            output.removeSpan(lastFontSpan)
            if (where != len) {
                output.setSpan(fontSpan, where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                output.setSpan(indentSpan, where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                output.setSpan(sizeSpan, where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            mBlockParents--
        }
    }

    private fun processInlineCode(opening: Boolean, output: Editable) {
        val len = output.length

        val sizeSpan = RelativeSizeSpan(0.8f)
        val foregroundColorSpan = ForegroundColorSpan(inlineCodeColor)

        if (opening) {
            output.setSpan(foregroundColorSpan, len, len, Spannable.SPAN_MARK_MARK)
            output.setSpan(sizeSpan, len, len, Spannable.SPAN_MARK_MARK)
        } else {
            val lastForegroundColorSpan = getLast(output, ForegroundColorSpan::class.java)
            val lastSizeSpan = getLast(output, RelativeSizeSpan::class.java)
            val where = output.getSpanStart(lastForegroundColorSpan)
            output.removeSpan(lastSizeSpan)
            output.removeSpan(lastForegroundColorSpan)
            if (where != len) {
                output.setSpan(foregroundColorSpan, where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                output.setSpan(sizeSpan, where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    private fun processUrl(opening: Boolean, output: Editable) {
        if (mBlockParents <= 0) {
            val len = output.length
            if (opening) {
                val urlSpan = URLSpan("")
                output.setSpan(urlSpan, len, len, Spannable.SPAN_MARK_MARK)
            } else {
                val lastUrlSpan = getLast(output, URLSpan::class.java)
                val where = output.getSpanStart(lastUrlSpan)
                output.removeSpan(lastUrlSpan)
                if (where != len) {
                    val urlSpan = URLSpan(output.substring(where, len))
                    output.setSpan(urlSpan, where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }
    }

    private fun processBlockQuote(opening: Boolean, output: Editable) {
        val len = output.length

        val quoteSpan = QuoteSpan(Color.TRANSPARENT)
        val indentSpan = LeadingMarginSpan.Standard(mPxSingleLine)

        if (opening) {
            output.setSpan(quoteSpan, len, len, Spannable.SPAN_MARK_MARK)
            output.setSpan(indentSpan, len, len, Spannable.SPAN_MARK_MARK)
            mBlockParents++
        } else {
            val lastQuoteSpan = getLast(output, QuoteSpan::class.java) as QuoteSpan
            val lastSpan = getLast(output, LeadingMarginSpan.Standard::class.java) as LeadingMarginSpan.Standard
            val where = output.getSpanStart(lastSpan)
            output.removeSpan(lastQuoteSpan)
            output.removeSpan(lastSpan)
            if (where != len) {
                output.setSpan(quoteSpan, where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                output.setSpan(indentSpan, where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            mBlockParents--
        }
    }

    private fun getLast(text: Editable, kind: Class<out Any>): Any? {
        val objs = text.getSpans(0, text.length, kind)

        if (objs.isNullOrEmpty()) {
            return null
        } else {
            for (i in objs.size downTo 0) {
                if (text.getSpanFlags(objs[i - 1]) == Spannable.SPAN_MARK_MARK) {
                    return objs[i - 1]
                }
            }
            return null
        }
    }

}