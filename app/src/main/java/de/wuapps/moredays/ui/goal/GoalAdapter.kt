package de.wuapps.moredays.ui.goal

import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.ui.icons.IconManager


class GoalAdapter(private val application: Application) :
    ListAdapter<Goal, GoalAdapter.GoalViewHolder>(GoalDiffCallback) {
    class GoalViewHolder(itemView: View, private val application: Application) :
        RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        private val imageViewIcon: ImageView = itemView.findViewById(R.id.imageViewIcon)
        private val textViewPoints: TextView = itemView.findViewById(R.id.textViewPoints)
        private var currentGoal: Goal? = null

        init {
            itemView.setOnClickListener {
                it.findNavController().navigate(GoalListFragmentDirections.navigationGoalsGoal(currentGoal!!))

            }
        }

        fun bind(goal: Goal) {
            currentGoal = goal
            imageViewIcon.setImageDrawable(
                IconManager.getInstance(application).iconPack.getIcon(
                goal.symbol
            )?.drawable)
            textViewPoints.text = goal.points.toString()
            textViewName.text = goal.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_goal, parent, false)
        return GoalViewHolder(view, application)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object GoalDiffCallback : DiffUtil.ItemCallback<Goal>() {
    override fun areItemsTheSame(oldItem: Goal, newItem: Goal): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Goal, newItem: Goal): Boolean {
        return oldItem.uid == newItem.uid
    }
}
