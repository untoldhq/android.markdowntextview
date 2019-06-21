/*
 * SingleLineRenderer.kt
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
import kotlin.math.max
import kotlin.math.min


internal class SingleLineRenderer(
    pxPaddingHorizontal: Int,
    pxPaddingVertical: Int,
    val drawable: Drawable
) : BackgroundRenderer(pxPaddingHorizontal, pxPaddingVertical) {

    override fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int
    ) {
        val lineTop = getLineTop(layout, startLine) + (pxPaddingVertical*2/3) + (if (startLine <= 1) (pxPaddingVertical/2) else 0)
        val lineBottom = getLineBottom(layout, startLine) - pxPaddingVertical
        val right = max(startOffset, endOffset) + (pxPaddingHorizontal/4)
        val left = min(startOffset, endOffset) - (pxPaddingHorizontal/4)
        drawable.setBounds(left, lineTop, right, lineBottom)
        drawable.draw(canvas)
    }
}