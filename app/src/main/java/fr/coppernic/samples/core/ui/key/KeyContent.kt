package fr.coppernic.samples.core.ui.key

import android.content.Context
import android.util.SparseArray
import fr.coppernic.samples.core.R
import fr.coppernic.sdk.mapping.utils.MapperUtils
import java.util.*
import kotlin.math.min

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 *
 */
class KeyContent() {
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
       for ( (k,v) in MapperUtils.KEY_MAP){
            addItem(KeyItem(k,v))
        }
        addItem(KeyItem("BARCODE_SCAN", MapperUtils.getBarcodeMappingKeyCode()))
    }
}