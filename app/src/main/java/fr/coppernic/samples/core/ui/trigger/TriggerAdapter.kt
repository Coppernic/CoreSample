package fr.coppernic.samples.core.ui.trigger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.coppernic.samples.core.R

class TriggerAdapter internal constructor(
    private val mValues: List<TriggerItem>,
    private    
    val listener: OnTriggerAdapterListener
) : RecyclerView.Adapter<TriggerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_trigger, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item = mValues[position]
        holder.tvLabel.text = holder.item.label
        if (holder.item.icon != null) {
            holder.icon.setImageDrawable(holder.item.icon)
        }
        holder.itemView.setOnClickListener { listener?.onTriggerChosen(holder.item) }
    }

    override fun getItemCount() = mValues.size

    internal interface OnTriggerAdapterListener {
        fun onTriggerChosen(item: TriggerItem)
    }

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val tvLabel: TextView = view.findViewById(R.id.content)
        val icon: ImageView = view.findViewById(R.id.icon)
        lateinit var item: TriggerItem
        override fun toString(): String {
            return super.toString() + " '" + tvLabel.text + "'"
        }
    }
}
