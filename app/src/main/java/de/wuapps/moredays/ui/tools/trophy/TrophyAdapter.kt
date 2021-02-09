package de.wuapps.moredays.ui.tools.trophy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.wuapps.moredays.MainActivity
import de.wuapps.moredays.R
import de.wuapps.moredays.data.entity.TrophyAndRelatedGoal
import de.wuapps.moredays.database.entity.Trophy
import java.text.SimpleDateFormat


class TrophyAdapter :
    ListAdapter<TrophyAndRelatedGoal, TrophyAdapter.TrophyViewHolder>(TrophyDiffCallback) {

    class TrophyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        private val imageViewTrophy: ImageView = itemView.findViewById((R.id.imageViewTrophy))
        private val imageViewGoal: ImageView = itemView.findViewById((R.id.imageViewGoal))
        private val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        private val textViewText: TextView = itemView.findViewById(R.id.textViewText)
        private var currentTrophy: TrophyAndRelatedGoal? = null
        private val dateFormatter = SimpleDateFormat(itemView.context.getString(R.string.date_ymd))
        fun bind(trophy: TrophyAndRelatedGoal) {
            currentTrophy = trophy
            imageViewGoal.setImageDrawable(
                MainActivity.iconPack?.let { it.getIcon(trophy.goal.symbol) }?.drawable
            )
            imageViewTrophy.setImageDrawable(
                MainActivity.iconPack?.let { it.getIcon(matchTrophyTypeToSymbol(currentTrophy!!.trophy.type)) }?.drawable
            )
            textViewDate.text = dateFormatter.format(currentTrophy!!.trophy.timestamp.time)
            textViewTitle.text = matchTrophyTypeToText(currentTrophy!!.trophy.type)
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
        private fun matchTrophyTypeToText(trophyType: Int): String {
            when (trophyType) {
                Trophy.TROPHY_TYPE_GOAL_DAYS_IN_ROW -> return itemView.context.getString(R.string.trophy_type_title_row)
                Trophy.TROPHY_TYPE_GOAL_TOTAL -> return itemView.context.getString(R.string.trophy_type_title_total)
                Trophy.TROPHY_TYPE_MADE_GOAL -> return itemView.context.getString(R.string.trophy_type_title_made_goal)
                Trophy.TROPHY_TYPE_MADE_GOAL_N_TIMES_IN_WEEK -> return itemView.context.getString(R.string.trophy_type_title_times_per_week)
            }
            return ""
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrophyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_trophy, parent, false)
        return TrophyViewHolder(view)
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
