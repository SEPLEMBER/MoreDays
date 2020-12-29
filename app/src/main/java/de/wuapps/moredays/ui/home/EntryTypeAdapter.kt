package de.wuapps.moredays.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.wuapps.moredays.R


class EntryTypeAdapter() :
    ListAdapter<EntryType, EntryTypeAdapter.EntryTypeViewHolder>(EntryTypeDiffCallback) {

    class EntryTypeViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        private val textViewInfo: TextView = itemView.findViewById(R.id.textViewInfo)
        private val imageViewSymbol: ImageView = itemView.findViewById(R.id.imageViewSymbol)
        private var currentEntryType: EntryType? = null

        init {
            itemView.setOnClickListener {
                currentEntryType!!.offerInput(itemView.context)
            }
        }

        fun bind(entryType: EntryType) {
            currentEntryType = entryType
            textViewName.text = entryType.getName()
            textViewInfo.text = entryType.getInfo()
            //imageViewSymbol.setBackgroundResource(entryType.getSymbol())

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryTypeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_entry_type, parent, false)
        return EntryTypeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntryTypeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object EntryTypeDiffCallback : DiffUtil.ItemCallback<EntryType>() {
    override fun areItemsTheSame(oldItem: EntryType, newItem: EntryType): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: EntryType, newItem: EntryType): Boolean {
        return oldItem.getId() == newItem.getId()
    }
}
