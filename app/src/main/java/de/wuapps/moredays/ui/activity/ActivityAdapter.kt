package de.wuapps.moredays.ui.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.ui.goal.GoalFragmentDirections


class ActivityAdapter(_goal: Goal) :
    ListAdapter<Activity, ActivityAdapter.ActivityViewHolder>(ActivityDiffCallback) {
    val goal = _goal

    class ActivityViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val chipEditActivity: Chip = itemView.findViewById(R.id.chipEditActivity)
        private var currentActivity: Activity? = null
        private var currentGoal: Goal? = null


        init {
            chipEditActivity.setOnClickListener {
                it.findNavController().navigate(
                    GoalFragmentDirections.navigationGoalActivity(
                        currentActivity,
                        currentGoal!!
                    )
                )

            }
        }

        fun bind(activity: Activity, goal: Goal) {
            currentActivity = activity
            currentGoal = goal
            chipEditActivity.text = activity.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_activity, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        holder.bind(getItem(position), goal)
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
