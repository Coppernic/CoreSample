package fr.coppernic.samples.core.ui

import android.support.design.widget.TextInputLayout
import android.widget.EditText
import fr.coppernic.test.robolectric.RobolectricTest
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.robolectric.Robolectric
import timber.log.Timber

class NetPresenterTest : RobolectricTest() {

    lateinit var presenter: NetPresenter2

    @Before
    fun setUp() {
        presenter = NetPresenter2()
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
        assertTrue(presenter.isValidMask("1"))
        assertTrue(presenter.isValidMask("10"))
        assertTrue(presenter.isValidMask("24"))

    }

    @Test
    fun fromMasktoPrefix() {
        assertEquals(null, presenter.fromMasktoPrefix(""))
        assertEquals(10, presenter.fromMasktoPrefix("198.168.0.0"))
        assertEquals(16, presenter.fromMasktoPrefix("255.255.25.2"))
        assertEquals(6, presenter.fromMasktoPrefix("1.0.0.0"))
        assertEquals(1, presenter.fromMasktoPrefix("1"))
        assertEquals(2, presenter.fromMasktoPrefix("10"))
        assertEquals(2, presenter.fromMasktoPrefix("24"))

    }
}