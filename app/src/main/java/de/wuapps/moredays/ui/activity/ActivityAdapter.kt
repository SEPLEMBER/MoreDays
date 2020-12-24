package de.wuapps.moredays.ui.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.ui.activity.ActivityAdapter
import de.wuapps.moredays.ui.activity.ActivityListFragmentDirections

class ActivityAdapter :
    ListAdapter<Activity, ActivityAdapter.ActivityViewHolder>(ActivityDiffCallback) {

    class ActivityViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        private var currentActivity: Activity? = null

        init {
            itemView.setOnClickListener {
                val activityId = currentActivity!!.uid
                //it.findNavController().navigate(ActivityListFragmentDirections.navigationActivitysActivity(activityId))

            }
        }

        fun bind(activity: Activity) {
            currentActivity = activity

            textViewName.text = activity.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_activity, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object ActivityDiffCallback : DiffUtil.ItemCallback<Activity>() {
    override fun areItemsTheSame(oldItem: Activity, newItem: Activity): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Activity, newItem: Activity): Boolean {
        return oldItem.uid == newItem.uid
    }
}
