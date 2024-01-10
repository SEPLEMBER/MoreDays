package de.wuapps.moredays.ui.home

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.wuapps.moredays.MoreDaysApplication
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.databinding.ListItemGoalHomeBinding


class GoalAdapter(
    private val onGoalClickHandler: (goal: Goal, activity: Activity) -> Unit,
    private val onDeleteActivityClickHandler: (activity: Activity) -> Unit
) :
    ListAdapter<GoalEntry, GoalAdapter.GoalViewHolder>(GoalDiffCallback) {

    class GoalViewHolder(
        private val binding: ListItemGoalHomeBinding,
        private val onGoalClickHandler: (goal: Goal, activity: Activity) -> Unit,
        private val onDeleteActivityClickHandler: (activity: Activity) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setClickListener { view ->
                showCustomDialog(binding.goalEntry!!, view.context)
            }
        }

        fun bind(item: GoalEntry) {
            binding.imageViewSymbol.setImageDrawable(
                MoreDaysApplication.iconPack?.getIcon(item.getGoal().symbol)?.drawable
            )
            binding.imageViewSymbol.setColorFilter(item.getGoal().color)
            binding.apply {
                goalEntry = item
                executePendingBindings()
            }

        }

        private fun showCustomDialog(goalEntry: GoalEntry, context: Context) {
            val dialogBuilder = AlertDialog.Builder(context)
            val layoutView: View =
                LayoutInflater.from(context).inflate(R.layout.goal_entry_custom_dialog, null)
            val itemList: MutableList<Pair<Activity, Int>> = mutableListOf()
            val activities = goalEntry.getActivities().sortedBy { activity -> activity.points }
            activities.forEach { activity ->
                itemList.add(Pair(activity, goalEntry.countActivityDoneToday(activity.uid)))
            }
            layoutView.findViewById<TextView>(R.id.textViewGoalName).text = goalEntry.getName()
            val recyclerView =
                layoutView.findViewById<RecyclerView>(R.id.recyclerviewActivityEntries)
            recyclerView.layoutManager = LinearLayoutManager(context)

            dialogBuilder.setView(layoutView)
            val goalEntryDialog = dialogBuilder.create()
            recyclerView.adapter = GoalEntryAdapter(
                itemList,
                goalEntry.getGoal(),
                onGoalClickHandler,
                onDeleteActivityClickHandler,
                goalEntryDialog
            )
            goalEntryDialog.show()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        return GoalViewHolder(
            ListItemGoalHomeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), onGoalClickHandler, onDeleteActivityClickHandler
        )
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object GoalDiffCallback : DiffUtil.ItemCallback<GoalEntry>() {
    override fun areItemsTheSame(oldItem: GoalEntry, newItem: GoalEntry): Boolean {
        return oldItem.getId() == newItem.getId()
    }

    override fun areContentsTheSame(oldItem: GoalEntry, newItem: GoalEntry): Boolean {
        return oldItem == newItem
    }
}
