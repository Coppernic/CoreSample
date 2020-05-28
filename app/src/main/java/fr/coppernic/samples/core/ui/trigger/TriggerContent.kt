package fr.coppernic.samples.core.ui.trigger

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import com.askey.mapping.KeyDefines
import fr.coppernic.samples.core.BuildConfig.DEBUG
import fr.coppernic.samples.core.R
import java.util.*

private const val TAG = "TriggerContent"
private const val REGEX_BARCODE = ".*barcode.*"
private const val REGEX_RFID = ".*rfid.*"

internal class TriggerContent(context: Context) {
    /**
     * An array of sample (dummy) items.
     */
    val items: MutableList<TriggerItem> = ArrayList()
    private fun addItem(item: TriggerItem) {
        Log.v(TAG, "Adding item $item")
        items.add(item)
    }

    fun getActivityList(context: Context) {
        val pm = context.packageManager
        val pInfos = pm.getInstalledPackages(
                PackageManager.GET_INTENT_FILTERS or PackageManager.GET_RECEIVERS)
        for (pInfo in pInfos) {
            if (DEBUG) {
                Log.d(TAG, "Package " + pInfo.packageName)
            }
            if (pInfo.receivers != null) {
                for (aInfo in pInfo.receivers) {
                    if (aInfo.name.matches(REGEX_BARCODE.toRegex())) {
                        if (DEBUG) {
                            Log.v(TAG, "Got barcode receiver " + aInfo.packageName + "/" + aInfo.name)
                        }
                        val intentUp = Intent(KeyDefines.INTENT_ACTION_BARCODE_STOP)
                        intentUp.setClassName(aInfo.packageName, aInfo.name)
                        val intentDown = Intent(KeyDefines.INTENT_ACTION_BARCODE_START)
                        intentDown.setClassName(aInfo.packageName, aInfo.name)
                        val icon = aInfo.loadIcon(pm)
                        addItem(TriggerItem(context.getString(R.string.label_trig_barcode, aInfo.loadLabel(pm)),
                                intentUp, intentDown,
                                icon))
                    } else if (aInfo.name.matches(REGEX_RFID.toRegex())) {
                        if (DEBUG) {
                            Log.v(TAG, "Got rfid receiver " + aInfo.packageName + "/" + aInfo.name)
                        }
                        val intentUp = Intent(KeyDefines.INTENT_ACTION_TRIG_STOP)
                        intentUp.setClassName(aInfo.packageName, aInfo.name)
                        val intentDown = Intent(KeyDefines.INTENT_ACTION_TRIG_START)
                        intentDown.setClassName(aInfo.packageName, aInfo.name)
                        val icon = aInfo.loadIcon(pm)
                        addItem(TriggerItem(context.getString(R.string.label_trig_rfid, aInfo.loadLabel(pm)),
                                intentUp, intentDown,
                                icon))
                    }
                }
            }
        }
    }

    init {
        getActivityList(context)
    }
}
