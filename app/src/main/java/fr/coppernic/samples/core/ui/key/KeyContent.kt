package fr.coppernic.samples.core.ui.key

import android.content.Context
import android.util.SparseArray
import fr.coppernic.sdk.mapping.utils.MapperUtils
import java.util.*

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 *
 */
class KeyContent(context: Context?) {
    /**
     * An array of sample (dummy) items.
     */
    val items: MutableList<KeyItem> = ArrayList()

    /**
     * A map of sample (dummy) items, by ID.
     */
    val itemMap = SparseArray<KeyItem>()

    private fun addItem(item: KeyItem) {
        items.add(item)
        itemMap.put(item.code, item)
    }

    init {
        for ((k, v) in MapperUtils.getKeyMap(context)) {
            addItem(KeyItem(k, v))
        }
        addItem(KeyItem("BARCODE_SCAN", MapperUtils.getBarcodeMappingKeyCode()))
    }
}
