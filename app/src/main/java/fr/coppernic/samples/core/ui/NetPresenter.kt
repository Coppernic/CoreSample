package fr.coppernic.samples.core.ui

import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher
import fr.coppernic.samples.core.R
import kotlinx.android.synthetic.main.fragment_net.*

class NetPresenter() {

    val textWatcher = object : TextWatcher {
        override
        fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        }

        override
        fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

        }

        override
        fun afterTextChanged(editable: Editable?) {

            if (editable?.length != 7 || editable?.length != 15) {
                print("OKK")
            }
        }

        fun isEmptyField(edit: String?, layout: TextInputLayout, message: String): Boolean {
            if (edit != null && !edit.trim().isEmpty()) {
                layout.error = message
                layout.isErrorEnabled = true
            } else layout.isErrorEnabled = false
            return false
        }

        fun isValidIp(edit: String): Boolean {
            val regexIp = android.util.Patterns.IP_ADDRESS.toRegex()
            if (edit.matches(regexIp)) {
                isValidIp(edit)
            }
            return false
        }

        fun isValidMask(edit: String, regex: Regex): Boolean {
            if (edit.matches(regex)) {
                return true
            }
            return false
        }

        fun fromMasktoPrefix(edit: String): Int {
            return Integer.bitCount(edit.toInt())
        }

        @Throws(IllegalArgumentException::class)
        fun prefixLengthToNetmaskInt(prefixLength: Int): Int {
            if (prefixLength < 0 || prefixLength > 32) {
                throw IllegalArgumentException("Invalid prefix length (0 <= prefix <= 32)")
            }
            val value = -0x1 shl 32 - prefixLength //ou val value = -0x1 shr 32 - prefixLength
            return Integer.reverseBytes(value)
        }
    }
    @Throws(IllegalArgumentException::class)

    fun prefixLengthToNetmaskInt(prefixLength: Int): Int {
        if (prefixLength < 0 || prefixLength > 32) {
            throw IllegalArgumentException("Invalid prefix length (0 <= prefix <= 32)")
        }
        val value = -0x1 shl 32 - prefixLength //ou val value = -0x1 shl 32 - prefixLength
        return Integer.reverseBytes(value)
    }
}