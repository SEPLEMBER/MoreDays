package de.wuapps.moredays.ui.home

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.wuapps.moredays.MoreDaysApplication
import de.wuapps.moredays.databinding.ListItemGoalHomeBinding


class GoalAdapter(private val homeViewModel: HomeViewModel) :
    ListAdapter<GoalEntry, GoalAdapter.GoalViewHolder>(GoalDiffCallback) {

    class GoalViewHolder(
        private val binding: ListItemGoalHomeBinding,
        private val homeViewModel: HomeViewModel
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setClickListener { view ->
                showInputDialog(binding.goalEntry!!, view.context)
            }
        }

        fun bind(item: GoalEntry) {
            binding.imageViewSymbol.setImageDrawable(
                MoreDaysApplication.iconPack?.let {
                    it.getIcon(item.getGoal().symbol)
                }?.drawable
            )
            binding.imageViewSymbol.setColorFilter(item.getGoal().color)
            binding.apply {
                goalEntry = item
                executePendingBindings()
            }

        }

        private fun showInputDialog(goalEntry: GoalEntry, context: Context) {
            val dialogBuilder = AlertDialog.Builder(context)
            val stringList: MutableList<String> = mutableListOf()
            val activities = goalEntry.getActivities().sortedBy { activity -> activity.points }
            activities.forEach { activity -> stringList.add(activity.toString()) }
            val values: Array<String> = stringList.toTypedArray()
            dialogBuilder.setTitle(goalEntry.getName()).setItems(values) { _, position ->
                val activity = activities[position]
                homeViewModel.addGoalEntry(goalEntry.getGoal(), activity)
            }
            val alertDialog = dialogBuilder.create()
            alertDialog.show()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        return GoalViewHolder(
            ListItemGoalHomeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), homeViewModel
        )
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object GoalDiffCallback : DiffUtil.ItemCallback<GoalEntry>() {
    override fun areItemsTheSame(oldItem: GoalEntry, newItem: GoalEntry): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: GoalEntry, newItem: GoalEntry): Boolean {
        return oldItem.getId() == newItem.getId()
    }
}
