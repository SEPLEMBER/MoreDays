package de.wuapps.moredays.ui.journal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.Journal


class JournalAdapter :
    ListAdapter<Journal, JournalAdapter.JournalViewHolder>(JournalDiffCallback) {

    class JournalViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        private var currentJournal: Journal? = null

        init {
            itemView.setOnClickListener {
                val journalDate = currentJournal!!.date
                //it.findNavController()
                //    .navigate(JournalListFragmentDirections.navigationJournalDay(journalDate))

            }
        }

        fun bind(journal: Journal) {
            currentJournal = journal

            textViewName.text = journal.affirmation
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_journal, parent, false)
        return JournalViewHolder(view)
    }

    override fun onBindViewHolder(holder: JournalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object JournalDiffCallback : DiffUtil.ItemCallback<Journal>() {
    override fun areItemsTheSame(oldItem: Journal, newItem: Journal): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Journal, newItem: Journal): Boolean {
        return oldItem.date == newItem.date
    }
}
