package fr.coppernic.samples.core.ui

import android.util.Patterns.*
import kotlin.NullPointerException

class NetPresenter {

    private val regexIp: Regex = IP_ADDRESS.toRegex()
    internal val regexMask: Regex = ("(((255\\.){3}(255|254|252|248|240|224|192|128|0+))|" +
            "((255\\.){2}(255|254|252|248|240|224|192|128|0+)\\.0)|" +
            "((255\\.)(255|254|252|248|240|224|192|128|0+)(\\.0+){2})|" +
            "((255|254|252|248|240|224|192|128|0+)(\\.0+){3}" +
            "|(([0-9])|(1[0-9])|(2[0-4]))))").toRegex()
    private val regexPrefix: Regex = "[0-9]|^[0-9]{2}\$".toRegex()

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

    fun fromMaskPrefix(mask: String): Int {
        val m = IP_ADDRESS.matcher(mask)
        if (m.find()) {
            for (i in 0..m.groupCount()) {
                val g1 = Integer.bitCount(m.group(2).toInt())
                val g2 = Integer.bitCount(m.group(3).toInt())
                val g3 = Integer.bitCount(m.group(4).toInt())
                val g4 = Integer.bitCount(m.group(5).toInt())

                return g1 + g2 + g3 + g4
            }
        } else if (mask.matches(regexPrefix)) {
            return mask.toInt()
        }
        throw NullPointerException()
    }
}

