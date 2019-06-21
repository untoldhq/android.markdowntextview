/*
 * BlockQuoteLineRenderer.kt
 * markdowntextview
 *
 * Created by Alberto Martinez on 06/17/19.
 * Copyright © 2019 Untold Studio. All rights reserved.
 */

package studio.untold.internal.markdowntextview.render

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import studio.untold.internal.markdowntextview.render.base.BackgroundRenderer

internal class BlockQuoteLineRenderer(private val blockQuoteLineColor: Int, paddingHorizontal: Int, paddingVertical: Int) : BackgroundRenderer(paddingHorizontal, paddingVertical) {
    private val paint by lazy { Paint().apply {
            color = blockQuoteLineColor
            strokeWidth = (pxPaddingHorizontal/2f)
        }
    }

    override fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int
    ) {
        val lineBottom = getLineBottom(layout, endLine)
        val lineTop = getLineTop(layout, startLine)
        canvas.drawLine(
            (pxPaddingHorizontal/2f),
            lineTop.toFloat(),
            (pxPaddingHorizontal/2f),
            lineBottom.toFloat(),
            paint )

    }

    override fun getLineBottom(layout: Layout, line: Int): Int {
        return layout.getLineBottom(line) - pxPaddingVertical
    }
}