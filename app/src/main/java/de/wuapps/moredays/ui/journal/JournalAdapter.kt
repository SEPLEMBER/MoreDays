package de.wuapps.moredays.ui.journal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.bumptech.glide.Glide
import de.wuapps.moredays.MoreDaysApplication
import de.wuapps.moredays.R
import de.wuapps.moredays.database.converter.DateTypeConverter
import de.wuapps.moredays.database.entity.Journal
import java.text.SimpleDateFormat


class JournalAdapter :
    ListAdapter<Journal, JournalAdapter.JournalViewHolder>(JournalDiffCallback) {
    private val viewPool = RecycledViewPool()

    class JournalViewHolder(private val itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val textViewImportant: TextView = itemView.findViewById(R.id.textViewImportant)
        private val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        private val imageViewPictureOfTheDay: ImageView = itemView.findViewById(R.id.imageViewPictureOfTheDay)
        private val recyclerViewIcons: RecyclerView = itemView.findViewById(R.id.recyclerviewIcons)
        private var currentJournal: Journal? = null
        private val formatter = SimpleDateFormat(itemView.context.getString(R.string.date_ymd))

        init {
            itemView.setOnClickListener {
                it.findNavController()
                    .navigate(
                        JournalListFragmentDirections.navigationJournalListJournal(
                            currentJournal
                        )
                    )

            }
        }

        fun bind(journal: Journal) {
            currentJournal = journal
            textViewImportant.text = journal.affirmation
            textViewDate.text = formatter.format(DateTypeConverter.convertStringToDate(journal.date).time)
            if (!journal.imageFilename.isNullOrBlank())
                Glide.with(itemView.context)
                    .load(journal.imageFilename)
                    .into(imageViewPictureOfTheDay);
            if (journal.icons.size > 0){
                val layoutManager = LinearLayoutManager(
                    itemView.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                layoutManager.initialPrefetchItemCount = journal.icons.size
                val iconAdapter = IconAdapter()
                iconAdapter.submitList(journal.icons.map{ MoreDaysApplication.iconPack!!.getIcon(it)!! })
                recyclerViewIcons.setLayoutManager(layoutManager)
                recyclerViewIcons.adapter = iconAdapter
                //recyclerViewIcons.setRecycledViewPool(viewPool)
            }
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
