package de.wuapps.moredays.ui.tools.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.wuapps.moredays.MainActivity
import de.wuapps.moredays.MoreDaysApplication
import de.wuapps.moredays.R
import de.wuapps.moredays.data.entity.ActivityEntryAndRelatedData
import java.text.SimpleDateFormat


class ActivityEntryAdapter :
    ListAdapter<ActivityEntryAndRelatedData, ActivityEntryAdapter.ActivityEntryViewHolder>(ActivityEntryDiffCallback) {
    class ActivityEntryViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        private val imageViewGoal: ImageView = itemView.findViewById((R.id.imageViewGoal))
        private val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        private val textViewText: TextView = itemView.findViewById(R.id.textViewText)
        private var currentActivityEntry: ActivityEntryAndRelatedData? = null
        private val dateFormatter = SimpleDateFormat(itemView.context.getString(R.string.date_ymd))

        fun bind(activityEntry: ActivityEntryAndRelatedData) {
            currentActivityEntry = activityEntry
            imageViewGoal.setImageDrawable(
                MoreDaysApplication.iconPack?.let { it.getIcon(currentActivityEntry!!.goal.symbol) }?.drawable
            )
            textViewDate.text = dateFormatter.format(currentActivityEntry!!.activityEntry.timestamp.time)
            textViewTitle.text = currentActivityEntry!!.activity.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityEntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_activity_entry, parent, false)
        return ActivityEntryViewHolder(view)
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
