package fr.coppernic.samples.core.ui.key

import androidx.annotation.NonNull
import java.io.Serializable

/**
 * A dummy item representing a piece of content.
 */
class KeyItem internal constructor(val name: String, val code: Int) : Serializable {
    @NonNull
    override fun toString(): String {
        return "KeyItem{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}'
    }

    companion object {
        val NONE = KeyItem("UNKNOWN", 0)
    }

}