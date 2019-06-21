/*
 * BackgroundRenderer.kt
 * markdowntextview
 *
 * Created by Alberto Martinez on 06/17/19.
 * Copyright © 2019 Untold Studio. All rights reserved.
 */

package studio.untold.internal.markdowntextview.render.base

import android.graphics.Canvas
import android.text.Layout

internal abstract class BackgroundRenderer(
    protected val pxPaddingHorizontal: Int,
    protected val pxPaddingVertical: Int
) {

    abstract fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int
    )

    protected fun getLineTop(layout: Layout, line: Int): Int {
        return layout.getLineTop(line) - (pxPaddingVertical/2)
    }

    protected open fun getLineBottom(layout: Layout, line: Int): Int {
        return layout.getLineBottom(line) + (pxPaddingVertical/2) + (if (line == layout.lineCount-1) (pxPaddingVertical/2) else 0)
    }

}