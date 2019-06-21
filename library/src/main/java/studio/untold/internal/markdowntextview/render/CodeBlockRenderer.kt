/*
 * RoundedRectRenderernderer.kt
 * markdowntextview
 *
 * Created by Alberto Martinez on 06/17/19.
 * Copyright © 2019 Untold Studio. All rights reserved.
 */

package studio.untold.internal.markdowntextview.render

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Layout
import studio.untold.internal.markdowntextview.render.base.BackgroundRenderer

internal class CodeBlockRenderer(
    pxPaddingHorizontal: Int,
    pxPaddingVertical: Int,
    val drawable: Drawable
) : BackgroundRenderer(pxPaddingHorizontal, pxPaddingVertical) {

    var levels = 0

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
        drawable.setBounds(
        -startOffset + (if (levels > 2) pxPaddingHorizontal else 0) + ((pxPaddingHorizontal/2)*(Math.max(1,levels-1))),
        lineTop,
        canvas.width - endOffset - startOffset - ((pxPaddingHorizontal/2)*(Math.max(1,levels-2))),
        lineBottom - pxPaddingVertical - (if (levels > 2) (pxPaddingVertical*2) else 0))
        drawable.draw(canvas)
    }

    override fun getLineBottom(layout: Layout, line: Int): Int {
        return layout.getLineBottom(line) + pxPaddingVertical
    }
}