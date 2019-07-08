package fr.coppernic.samples.core.ui

import android.text.Editable
import android.widget.EditText
import fr.coppernic.test.robolectric.RobolectricTest
import org.intellij.lang.annotations.JdkConstants
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class NetPresenterTest : RobolectricTest() {


    lateinit var presenter: NetPresenter

    @Before
    fun setUp() {
        presenter = NetPresenter()
    }

    @Test
    fun isValidIpTest() {
        assertFalse(presenter.isValidIp(""))
        assertFalse(presenter.isValidIp("255.255.255.255.255"))
        assertFalse(presenter.isValidIp("25555555555555"))
        assertTrue(presenter.isValidIp("192.168.0.0"))
        assertTrue(presenter.isValidIp("255.255.255.255"))
        assertTrue(presenter.isValidIp("1.0.0.0"))
    }

    @Test
    fun isValidMask() {
        assertFalse(presenter.isValidMask(""))
        assertFalse(presenter.isValidMask("255.255.255.255.255"))
        assertFalse(presenter.isValidMask("25555555555555"))
        assertFalse(presenter.isValidMask("0"))
        assertFalse(presenter.isValidMask("25"))
        assertTrue(presenter.isValidMask("192.168.0.0"))
        assertTrue(presenter.isValidMask("255.255.255.255"))
        assertTrue(presenter.isValidMask("1.0.0.0"))
        assertTrue(presenter.isValidMask("1.1.1.1"))

    }

    @Test
    fun fromMasktoPrefix() {
        assertEquals(null, presenter.fromMasktoPrefix(""))
        assertEquals(5, presenter.fromMasktoPrefix("192.168.0.0"))
        assertEquals(32, presenter.fromMasktoPrefix("255.255.255.255"))
        assertEquals(9, presenter.fromMasktoPrefix("255.128.0.0"))
        assertEquals(8, presenter.fromMasktoPrefix("255.0.0.0"))
        assertEquals(21, presenter.fromMasktoPrefix("255.255.248.0"))
        assertEquals(null, presenter.fromMasktoPrefix("1"))
        assertEquals(null, presenter.fromMasktoPrefix("10"))
        assertEquals(null, presenter.fromMasktoPrefix("24"))

    }
}