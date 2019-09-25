package fr.coppernic.samples.core.interactor

import android.annotation.SuppressLint
import android.content.Context
import android.net.ethernet.EthernetManager
import timber.log.Timber

private const val ETHERNET_SERVICE = "ethernet"

class NetInteractorImplCone : NetInteractor {

    private lateinit var manager: EthernetManager

    @SuppressLint("WrongConstant")
    override fun enableEthernet(context: Context, enable: Boolean) {
        manager = context.getSystemService(ETHERNET_SERVICE) as EthernetManager
        if (enable) {
            try {
                manager.setEnabled(true)
            } catch (e: SecurityException) {
                Timber.e(e.toString())
            }
        } else {
            try {
                manager.setEnabled(false)
            } catch (e: SecurityException) {
                Timber.e(e.toString())
            }

        }
    }

    override fun enableCradle(context: Context, enable: Boolean) {
    }
}