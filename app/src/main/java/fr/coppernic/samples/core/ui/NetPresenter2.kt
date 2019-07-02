package fr.coppernic.samples.core.ui

import android.util.Patterns.*

class NetPresenter2 {

    private val regexIp: Regex = IP_ADDRESS.toRegex()
    private val regexMask: Regex = "[0-9]{1,3}".toRegex()

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
        val m = IP_ADDRESS.matcher(mask)
        if (m.find()) for (i in 0..m.groupCount()) {
            //Timber.v(m.group(i))
            val digitMask = digitsAndPlusOnly(m).toInt()
            val binaryPrefix = decimalToBinary(digitMask)
            val prefix = Integer.bitCount(binaryPrefix.toInt())
            return prefix
        }
        return null
    }

    fun decimalToBinary(decimalNumber: Int, binaryString: String = "") : String {
        while (decimalNumber > 0) {
            val temp = "${binaryString}${decimalNumber%2}"
            return decimalToBinary(decimalNumber/2, temp)
        }
        return binaryString.reversed()
    }
}
