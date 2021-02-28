package de.wuapps.moredays.ui.tools.trophy

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
import de.wuapps.moredays.database.entity.TrophyAndRelatedGoal
import de.wuapps.moredays.database.entity.Trophy
import de.wuapps.moredays.databinding.ListItemGoalBinding
import de.wuapps.moredays.databinding.ListItemTrophyBinding
import de.wuapps.moredays.ui.goal.GoalAdapter
import java.text.SimpleDateFormat


class TrophyAdapter :
    ListAdapter<TrophyAndRelatedGoal, TrophyAdapter.TrophyViewHolder>(TrophyDiffCallback) {

    class TrophyViewHolder(private val binding: ListItemTrophyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val dateFormatter = SimpleDateFormat(itemView.context.getString(R.string.date_ymd))

        fun bind(item: TrophyAndRelatedGoal) {
            binding.imageViewGoal.setImageDrawable(
                MoreDaysApplication.iconPack?.let { it.getIcon(item.goal.symbol) }?.drawable
            )
            binding.imageViewGoal.setColorFilter(item.goal.color)
            binding.imageViewTrophy.setImageDrawable(
                MoreDaysApplication.iconPack?.let { it.getIcon(matchTrophyTypeToSymbol(item.trophy.type)) }?.drawable
            )
            binding.textViewDate.text = dateFormatter.format(item.trophy.timestamp.time)
            binding.textViewTitle.text = matchTrophyTypeToText(item.trophy.type, item.trophy.value)
            binding.apply {
                trophyData = item
                executePendingBindings()
            }
        }

        private fun matchTrophyTypeToSymbol(trophyType: Int): Int {
            when (trophyType) {
                Trophy.TROPHY_TYPE_GOAL_DAYS_IN_ROW -> return 62 // trophy
                Trophy.TROPHY_TYPE_GOAL_TOTAL -> return 49 // crown
                Trophy.TROPHY_TYPE_MADE_GOAL -> return 59 // award
                Trophy.TROPHY_TYPE_MADE_GOAL_N_TIMES_IN_WEEK -> return 60 // medal
            }
            return 42
        }
        private fun matchTrophyTypeToText(trophyType: Int, value: Int): String {
            when (trophyType) {
                Trophy.TROPHY_TYPE_GOAL_DAYS_IN_ROW -> return String.format("%s: %d", itemView.context.getString(R.string.trophy_type_title_row), value)
                Trophy.TROPHY_TYPE_GOAL_TOTAL -> return  String.format("%s: %d", itemView.context.getString(R.string.trophy_type_title_total), value)
                Trophy.TROPHY_TYPE_MADE_GOAL -> return  itemView.context.getString(R.string.trophy_type_title_made_goal)
                Trophy.TROPHY_TYPE_MADE_GOAL_N_TIMES_IN_WEEK -> return  String.format("%s: %d", itemView.context.getString(R.string.trophy_type_title_times_per_week), value)
            }
            return ""
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrophyViewHolder {
        return TrophyAdapter.TrophyViewHolder(
            ListItemTrophyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TrophyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object TrophyDiffCallback : DiffUtil.ItemCallback<TrophyAndRelatedGoal>() {
    override fun areItemsTheSame(oldItem: TrophyAndRelatedGoal, newItem: TrophyAndRelatedGoal): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: TrophyAndRelatedGoal, newItem: TrophyAndRelatedGoal): Boolean {
        return oldItem.trophy.uid == newItem.trophy.uid
    }
}
