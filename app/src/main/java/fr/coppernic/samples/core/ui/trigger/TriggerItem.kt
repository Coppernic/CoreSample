package fr.coppernic.samples.core.ui.trigger

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class TriggerItem : Parcelable {
    @NonNull
    val intentUp: Intent

    @NonNull
    val intentDown: Intent

    @NonNull
    val label: CharSequence

    @Nullable
    @Transient
    var icon: Drawable? = null

    internal constructor(
        label: CharSequence,
        intentUp: Intent,
        intentDown: Intent,
        icon: Drawable?
    ) {
        this.label = label
        this.intentUp = intentUp
        this.intentDown = intentDown
        this.icon = icon
    }

    private constructor(`in`: Parcel) {
        val s = `in`.readString()
        label = s ?: ""
        var pending = `in`.readParcelable<Intent>(Intent::class.java.classLoader)
        intentUp = pending ?: Intent()
        pending = `in`.readParcelable(Intent::class.java.classLoader)
        intentDown = pending ?: Intent()
    }

    override fun toString(): String {
        return "TriggerItem{" +
            "intentUp=" + intentUp +
            ", intentDown=" + intentDown +
            ", label=" + label +
            ", icon=" + icon +
            '}'
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(label.toString())
        dest.writeParcelable(intentUp, flags)
        dest.writeParcelable(intentDown, flags)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TriggerItem?> = object : Parcelable.Creator<TriggerItem?> {
            override fun createFromParcel(`in`: Parcel): TriggerItem? {
                return TriggerItem(`in`)
            }

            override fun newArray(size: Int): Array<TriggerItem?> {
                return arrayOfNulls(size)
            }
        }
    }
}
