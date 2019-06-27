package fr.coppernic.samples.core.ui

import kotlinx.android.synthetic.main.fragment_net.*
import org.junit.After
import org.junit.Before
import org.junit.Test

import fr.coppernic.sdk.net.cone2.StaticIpConfig

import org.junit.Assert.*

class NetFragmentTest {

    @Before
    fun before() {

        val regexMask = Regex(pattern = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}\\/\\d+")
        val mask = "255.255.255.255"
        val maskfield = regexMask.matches(mask)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun onCreateView() {
    }

    @Test
    fun onViewCreated() {
    }

}