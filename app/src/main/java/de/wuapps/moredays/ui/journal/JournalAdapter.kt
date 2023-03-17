package de.wuapps.moredays.ui.journal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.wuapps.moredays.R
import de.wuapps.moredays.database.converter.DateTypeConverter
import de.wuapps.moredays.database.entity.Journal
import de.wuapps.moredays.databinding.ListItemJournalBinding
import java.text.SimpleDateFormat


class JournalAdapter :
    ListAdapter<Journal, JournalAdapter.JournalViewHolder>(JournalDiffCallback) {

    class JournalViewHolder(private val binding: ListItemJournalBinding) :  RecyclerView.ViewHolder(binding.root) {
        private var formatter = SimpleDateFormat(binding.root.context.getString(R.string.date_ymd))

        init {
            val preferences =
                PreferenceManager.getDefaultSharedPreferences(binding.root.context)
            val journalDBFormat = preferences.getString(
                binding.root.context.getString(R.string.preferences_frequency_journal_entries_key),
                Journal.DATE_FORMAT
            ) ?: Journal.DATE_FORMAT
            if (journalDBFormat.length > 6 )
                formatter = SimpleDateFormat(binding.root.context.getString(R.string.date_time))
            binding.setClickListener {
                it.findNavController()
                    .navigate(
                        JournalListFragmentDirections.navigationJournalListJournal(
                            binding.journal!!
                        )
                    )
            }
        }

        fun bind(item: Journal) {
            binding.textViewDate.text = formatter.format(DateTypeConverter.convertStringToDate(item.date).time)
            if (item.imageFilename.isNotBlank())
                Glide.with(itemView.context)
                    .load(item.imageFilename)
                    .into(binding.imageViewPictureOfTheDay)

            binding.apply {
                journal = item
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalViewHolder {
        return JournalViewHolder(
            ListItemJournalBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: JournalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object JournalDiffCallback : DiffUtil.ItemCallback<Journal>() {
    override fun areItemsTheSame(oldItem: Journal, newItem: Journal): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: Journal, newItem: Journal): Boolean {
        return oldItem == newItem
    }
}
