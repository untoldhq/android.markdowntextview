/*
 * MarkdownParser.kt
 * markdowntextview
 *
 * Created by Alberto Martinez on 06/17/19.
 * Adapted from Swift created by Sam Ingle.
 * Necessary use of `&zwj;` discovered and implemented by Mike Purvis.
 * Copyright © 2019 Untold Studio. All rights reserved.
 */

package studio.untold.internal.markdowntextview

import android.util.Log
import studio.untold.markdowntextview.BuildConfig
import studio.untold.MarkdownPatternInterface
import java.util.*

internal class MarkdownParser {

    companion object {
        //todo: make this return Spanned! Probably scuttle html.fromtext to here with a custom tag handler
        fun process(inString: String): String {
            if (BuildConfig.DEBUG) Log.d("process", "\uD83C\uDFA7\uD83C\uDFA7\uD83C\uDFA7")
            var mutableCopy = inString
            val tokens = mutableListOf<MarkdownToken>()

            for (pattern in all) {
                val pair = pattern.replace(mutableCopy)
                mutableCopy = pair.first
                tokens.addAll(pair.second)
            }

            return reinsertTokens(tokens, mutableCopy)
        }

        fun reinsertTokens(tokens: List<MarkdownToken>, inString: String): String {
            var mutableCopy = inString
            for (token in tokens.reversed()) {
                mutableCopy = mutableCopy.replace(token.replacement, token.original)
            }
            return mutableCopy
        }

        val all by lazy {
            arrayOf(
                URLPattern,
                AmpersandPattern,
                EscapeAngleBracketsPattern,
                CodeBlockPattern,
                InlineCodeBlockPattern,
                ItalicPattern,
                BoldPattern,
                UnderlinePattern,
                StrikethroughPattern,
                ExtendedBlockquotePattern,
                BlockquotePattern,
                NewlinePattern
            )
        }

        private fun createReplacementTokens(inString: String, regex: Regex, leftMarkup: String = "", rightMarkup: String = ""): Pair<String,List<MarkdownToken>> {
            var mutableCopy = inString
            val tokens = mutableListOf<MarkdownToken>()
            val matches = regex.findAll(inString)
            for (index in (matches.count()-1) downTo 0) {
                val match = matches.elementAt(index)
                val substring = inString.substring(match.range).replace(regex, "$1")
                val replacement = "[token|${UUID.randomUUID()}]"
                tokens.add(
                    MarkdownToken(
                        "$leftMarkup$substring$rightMarkup",
                        replacement
                    )
                )
                mutableCopy = mutableCopy.replaceRange(match.range, replacement)
            }
            return Pair(mutableCopy,tokens)
        }
    }

    object BoldPattern: MarkdownPatternInterface {
        private val regex = Regex("(?<=^|\\s|<|>|/|~|_)\\*([^*\n]+)\\*(?=$|\\s|<|>|/|~|_|\\.|\\?|,|\"|')",RegexOption.IGNORE_CASE)
        override fun replace(inString: String): Pair<String,List<MarkdownToken>> {
            if (BuildConfig.DEBUG) Log.d("BoldPattern", "found ${regex.findAll(inString).count()} matches")
            return Pair(inString.replace(regex, "<b>$1</b>"), emptyList())
        }
    }

    object ItalicPattern: MarkdownPatternInterface {
        private val regex = Regex("(?<=^|\\s|<|>|\\*|~|_)/([^/\n]+)/(?=$|\\s|<|>|\\*|~|_|\\.|\\?|,|\"|')",RegexOption.IGNORE_CASE)
        override fun replace(inString: String): Pair<String,List<MarkdownToken>> {
            if (BuildConfig.DEBUG) Log.d("ItalicPattern", "found ${regex.findAll(inString).count()} matches")
            return Pair(inString.replace(regex, "<em>$1</em>"), emptyList())
        }
    }

    object UnderlinePattern: MarkdownPatternInterface {
        private val regex = Regex("(?<=^|\\s|<|>|\\*|/|~)_([^_\n]+)_(?=$|\\s|<|>|\\*|/|~|\\.|\\?|,|\"|')",RegexOption.IGNORE_CASE)
        override fun replace(inString: String): Pair<String,List<MarkdownToken>> {
            if (BuildConfig.DEBUG) Log.d("UnderlinePattern", "found ${regex.findAll(inString).count()} matches")
            return Pair(inString.replace(regex, "<u>$1</u>"), emptyList())
        }
    }

    object StrikethroughPattern: MarkdownPatternInterface {
        private val regex = Regex("(?<=^|\\s|<|>|\\*|/|_)~([^~\n]+)~(?=$|\\s|<|>|\\*|/|_|\\.|\\?|,|\"|')",RegexOption.IGNORE_CASE)
        override fun replace(inString: String): Pair<String,List<MarkdownToken>> {
            if (BuildConfig.DEBUG) Log.d("StrikethroughPattern", "found ${regex.findAll(inString).count()} matches")
            return Pair(inString.replace(regex, "<strike>$1</strike>"), emptyList())
        }
    }

    private object BlockquotePattern: MarkdownPatternInterface {
        private val regex = Regex("(^|\n)&gt;(.+)")
        override fun replace(inString: String): Pair<String,List<MarkdownToken>> {
            if (BuildConfig.DEBUG) Log.d("BlockquotePattern", "found ${regex.findAll(inString).count()} matches")
            return Pair(inString.replace(regex, "$1<bquote><p>$2</p></bquote>"), emptyList())
        }
    }

    private object ExtendedBlockquotePattern: MarkdownPatternInterface {
        private val regex = Regex("(^|\n)&gt;&gt;&gt;(.+)\$",RegexOption.DOT_MATCHES_ALL)
        override fun replace(inString: String): Pair<String,List<MarkdownToken>> {
            if (BuildConfig.DEBUG) Log.d("ExtendedBlockquote", "found ${regex.findAll(inString).count()} matches")
            return Pair(inString.replace(regex, "$1<bquote><p>$2</p></bquote>"), emptyList())
        }
    }

    private object NewlinePattern: MarkdownPatternInterface {
        private val regex = Regex("\n",RegexOption.IGNORE_CASE)
        override fun replace(inString: String): Pair<String,List<MarkdownToken>> {
            if (BuildConfig.DEBUG) Log.d("NewlinePattern", "found ${regex.findAll(inString).count()} matches")
            return Pair(inString.replace(regex, "<br />"), emptyList())
        }
    }

    private object AmpersandPattern: MarkdownPatternInterface {
        private val regex = Regex("&",RegexOption.IGNORE_CASE)
        override fun replace(inString: String): Pair<String,List<MarkdownToken>> {
            if (BuildConfig.DEBUG) Log.d("AmpersandPattern", "found ${regex.findAll(inString).count()} matches")
            return Pair(inString.replace(regex, "&amp;"), emptyList())
        }
    }

    private object EscapeAngleBracketsPattern: MarkdownPatternInterface {
        private val regexLT = Regex("<")
        private val regexGT = Regex(">")
        override fun replace(inString: String): Pair<String,List<MarkdownToken>> {
            if (BuildConfig.DEBUG) Log.d("EscapeAngleBrackets", "found ${regexLT.findAll(inString).count()} & ${regexGT.findAll(inString).count()} matches")
            return Pair(inString.replace(regexLT, "&lt;").replace(
                regexGT, "&gt;"), emptyList())
        }
    }

    object InlineCodeBlockPattern: MarkdownPatternInterface {
        private val regex = Regex("(?<=^|\\s|\\*|/|~|_)`([^`\\n]+)`(?=\$|\\s|\\*|/|~|_|\\.|\\?|,|\\|\"|')",RegexOption.IGNORE_CASE)
        override fun replace(inString: String): Pair<String,List<MarkdownToken>> {
            if (BuildConfig.DEBUG) Log.d("InlineCodePattern", "found ${regex.findAll(inString).count()} matches")
            // Regarding &zwj; usage below, credit to https://stackoverflow.com/a/25836810/711863 for this Android bug workaround
            return createReplacementTokens(
                inString,
                regex,
                "&zwj;<inlinecode>",
                "</inlinecode>"
            )
        }
    }

    private object CodeBlockPattern: MarkdownPatternInterface {
        private val regex = Regex("```(.+?)```", RegexOption.DOT_MATCHES_ALL)
        override fun replace(inString: String): Pair<String,List<MarkdownToken>> {
            if (BuildConfig.DEBUG) Log.d("CodeBlockPattern", "found ${regex.findAll(inString).count()} matches")
            return createReplacementTokens(
                inString,
                regex,
                "<fullcodeblock><p>",
                "</p></fullcodeblock>"
            )
        }
    }

    private object URLPattern: MarkdownPatternInterface {
        private val regex = Regex("\\b((?:[a-z][\\w-]+:(?:/{1,3}|[a-z0-9%])|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)){0,}(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s!()\\[\\]{};:\'\".,<>?«»“”‘’`]))",RegexOption.IGNORE_CASE)
        override fun replace(inString: String): Pair<String,List<MarkdownToken>> {
            if (BuildConfig.DEBUG) Log.d("URLPattern", "found ${regex.findAll(inString).count()} matches")
            return createReplacementTokens(
                inString,
                regex,
                "&zwj;<url>",
                "</url>"
            )
        }
    }

}