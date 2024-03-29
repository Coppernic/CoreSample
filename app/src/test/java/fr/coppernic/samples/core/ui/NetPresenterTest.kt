package fr.coppernic.samples.core.ui

import fr.coppernic.test.robolectric.RobolectricTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.security.InvalidParameterException

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

        try {
            presenter.fromMaskPrefix("random string")
            fail()
        } catch (e: InvalidParameterException) {}

        try {
            presenter.fromMaskPrefix("-1")
            fail()
        } catch (e: InvalidParameterException) {}

        assertEquals(0, presenter.fromMaskPrefix("0"))
        assertEquals(1, presenter.fromMaskPrefix("1"))
        assertEquals(9, presenter.fromMaskPrefix("9"))
        assertEquals(10, presenter.fromMaskPrefix("10"))
        assertEquals(24, presenter.fromMaskPrefix("24"))
        assertEquals(32, presenter.fromMaskPrefix("32"))
    }
}
