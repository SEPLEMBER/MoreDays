package de.wuapps.moredays.ui.home

import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.wuapps.moredays.R
import de.wuapps.moredays.ui.icons.IconManager


class EntryTypeAdapter(private val application: Application) :
    ListAdapter<EntryType, EntryTypeAdapter.EntryTypeViewHolder>(EntryTypeDiffCallback) {

    class EntryTypeViewHolder(itemView: View, private val application: Application) :
        RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        private val textViewInfo: TextView = itemView.findViewById(R.id.textViewInfo)
        private val imageViewSymbol: ImageView = itemView.findViewById(R.id.imageViewSymbol)
        private val cardView: CardView = itemView.findViewById(R.id.layout)
        private var currentEntryType: EntryType? = null

        init {
            itemView.setOnClickListener {
                currentEntryType!!.showInputDialog(itemView.context)
            }
        }

        fun bind(entryType: EntryType) {
            currentEntryType = entryType
            cardView.setCardBackgroundColor(entryType.getColor())
            textViewName.text = entryType.getName()
            textViewInfo.text = entryType.getInfo()
            imageViewSymbol.setImageDrawable(
                IconManager.getInstance(application).iconPack.getIcon(
                    entryType.getSymbol()
                )?.drawable)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryTypeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_entry_type, parent, false)
        return EntryTypeViewHolder(view, application)
    }

    override fun onBindViewHolder(holder: EntryTypeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object EntryTypeDiffCallback : DiffUtil.ItemCallback<EntryType>() {
    override fun areItemsTheSame(oldItem: EntryType, newItem: EntryType): Boolean {
        return oldItem.equals(newItem)
    }

    override fun areContentsTheSame(oldItem: EntryType, newItem: EntryType): Boolean {
        return oldItem.getId() == newItem.getId()
    }
}
