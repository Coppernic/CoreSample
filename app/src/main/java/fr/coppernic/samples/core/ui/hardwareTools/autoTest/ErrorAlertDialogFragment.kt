package fr.coppernic.samples.core.ui.hardwareTools.autoTest

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import fr.coppernic.samples.core.R

class ErrorAlertDialogFragment(private val callback: HdwrErrorAlertDialogListener) : DialogFragment() {
    companion object {
        fun newInstance(title: String?, listener: HdwrErrorAlertDialogListener): ErrorAlertDialogFragment {
            val frag = ErrorAlertDialogFragment(listener)
            val args = Bundle()
            args.putString("message", title)
            frag.arguments = args
            return frag
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getString("message")
        return AlertDialog.Builder(activity)
                .setMessage(title)
                .setPositiveButton(R.string.dial_positive
                ) { _, _ -> callback.onPositiveClick() }
                .setNegativeButton(R.string.dial_negative
                ) { _, _ -> callback.onNegativeClick() }
                .create()
    }
}