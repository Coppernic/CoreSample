package fr.coppernic.samples.core.utils

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import fr.coppernic.sdk.utils.helpers.CpcApp.sendBroadcast


class ConnectivityHelper {

    private val SET_CRADLE_ACTION = "android.net.ethernet.SET_CRADLE_ACTION"
    private val EXTRA_CRADLE_STATE = "cradle:state"


    fun isNetworkConnected(context: Context?): Boolean {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo.isConnected
    }

    fun isEthernetConnected(context: Context?): Boolean = when {
        isNetworkConnected(context) -> {
            val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            cm.activeNetworkInfo.type == ConnectivityManager.TYPE_ETHERNET
        }
        else -> false
    }

    fun turnOnEthernetCradle(context: Context?) {
        val intent = Intent(SET_CRADLE_ACTION)
        intent.putExtra(EXTRA_CRADLE_STATE, 1)
        context?.let { sendBroadcast(it, intent) }
    }

    fun turnOffEthernetCradle(context: Context?) {
        val intent = Intent(SET_CRADLE_ACTION)
        intent.putExtra(EXTRA_CRADLE_STATE, 0)
        context?.let { sendBroadcast(it, intent) }
    }
}