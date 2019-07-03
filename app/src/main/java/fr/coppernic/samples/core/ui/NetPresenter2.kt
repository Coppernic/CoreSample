package fr.coppernic.samples.core.ui

import android.util.Log
import android.util.LogPrinter
import android.util.Patterns.*
import timber.log.Timber
import java.math.BigDecimal
import java.math.BigInteger
import android.widget.Toast
import android.R


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
            val g1 = m.group(2).toInt()
            val g2 = m.group(3).toInt()
            val g3 = m.group(4).toInt()
            val g4 = m.group(5).toInt()

            val g1Count = Integer.bitCount(g1)
            val g2Count = Integer.bitCount(g2)
            val g3Count = Integer.bitCount(g3)
            val g4Count = Integer.bitCount(g4)

            val prefix = g1Count + g2Count + g3Count + g4Count
            return prefix
        }
        return null
    }
}
