package fr.coppernic.samples.core.ui.key

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import fr.coppernic.samples.core.R

class KeyAdapter internal constructor(private val mValues: List<KeyItem>, private val mListener: OnKeyAdapterListener) : androidx.recyclerview.widget.RecyclerView.Adapter<KeyAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_key, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = mValues[position]
        holder.mContentView.text = mValues[position].name
        holder.itemView.setOnClickListener { mListener?.onKeyChosen(holder.mItem) }
    }

    override fun getItemCount() = mValues.size

    inner class ViewHolder internal constructor(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val mContentView: TextView = view.findViewById(R.id.content)
        lateinit var mItem: KeyItem
        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }

    }

    internal interface OnKeyAdapterListener {
        fun onKeyChosen(item: KeyItem)
    }

}