package fr.coppernic.samples.core.ui

import android.support.design.widget.TextInputLayout
import android.widget.EditText
import org.junit.Test

import org.junit.Assert.*
import timber.log.Timber

class NetPresenterTest {

    private val regexIpTest = android.util.Patterns.IP_ADDRESS.toRegex()
    private val regexMaskTest = Regex(pattern = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})")
    private val editIpMaskTest = "192.168.0.0"
    private val editFalseIpMaskTest = "255.255.255.255.555"
    private val editText = "jsqkdjqskdjq"

    @Test
    fun isEmptyFieldTest() {

        if (editText != null && !editText.trim().isEmpty()) {
           Timber.v("Error")
        } else
        Timber.v("OK")
    }

    @Test
    fun isValidIpTest() {
        if (editIpMaskTest.matches(regexIpTest)) {
            Timber.v("OK")
        }
    }

    @Test
    fun isUnvalidIp() {
        if (editIpMaskTest.matches(regexIpTest)) {
            Timber.v("Error")
        }
    }

    @Test
    fun isValidMaskTest() {
        if (editIpMaskTest.matches(regexMaskTest)) {
            Timber.v("OK")
        }
    }

    @Test
    fun fromMasktoPrefixTest() {
        assertEquals(10, Integer.bitCount(editIpMaskTest.replace(".", "").toInt()))
    }

    @Test
    fun prefixLengthToNetmaskIntTest() {
    }
}