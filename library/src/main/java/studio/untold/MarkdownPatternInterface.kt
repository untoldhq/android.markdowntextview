/*
 * MarkdownPatternInterface.kt
 * markdowntextview
 *
 * Created by Alberto Martinez on 06/17/19.
 * Copyright © 2019 Untold Studio. All rights reserved.
 */

package studio.untold

import studio.untold.internal.markdowntextview.MarkdownToken

interface MarkdownPatternInterface {
    fun replace(inString: String): Pair<String,List<MarkdownToken>>
}