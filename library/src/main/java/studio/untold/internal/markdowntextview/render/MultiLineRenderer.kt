/*
 * MultiLineRenderer.kt
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

internal class MultiLineRenderer(
        pxPaddingHorizontal: Int,
        pxPaddingVertical: Int,
        val drawableLeft: Drawable,
        val drawableMid: Drawable,
        val drawableRight: Drawable
) : BackgroundRenderer(pxPaddingHorizontal, pxPaddingVertical) {

    override fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int
    ) {
        // draw the first line
        val paragDir = layout.getParagraphDirection(startLine)
        val lineEndOffset = if (paragDir == Layout.DIR_RIGHT_TO_LEFT) {
            layout.getLineLeft(startLine)
        } else {
            layout.getLineRight(startLine)
        }.toInt()

        var lineBottom = getLineBottom(layout, startLine)
        var lineTop = getLineTop(layout, startLine)
        drawStart(canvas, startOffset, lineTop, lineEndOffset, lineBottom)

        // for the lines in the middle draw the mid drawable
        for (line in startLine + 1 until endLine) {
            lineTop = getLineTop(layout, line) + (pxPaddingVertical/2)
            lineBottom = getLineBottom(layout, line) - (pxPaddingVertical*3/4)
            val right = layout.getLineRight(line) + (pxPaddingHorizontal/4)
            val left = layout.getLineLeft(line) - (pxPaddingHorizontal/4)
            drawableMid.setBounds(
                    left.toInt(),
                    lineTop,
                    right.toInt(),
                    lineBottom
            )
            drawableMid.draw(canvas)
        }

        val lineStartOffset = if (paragDir == Layout.DIR_RIGHT_TO_LEFT) {
            layout.getLineRight(startLine)
        } else {
            layout.getLineLeft(startLine)
        }.toInt()

        // draw the last line
        lineBottom = getLineBottom(layout, endLine)
        lineTop = getLineTop(layout, endLine)

        drawEnd(canvas, lineStartOffset, lineTop, endOffset, lineBottom)
    }

    private fun drawStart(canvas: Canvas, start: Int, top: Int, end: Int, bottom: Int) {
        if (start > end) {
            drawableRight.setBounds(end - (pxPaddingHorizontal/4), top + (pxPaddingVertical*2/3), start + (pxPaddingHorizontal/4), bottom - pxPaddingVertical)
            drawableRight.draw(canvas)
        } else {
            drawableLeft.setBounds(start - (pxPaddingHorizontal/4), top + (pxPaddingVertical*2/3), end + (pxPaddingHorizontal/4), bottom - pxPaddingVertical)
            drawableLeft.draw(canvas)
        }
    }

    private fun drawEnd(canvas: Canvas, start: Int, top: Int, end: Int, bottom: Int) {
        if (start > end) {
            drawableLeft.setBounds(end - (pxPaddingHorizontal/4), top + (pxPaddingVertical*2/3), start + (pxPaddingHorizontal/4), bottom - pxPaddingVertical)
            drawableLeft.draw(canvas)
        } else {
            drawableRight.setBounds(start - (pxPaddingHorizontal/4), top + (pxPaddingVertical*2/3), end + (pxPaddingHorizontal/4), bottom - pxPaddingVertical)
            drawableRight.draw(canvas)
        }
    }
}