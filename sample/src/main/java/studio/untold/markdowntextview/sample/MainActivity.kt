package studio.untold.markdowntextview.sample

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import studio.untold.MarkdownTextView

class MainActivity : AppCompatActivity() {

    private val mStringPlainOl = "This is just a plain ol' string that shouldn't match anything in the parser until you decide to change it."

    private val mStringThorough = "~strike *bold /italic _underline_/*~"

    private val mStringMultiLine = "Lorem ipsum dolor sit `amet`, consectetur `adipiscing` elit. Cras sed leo a ipsum " +
            "porttitor mollis et eu lectus. `Duis quis tellus non tellus venenatis pharetra nec ut sem. Pellentesque " +
            "aliquam sapien libero, non hendrerit nisi varius vel.` Pellentesque ac ex faucibus massa commodo finibus " +
            "ut id quam. `Nunc` faucibus `augue` vitae dapibus finibus."

    private val mStringComprehensive = "Here's a *long* string that ~should~ will comprehensively hit /every/ feature of the parser.\n" +
            "\n" +
            "The inline HTML here should be <div>escaped</div>. So should this ampersand: &\n" +
            "\n" +
            "```\n" +
            "The *bolds*, _underlines_, and /italics/ inside this code block should not be rendered.\n" +
            "This url: https://untoldhq.com/ should remain in tact.\n" +
            "This `inlineCodeBlock` should not be rendered\n" +
            "> Nor this blockquote\n" +
            ">>> Or this extended one\n" +
            "```\n" +
            "\n" +
            "Nesting should work _in *any /order*, including/ this_, With the exception of `inline *code* blocks`\n" +
            "\n" +
            "The following URLs should be preserved, without any additional markup:\n" +
            "\n" +
            "https://untoldhq.com/*markup*/_in_/`urls`\n" +
            "untold.studio\n" +
            "untold:///studio/cd00132cfea-caaceedd33d21-adec312389\n" +
            "\n" +
            "A series of newlines:\n" +
            "\n" +
            "\n" +
            "\n" +
            "Should be replaced by a series of <br /> tags\n" +
            "\n" +
            ">>> This blockquote should encapsulate everything from this point to the end of the document\n" +
            "Nestable pattern types should _still_ /be/ *rendered* here.\n" +
            "```Code blocks inside of blockquotes should be rendered```"

    private val mOnTextClickListener = View.OnClickListener { view ->
        val markdownTextView = view as MarkdownTextView
        val dialog = EditTextDialogFragment()
        dialog.editText = markdownTextView
        dialog.show(supportFragmentManager, "dialog_edit_text")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
            navigationBarColor = ResourcesCompat.getColor(resources, R.color.navigation_bar_color, theme)
            setBackgroundDrawable(getDrawable(R.drawable.gradient_untold))
        }

        setSupportActionBar(findViewById(R.id.toolbar)) // we can then get this via `toolbar`
        supportActionBar?.setDisplayShowTitleEnabled(false)
        findViewById<MarkdownTextView>(R.id.text_app_title).setMarkdownText("Mark ~up~ downTextView")
    }

    override fun onStart() {
        super.onStart()
        findViewById<MarkdownTextView>(R.id.text_1).setMarkdownText(mStringPlainOl)
        findViewById<MarkdownTextView>(R.id.text_2).setMarkdownText(mStringPlainOl)

        findViewById<MarkdownTextView>(R.id.text_3).setMarkdownText(mStringThorough)
        findViewById<MarkdownTextView>(R.id.text_4).setMarkdownText(mStringThorough)

        findViewById<MarkdownTextView>(R.id.text_5).setMarkdownText(mStringMultiLine)
        findViewById<MarkdownTextView>(R.id.text_6).setMarkdownText(mStringMultiLine)

        findViewById<MarkdownTextView>(R.id.text_7).setMarkdownText(mStringComprehensive)
        findViewById<MarkdownTextView>(R.id.text_8).setMarkdownText(mStringComprehensive)

        findViewById<MarkdownTextView>(R.id.text_1).setOnClickListener(mOnTextClickListener)
        findViewById<MarkdownTextView>(R.id.text_2).setOnClickListener(mOnTextClickListener)
        findViewById<MarkdownTextView>(R.id.text_3).setOnClickListener(mOnTextClickListener)
        findViewById<MarkdownTextView>(R.id.text_4).setOnClickListener(mOnTextClickListener)
        findViewById<MarkdownTextView>(R.id.text_5).setOnClickListener(mOnTextClickListener)
        findViewById<MarkdownTextView>(R.id.text_6).setOnClickListener(mOnTextClickListener)
        findViewById<MarkdownTextView>(R.id.text_7).setOnClickListener(mOnTextClickListener)
        findViewById<MarkdownTextView>(R.id.text_8).setOnClickListener(mOnTextClickListener)
    }

}
