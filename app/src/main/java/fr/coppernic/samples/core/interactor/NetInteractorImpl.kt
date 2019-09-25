package fr.coppernic.samples.core.interactor

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.EthernetManager
import fr.coppernic.sdk.utils.helpers.CpcApp
import timber.log.Timber


private const val ETHERNET_SERVICE = "ethernet"
private const val SET_CRADLE_ACTION = "android.net.ethernet.SET_CRADLE_ACTION"
private const val EXTRA_CRADLE_STATE = "cradle:state"

@SuppressLint("WrongConstant")

class NetInteractorImpl : NetInteractor {

    private lateinit var manager: EthernetManager

    override fun enableEthernet(context: Context, enable: Boolean) {
        manager = context.getSystemService(ETHERNET_SERVICE) as EthernetManager
        if (enable) {
            try {
                manager.setEthernetEnabled(2)
            } catch (e: SecurityException) {
                Timber.e(e.toString())
            }
        } else {
            try {
                manager.setEthernetEnabled(1)
            } catch (e: SecurityException) {
                Timber.e(e.toString())
            }
        }
    }

    override fun enableCradle(context: Context, enable: Boolean) {
        val intent = Intent(SET_CRADLE_ACTION)
        if (enable) {
            intent.putExtra(EXTRA_CRADLE_STATE, 1)
            CpcApp.sendBroadcast(context, intent)
        } else {
            intent.putExtra(EXTRA_CRADLE_STATE, 0)
            CpcApp.sendBroadcast(context, intent)
        }
    }
}
