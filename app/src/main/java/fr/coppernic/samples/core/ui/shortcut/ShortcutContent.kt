package fr.coppernic.samples.core.ui.shortcut

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.util.Log
import fr.coppernic.samples.core.BuildConfig.DEBUG
import java.util.*

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 *
 */
internal class ShortcutContent(context: Context) {
    /**
     * An array of sample (dummy) items.
     */
    val items: MutableList<ShortcutItem> = ArrayList()

    private fun addItem(item: ShortcutItem) {
        items.add(item)
        //itemMap.put(item.label.toString(), item);
    }

    fun getActivityList(context: Context) {
        val pm = context.packageManager
        val launchAbleFilterIntent = Intent(Intent.ACTION_MAIN, null)
        launchAbleFilterIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resInfos = pm.queryIntentActivities(launchAbleFilterIntent, 0)
        Collections.sort(resInfos, ResolveInfo.DisplayNameComparator(pm))
        for (resolveInfo in resInfos) {
            val icon = resolveInfo.loadIcon(pm)
            val label = resolveInfo.loadLabel(pm)
            val cName = ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name)
            val launchIntent = Intent(Intent.ACTION_MAIN)
            launchIntent.component = cName
            launchIntent.setPackage(resolveInfo.activityInfo.packageName)
            launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            launchIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            if (DEBUG) {
                Log.v(TAG, label
                        .toString() + "\n    icon : " + icon
                        + "\n    " + cName
                        + "\n    " + launchIntent)
            }
            addItem(ShortcutItem(label, launchIntent, icon))
        }
    }

    companion object {
        private const val TAG = "ShortcutContent"
    }

    /**
     * A map of sample (dummy) items, by ID.
     */
//public final HashMap<String, ShortcutItem> itemMap = new HashMap<>();
    init {
        getActivityList(context)
    }
}
