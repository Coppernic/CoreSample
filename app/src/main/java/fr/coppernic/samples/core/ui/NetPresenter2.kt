package fr.coppernic.samples.core.ui

import android.widget.EditText


class NetPresenter2 {

    val regex: Regex = android.util.Patterns.IP_ADDRESS.toRegex()
     var isEmptyField = true

    fun isValidIp(editText: EditText,regex: Regex) {
    }

    fun isValidMask(editText: EditText,regex: Regex) {
    }

}