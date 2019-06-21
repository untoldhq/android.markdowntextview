/*
 * MarkdownManager.kt
 * markdowntextview
 *
 * Created by Alberto Martinez on 06/17/19.
 * Copyright © 2019 Untold Studio. All rights reserved.
 */

package studio.untold.internal.markdowntextview

import android.os.Build
import android.text.*
import android.util.LruCache
import android.view.View
import studio.untold.markdowntextview.BuildConfig
import studio.untold.MarkdownTextView
import java.lang.ref.WeakReference
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

internal object MarkdownManager {

    /* STATIC */
    private val MEMORY_CACHE = LruCache<String, Spanned>(50)

    private val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()
    private val PROCESS_QUEUE by lazy { LinkedBlockingQueue <Runnable>() }
    private val THREAD_POOL by lazy {
        ThreadPoolExecutor(
            NUMBER_OF_CORES,
            NUMBER_OF_CORES, 1, TimeUnit.SECONDS,
            PROCESS_QUEUE
        )
    }
    /* STATIC */

    fun clearCache(messageId: String?) {
        if (messageId == null) MEMORY_CACHE.evictAll()
        else MEMORY_CACHE.remove(messageId)
    }
    fun loadMessageText(
        textView: MarkdownTextView,
        tagHandler: Html.TagHandler,
        messageId: String,
        inString: String,
        cacheFlag: Boolean = false) {
        if (cacheFlag) clearCache(messageId)
        THREAD_POOL.execute(
            LoadMessageRunnable(
                textView,
                tagHandler,
                messageId,
                inString
            )
        )
    }

    class LoadMessageRunnable(
        textView: MarkdownTextView,
        private val tagHandler: Html.TagHandler,
        private val messageId: String,
        private val inString: String): Runnable {
        private val weakTextView = WeakReference(textView)
        override fun run() {
            try {
                val inCache = MEMORY_CACHE.get(messageId)
                val result = inCache ?: process(inString)?.also {
                        MEMORY_CACHE.put(messageId, it)
                    }
                if (result == null) {
                    weakTextView.get()?.let {
                        it.post {
                            it.text = inString
                            it.visibility = if (inString.isEmpty()) View.GONE else View.VISIBLE
                        }
                    }
                } else {
                    weakTextView.get()?.let {
                        it.post {
                            it.text = result
                            it.visibility = if (result.isEmpty()) View.GONE else View.VISIBLE
                        }
                    }
                }
            } catch(err: Exception) {
                if (BuildConfig.DEBUG) err.printStackTrace()
            }
        }

        private fun process(inString: String): Spanned? {
            var spanned: Spanned? = null
            try {
                val toHtmlMarkdown = MarkdownParser.process(inString)
                spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val imageGetter = Html.ImageGetter { null }
                    Html.fromHtml(toHtmlMarkdown, Html.FROM_HTML_MODE_COMPACT, imageGetter, tagHandler)
                } else {
                    @Suppress("DEPRECATION")
                    Html.fromHtml(toHtmlMarkdown)
                }
            } catch(err: Exception) {
                if (BuildConfig.DEBUG) err.printStackTrace()
            }
            return spanned
        }

    }
}