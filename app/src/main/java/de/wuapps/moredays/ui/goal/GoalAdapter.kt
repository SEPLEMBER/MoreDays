package de.wuapps.moredays.ui.goal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.wuapps.moredays.MoreDaysApplication
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.databinding.ListItemGoalBinding


class GoalAdapter() :
    ListAdapter<Goal, GoalAdapter.GoalViewHolder>(GoalDiffCallback) {
    class GoalViewHolder( private val binding: ListItemGoalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.setClickListener{ view ->
                view.findNavController().navigate(GoalListFragmentDirections.navigationGoalsGoal(binding.goal))
            }
        }

        fun bind(item: Goal) {
            binding.imageViewIcon.setImageDrawable(
                MoreDaysApplication.iconPack?.let {it.getIcon(item.symbol)}?.drawable
            )
            binding.imageViewIcon.setColorFilter(item.color)
            binding.apply {
                goal = item
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        return GoalViewHolder(ListItemGoalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = getItem(position)
        if (goal != null)
            holder.bind(goal)
    }
}

object GoalDiffCallback : DiffUtil.ItemCallback<Goal>() {
    override fun areItemsTheSame(oldItem: Goal, newItem: Goal): Boolean {
        return oldItem.uid == newItem.uid
    }

    override fun areContentsTheSame(oldItem: Goal, newItem: Goal): Boolean {
        return oldItem == newItem
    }
}
