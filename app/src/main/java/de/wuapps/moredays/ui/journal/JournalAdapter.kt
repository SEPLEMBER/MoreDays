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
import de.wuapps.moredays.databinding.ListItemGoalBinding
import de.wuapps.moredays.databinding.ListItemJournalBinding
import de.wuapps.moredays.ui.goal.GoalAdapter
import java.text.SimpleDateFormat


class JournalAdapter :
    ListAdapter<Journal, JournalAdapter.JournalViewHolder>(JournalDiffCallback) {

    class JournalViewHolder(private val binding: ListItemJournalBinding) :  RecyclerView.ViewHolder(binding.root) {
        private val formatter = SimpleDateFormat(binding.root.context.getString(R.string.date_ymd))

        init {
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
            if (!item.imageFilename.isNullOrBlank())
                Glide.with(itemView.context)
                    .load(item.imageFilename)
                    .into(binding.imageViewPictureOfTheDay);
            if (item.icons.size > 0){
                val layoutManager = LinearLayoutManager(
                    itemView.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                layoutManager.initialPrefetchItemCount = item.icons.size
                val iconAdapter = IconAdapter()
                iconAdapter.submitList(item.icons.map{ MoreDaysApplication.iconPack!!.getIcon(it)!! })
                binding.recyclerviewIcons.setLayoutManager(layoutManager)
                binding.recyclerviewIcons.adapter = iconAdapter
            }
            binding.apply {
                journal = item
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalViewHolder {
        return JournalAdapter.JournalViewHolder(
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
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Journal, newItem: Journal): Boolean {
        return oldItem.date == newItem.date
    }
}
