package studio.untold.markdowntextview.sample

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import studio.untold.MarkdownTextView

class EditTextDialogFragment : AppCompatDialogFragment() {

    var editText: MarkdownTextView? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!).apply {
            val view = activity?.layoutInflater?.inflate(R.layout.dialog_edit_text, null, false)
            setView(view)
            setCancelable(true)
        }
        return builder.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        return view
    }

    override fun onResume() {
        super.onResume()
        dialog.findViewById<EditText>(R.id.edit_text).setText(editText?.currentMarkdownText?:"")
        dialog.findViewById<View>(R.id.action_save).setOnClickListener {
            editText?.setMarkdownText(dialog.findViewById<EditText>(R.id.edit_text).text.toString())
            dialog.dismiss()
        }
    }
}