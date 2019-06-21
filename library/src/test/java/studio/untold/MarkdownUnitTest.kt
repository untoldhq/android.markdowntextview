/*
 * MessageMarkdownUnitTestTest.kt
 * markdowntextview
 *
 * Created by Alberto Martinez on 06/17/19.
 * Copyright © 2019 Untold Studio. All rights reserved.
 */

package studio.untold

import org.junit.Assert.assertEquals
import org.junit.Test
import studio.untold.internal.markdowntextview.MarkdownParser
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream

class MarkdownUnitTest {

    private fun readResource(resFileName: String): String {
        val inputStream = this.javaClass.classLoader?.getResourceAsStream(resFileName)
        val bis = BufferedInputStream(inputStream)
        val buf = ByteArrayOutputStream()
        var reader = bis.read()
        while (reader != -1) {
            buf.write(reader)
            reader = bis.read()
        }
        return buf.toString("UTF-8")
    }

    @Test
    fun matchless() {
        val strToParse = "This is just a plain old string that shouldn't match anything in the parser"
        val result = MarkdownParser.process(strToParse)
        assertEquals(strToParse, result)
    }

    @Test
    fun inlineCodeBlocks() {
        val strToParse = "To the client team. We have plan to add one additional parameter on `getMessage` API. That param is \uD83E\uDD13 `client✊\uD83C\uDFFBDeviceId`, which \uD83D\uDC50\uD83C\uDFFB we use for notification purpose. I just need confirmation from you guys whether adding that param is possible from the client side (i.e. it is possible to put `clientDeviceId` when submitting `getMessage` API? )"
        val strToExpect = "To the client team. We have plan to add one additional parameter on &zwj;<inlinecode>getMessage</inlinecode> API. That param is \uD83E\uDD13 &zwj;<inlinecode>client✊\uD83C\uDFFBDeviceId</inlinecode>, which \uD83D\uDC50\uD83C\uDFFB we use for notification purpose. I just need confirmation from you guys whether adding that param is possible from the client side (i.e. it is possible to put &zwj;<inlinecode>clientDeviceId</inlinecode> when submitting &zwj;<inlinecode>getMessage</inlinecode> API? )"

        val result = MarkdownParser.process(strToParse)

        assertEquals(strToExpect, result)
    }

    @Test
    fun fullCodeBlocks() {
        val strToParse = "\"\"" +
                "Here's an example of a nestable pattern:\n" +
                "```" +
                "struct BoldPattern: ParsingPattern {\n" +
                "    static func replace(inString string: String) -> (String, [Token]) {\n" +
                "        var mutableCopy = NSMutableString(string: string)\n" +
                "        performReplacement(inString: &mutableCopy, pattern: \"\\\\*([^\\\\*]+)\\\\*\", template: \"<strong>\$1</strong>\")\n" +
                "        return (String(mutableCopy), [])\n" +
                "    }\n" +
                "}\n" +
                "```\n" +
                "And here's an exclusive pattern:\n" +
                "```\n" +
                "struct InlineCodeBlockPattern: ParsingPattern {\n" +
                "    static func replace(inString string: String) -> (String, [Token]) {\n" +
                "        return insertReplacementTokens(inString: string, pattern:  \"`([^`]+)`\", leftMarkup: \"<code>\", rightMarkup: \"</code>\")\n" +
                "    }\n" +
                "}\n" +
                "```" +
                "\"\""

        val strToExpect = "\"\"" +
                "Here's an example of a nestable pattern:<br /><fullcodeblock><p>" +
                "struct BoldPattern: ParsingPattern {\n" +
                "    static func replace(inString string: String) -&gt; (String, [Token]) {\n" +
                "        var mutableCopy = NSMutableString(string: string)\n" +
                "        performReplacement(inString: &amp;mutableCopy, pattern: \"\\\\*([^\\\\*]+)\\\\*\", template: \"&lt;strong&gt;\$1&lt;/strong&gt;\")\n" +
                "        return (String(mutableCopy), [])\n" +
                "    }\n" +
                "}\n" +
                "</p></fullcodeblock><br />And here's an exclusive pattern:<br /><fullcodeblock><p>\n" +
                "struct InlineCodeBlockPattern: ParsingPattern {\n" +
                "    static func replace(inString string: String) -&gt; (String, [Token]) {\n" +
                "        return insertReplacementTokens(inString: string, pattern:  \"`([^`]+)`\", leftMarkup: \"&lt;code&gt;\", rightMarkup: \"&lt;/code&gt;\")\n" +
                "    }\n" +
                "}\n" +
                "</p></fullcodeblock>" +
                "\"\""

        val result = MarkdownParser.process(strToParse)

        assertEquals(strToExpect, result)
    }

    // 03/14/2019 italic <em></em> tags removed for now which break these. Head to MarkdownParser::italicPattern and restore the tags to fix
    @Test
    fun nestedPatterns() {
        val strToParse = "To the client team. We _have *plan* to add_ one additional parameter on `getMessage` API. That param is \uD83E\uDD13 `client✊\uD83C\uDFFBDeviceId`, which \uD83D\uDC50\uD83C\uDFFB we use for notification purpose. I just need confirmation from you guys /whether/ adding that param is *possible* from the client side (i.e. it is possible to put `clientDeviceId` when submitting `getMessage` API? )"
        val strToExpect = "To the client team. We <u>have <b>plan</b> to add</u> one additional parameter on &zwj;<inlinecode>getMessage</inlinecode> API. That param is \uD83E\uDD13 &zwj;<inlinecode>client✊\uD83C\uDFFBDeviceId</inlinecode>, which \uD83D\uDC50\uD83C\uDFFB we use for notification purpose. I just need confirmation from you guys <em>whether</em> adding that param is <b>possible</b> from the client side (i.e. it is possible to put &zwj;<inlinecode>clientDeviceId</inlinecode> when submitting &zwj;<inlinecode>getMessage</inlinecode> API? )"

        val result = MarkdownParser.process(strToParse)

        assertEquals(strToExpect, result)
    }

    @Test
    fun urlIntegrity() {
        val strToParse = readResource("urlintegrity.txt")
        val strToExpect = strToParse.replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br />")
        val result = MarkdownParser.process(strToParse).replace("&zwj;<url>", "").replace("</url>", "")
        assertEquals(strToExpect, result)
    }

    @Test
    fun filePathsInCodeBlocks() {
        val strToParse = "`src/helper/untold-parser/parsers/ExclusivePatterns.js`"
        val strToExpect = "&zwj;<inlinecode>src/helper/untold-parser/parsers/ExclusivePatterns.js</inlinecode>"

        val result = MarkdownParser.process(strToParse)

        assertEquals(strToExpect, result)
    }

    @Test
    fun nestedFormat() {
        val strToParse = "~strike *bold /italic _underline_/*~"
        val strToExpect = "<strike>strike <b>bold <em>italic <u>underline</u></em></b></strike>"

        val result = MarkdownParser.process(strToParse)

        assertEquals(strToExpect, result)
    }

    @Test
    fun wordBoundaries() {
        data class PatternMeta (
            val type: MarkdownPatternInterface,
            val controlCharacters: String,
            val replacementTag: String
        )
        val patterns = arrayOf (
            PatternMeta(MarkdownParser.BoldPattern, "*", "b"),
            PatternMeta(MarkdownParser.ItalicPattern, "/", "em"),
            PatternMeta(MarkdownParser.UnderlinePattern, "_", "u"),
            PatternMeta(MarkdownParser.StrikethroughPattern, "~", "strike")
        )
        val strings = mapOf(
            Pair("hey [CONTROL]you[CONTROL] guys", "hey <[REPLACEMENT]>you</[REPLACEMENT]> guys"),
            Pair("[CONTROL]begin[CONTROL] a string", "<[REPLACEMENT]>begin</[REPLACEMENT]> a string"),
            Pair("end a [CONTROL]string[CONTROL]", "end a <[REPLACEMENT]>string</[REPLACEMENT]>"),
            Pair("multiple [CONTROL]in[CONTROL] [CONTROL]succession[CONTROL]", "multiple <[REPLACEMENT]>in</[REPLACEMENT]> <[REPLACEMENT]>succession</[REPLACEMENT]>"),
            Pair("¯\\[CONTROL](ツ)[CONTROL]/¯", "¯\\[CONTROL](ツ)[CONTROL]/¯"),
            Pair("shruggy ¯\\[CONTROL](ツ)[CONTROL]/¯ in the middle", "shruggy ¯\\[CONTROL](ツ)[CONTROL]/¯ in the middle"),
            Pair("multiple in a single word, my[CONTROL]php[CONTROL]variable[CONTROL]name", "multiple in a single word, my[CONTROL]php[CONTROL]variable[CONTROL]name"),
            Pair("[CONTROL]followed by punctuation[CONTROL]?", "<[REPLACEMENT]>followed by punctuation</[REPLACEMENT]>?")
        )

        for (meta in patterns) {
            for ( (base, expectation) in strings) {
                val replacedBase = base.replace("[CONTROL]", meta.controlCharacters)
                val replacedExpectation = expectation.replace("[REPLACEMENT]", meta.replacementTag).replace("[CONTROL]", meta.controlCharacters)
                val replaced = meta.type.replace(replacedBase)
                val actual = MarkdownParser.reinsertTokens(replaced.second, replaced.first)
                assertEquals(actual, replacedExpectation)
            }
        }
    }

    @Test
    fun wordBoundariesZWJ() {
        data class PatternMeta (
            val type: MarkdownPatternInterface,
            val controlCharacters: String,
            val replacementTag: String
        )
        val patterns = arrayOf (
            PatternMeta(MarkdownParser.InlineCodeBlockPattern, "`", "inlinecode")
        )
        val strings = mapOf(
            Pair("hey [CONTROL]you[CONTROL] guys", "hey &zwj;<[REPLACEMENT]>you</[REPLACEMENT]> guys"),
            Pair("[CONTROL]begin[CONTROL] a string", "&zwj;<[REPLACEMENT]>begin</[REPLACEMENT]> a string"),
            Pair("end a [CONTROL]string[CONTROL]", "end a &zwj;<[REPLACEMENT]>string</[REPLACEMENT]>"),
            Pair("multiple [CONTROL]in[CONTROL] [CONTROL]succession[CONTROL]", "multiple &zwj;<[REPLACEMENT]>in</[REPLACEMENT]> &zwj;<[REPLACEMENT]>succession</[REPLACEMENT]>"),
            Pair("¯\\[CONTROL](ツ)[CONTROL]/¯", "¯\\[CONTROL](ツ)[CONTROL]/¯"),
            Pair("shruggy ¯\\[CONTROL](ツ)[CONTROL]/¯ in the middle", "shruggy ¯\\[CONTROL](ツ)[CONTROL]/¯ in the middle"),
            Pair("multiple in a single word, my[CONTROL]php[CONTROL]variable[CONTROL]name", "multiple in a single word, my[CONTROL]php[CONTROL]variable[CONTROL]name"),
            Pair("[CONTROL]followed by punctuation[CONTROL]?", "&zwj;<[REPLACEMENT]>followed by punctuation</[REPLACEMENT]>?")
        )

        for (meta in patterns) {
            for ( (base, expectation) in strings) {
                val replacedBase = base.replace("[CONTROL]", meta.controlCharacters)
                val replacedExpectation = expectation.replace("[REPLACEMENT]", meta.replacementTag).replace("[CONTROL]", meta.controlCharacters)
                val replaced = meta.type.replace(replacedBase)
                val actual = MarkdownParser.reinsertTokens(replaced.second, replaced.first)
                assertEquals(actual, replacedExpectation)
            }
        }
    }

}