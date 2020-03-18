package fr.coppernic.samples.core.ui.shortcut

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.annotation.Nullable

/**
 * A dummy item representing a piece of content.
 */
class ShortcutItem : Parcelable {
    @NonNull
    val launchIntent: Intent
    @NonNull
    val label: CharSequence
    @Nullable
    @Transient
    var icon: Drawable? = null

    private constructor(@NonNull label: CharSequence, @NonNull launchIntent: Intent) {
        this.label = label
        this.launchIntent = launchIntent
    }

    internal constructor(@NonNull label: CharSequence, @NonNull launchIntent: Intent, @Nullable icon: Drawable?) {
        this.label = label
        this.launchIntent = launchIntent
        this.icon = icon
    }

    private constructor(`in`: Parcel) {
        val s = `in`.readString()
        label = s ?: ""
        val pending = `in`.readParcelable<Intent>(Intent::class.java.classLoader)
        launchIntent = pending ?: Intent()
    }

    @NonNull
    override fun toString(): String {
        return "KeyItem{" +
                "name='" + label + '\'' +
                ", launchIntent='" + launchIntent + '\'' +
                '}'
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(label.toString())
        dest.writeParcelable(launchIntent, flags)
    }

    companion object {
        val NONE = ShortcutItem("NONE", Intent())
        @JvmField
        val CREATOR: Parcelable.Creator<ShortcutItem?> = object : Parcelable.Creator<ShortcutItem?> {
            override fun createFromParcel(`in`: Parcel): ShortcutItem? {
                return ShortcutItem(`in`)
            }

            override fun newArray(size: Int): Array<ShortcutItem?> {
                return arrayOfNulls(size)
            }
        }
    }
}
