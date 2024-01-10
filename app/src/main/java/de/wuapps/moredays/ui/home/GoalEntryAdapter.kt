package de.wuapps.moredays.ui.home

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.databinding.ListItemActivityEntrySimpleBinding

class GoalEntryAdapter (private val itemList: List<Pair<Activity, Int>>, private val goal: Goal, private val onGoalClickHandler: (goal: Goal, activity: Activity) -> Unit, private val onDeleteActivityClickHandler: (activity: Activity) -> Unit, private val dialog: Dialog) : RecyclerView.Adapter<GoalEntryAdapter.ViewHolder>() {

    class ViewHolder(
        val binding: ListItemActivityEntrySimpleBinding,
        private val goal: Goal,
        private val onGoalClickHandler: (goal: Goal, activity: Activity) -> Unit,
        private val onDeleteActivityClickHandler: (activity: Activity) -> Unit,
        private val dialog: Dialog
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(activity: Activity, count: Int) {
            binding.activity = activity
            binding.textViewActivityName.setOnClickListener {
                onGoalClickHandler(goal, activity)
                dialog.dismiss()
            }
            if (count > 0) {
                binding.imageViewDelete.setOnClickListener {
                    onDeleteActivityClickHandler(activity)
                    dialog.dismiss()
                }
            }
            else {
                binding.imageViewDelete.visibility = View.INVISIBLE
            }
            val tick = "\u2713"
            val text = "$activity ${tick.repeat(count)}"
            binding.textViewActivityName.text = text
            binding.executePendingBindings()
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemActivityEntrySimpleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            goal,
            onGoalClickHandler,
            onDeleteActivityClickHandler,
            dialog
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position].first, itemList[position].second)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}