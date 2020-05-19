package fr.coppernic.samples.core.ui.shortcut

import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import fr.coppernic.samples.core.R

class ShortcutAdapter internal constructor(private val mValues: List<ShortcutItem>, private val listener: OnShortcutAdapterListener) : RecyclerView.Adapter<ShortcutAdapter.ViewHolder>() {
    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_shortcut, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item = mValues[position]
        holder.tvLabel.text = holder.item.label
        if (holder.item.icon != null) {
            holder.icon.setImageDrawable(holder.item.icon)
        }
        holder.itemView.setOnClickListener { listener.onShortcutChosen(holder.item) }
    }

    override fun getItemCount() = mValues.size

    internal interface OnShortcutAdapterListener {
        fun onShortcutChosen(item: ShortcutItem)
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val tvLabel: TextView = view.findViewById(R.id.content)
        val icon: ImageView = view.findViewById(R.id.icon)
        lateinit var item: ShortcutItem

        @NonNull
        override fun toString(): String {
            return super.toString() + " '" + tvLabel.text + "'"
        }
    }
}
