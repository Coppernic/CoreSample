package fr.coppernic.samples.core.utils

import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher

class RegexTextWatcher(private val regex: Regex,
                       private val message: String,
                       private val layout: TextInputLayout) : TextWatcher {


    override fun afterTextChanged(s: Editable) {
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (!s.contains(regex)) {
            layout.error = message
            layout.isErrorEnabled = true
        } else {
            layout.isErrorEnabled = false
        }
    }
}