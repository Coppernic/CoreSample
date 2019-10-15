package fr.coppernic.samples.core.ui

import android.util.Patterns.IP_ADDRESS
import java.security.InvalidParameterException

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
     * Transform an ip notation to a prefix int notation
     *
     * Ex : 255.255.255.0 -> 24
     *
     * If already an int, then return it
     *
     * If input is wrong, then throws InvalidParameterException
     *
     * @return prefix int
     *
     * @throws InvalidParameterException
     */
    fun fromMaskPrefix(mask: String): Int {
        val m = IP_ADDRESS.matcher(mask)
        return when {
            m.find() -> {
                var g1 = 0
                var g2 = 0
                var g3 = 0
                var g4 = 0

                for (i in 0..m.groupCount()) {
                    g1 = Integer.bitCount(m.group(2).toInt())
                    g2 = Integer.bitCount(m.group(3).toInt())
                    g3 = Integer.bitCount(m.group(4).toInt())
                    g4 = Integer.bitCount(m.group(5).toInt())
                }

                g1 + g2 + g3 + g4
            }
            mask.matches(regexPrefix) -> mask.toInt()
            else -> {
                throw InvalidParameterException()
            }
        }
    }
}

