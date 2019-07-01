package fr.coppernic.samples.core.ui

import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher


class NetPresenter2 {

    private val regexIp: Regex = android.util.Patterns.IP_ADDRESS.toRegex()
    private val regexMask: Regex = "[0-9]{1,3}".toRegex()
    private val textWatcher = object : TextWatcher {

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
    }

    fun isValidIp(ip: String): Boolean = ip.matches(regexIp)

    /**
     * Check if Mask is valid
     * @return true if Mask is valid, false if not
     * Check if Mask is Int
     * @return true if value of Mask Int is between 1 and 24
     */

    fun isValidMask(mask: String): Boolean = when {
        mask.matches(regexIp) -> true
        mask.matches(regexMask) -> {
            val maskValue = mask.toInt()
            maskValue in 1..24
        }
        else -> false
    }

    /**
     *
     * @return Mask String to Prefix Int
     */

    fun fromMasktoPrefix(mask: String): Int? {
        if (isValidMask(mask)) {
            return Integer.bitCount(mask.replace(".","").toInt())
        }
        return null
    }
}
