package fr.coppernic.samples.core.ui

import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher

class NetPresenter (private var netFragment: NetFragment?) {
    fun initialized() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun cleanup() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    val textWatcher = object : TextWatcher {

        override
        fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        }

        override
        fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        }

        override
        fun afterTextChanged(editable: Editable) {

            if (editable.length != 7 || editable.length != 15) {
                afterTextChanged(editable)
            } else error("You need at least 8 characters")
        }

        fun isEmptyField(editText: String?, layout: TextInputLayout, message: String): Boolean {
            if (editText != null && !editText.trim().isEmpty()) {
                layout.error = message
                layout.isErrorEnabled = true
            } else layout.isErrorEnabled = false
            return false
        }

        fun isValidIp(editText: String): Boolean {
            val regexIp = android.util.Patterns.IP_ADDRESS.toRegex()
            if (editText.matches(regexIp)) {
                isValidIp(editText)
            }
            return false
        }

        fun isValidMask(editText: String, regex: Regex): Boolean {
            if (editText.matches(regex)) {
                return true
            }
            return false
        }

        fun fromMasktoPrefix(editTexT: String): Int {
            return Integer.bitCount(editTexT.toInt())
        }

        /**@Throws(IllegalArgumentException::class)
        fun prefixLengthToNetmaskInt(prefixLength: Int): Int {
        if (prefixLength < 0 || prefixLength > 32) {
        throw IllegalArgumentException("Invalid prefix length (0 <= prefix <= 32)")
        }
        val value = -0x1 shl 32 - prefixLength //ou val value = -0x1 shr 32 - prefixLength
        return Integer.reverseBytes(value)
        }
        }*/
    }
}