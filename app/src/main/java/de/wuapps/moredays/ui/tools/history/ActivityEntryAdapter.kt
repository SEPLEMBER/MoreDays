package de.wuapps.moredays.ui.tools.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.wuapps.moredays.MoreDaysApplication
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.ActivityEntryAndRelatedData
import de.wuapps.moredays.databinding.ListItemActivityEntryBinding
import de.wuapps.moredays.databinding.ListItemGoalBinding
import de.wuapps.moredays.ui.goal.GoalAdapter
import java.text.SimpleDateFormat


class ActivityEntryAdapter :
    ListAdapter<ActivityEntryAndRelatedData, ActivityEntryAdapter.ActivityEntryViewHolder>(ActivityEntryDiffCallback) {
    class ActivityEntryViewHolder(private val binding: ListItemActivityEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val dateFormatter = SimpleDateFormat(itemView.context.getString(R.string.date_ymd))

        fun bind(item: ActivityEntryAndRelatedData) {
            binding.imageViewGoal.setImageDrawable(
                MoreDaysApplication.iconPack?.let { it.getIcon(item.goal.symbol) }?.drawable
            )
            binding.textViewDate.text = dateFormatter.format(item.activityEntry.timestamp.time)
            binding.apply {
                activityEntry = item
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityEntryViewHolder {
        return ActivityEntryAdapter.ActivityEntryViewHolder(
            ListItemActivityEntryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ActivityEntryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getActivityEntryId(position: Int): Long {
        return getItem(position).activityEntry.uid
    }
}

object ActivityEntryDiffCallback : DiffUtil.ItemCallback<ActivityEntryAndRelatedData>() {
    override fun areItemsTheSame(oldItem: ActivityEntryAndRelatedData, newItem: ActivityEntryAndRelatedData): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ActivityEntryAndRelatedData, newItem: ActivityEntryAndRelatedData): Boolean {
        return oldItem.activityEntry.uid == newItem.activityEntry.uid
    }
}
