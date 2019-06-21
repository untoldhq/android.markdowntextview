/*
 * AdjustableLeadingMarginSpannSpan.kt
 * markdowntextview
 *
 * Created by Alberto Martinez on 06/17/19.
 * Copyright © 2019 Untold Studio. All rights reserved.
 */

package studio.untold.internal.markdowntextview

import android.text.style.LeadingMarginSpan

internal class AdjustableLeadingMarginSpan(level: Int, gapWidth: Int): LeadingMarginSpan.Standard(gapWidth) {
    var levelsIn = level
    var indentGapWidth = gapWidth
    override fun getLeadingMargin(first: Boolean): Int { return indentGapWidth }
}