package de.wuapps.moredays.ui.home

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.wuapps.moredays.MainActivity
import de.wuapps.moredays.MoreDaysApplication
import de.wuapps.moredays.R


class GoalAdapter(private val homeViewModel: HomeViewModel) :
    ListAdapter<GoalEntry, GoalAdapter.GoalViewHolder>(GoalDiffCallback) {

    class GoalViewHolder(itemView: View, private val homeViewModel: HomeViewModel) :
        RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        private val textViewInfo: TextView = itemView.findViewById(R.id.textViewInfo)
        private val imageViewSymbol: ImageView = itemView.findViewById(R.id.imageViewSymbol)
        private val cardView: CardView = itemView.findViewById(R.id.layout)
        private var goalEntry: GoalEntry? = null

        init {
            itemView.setOnClickListener {
                showInputDialog(goalEntry!!, itemView.context)
            }
        }

        fun bind(goalEntry: GoalEntry) {
            this.goalEntry = goalEntry
            cardView.setCardBackgroundColor(goalEntry.getColor())
            textViewName.text = goalEntry.getName()
            textViewInfo.text = goalEntry.getInfo()
            imageViewSymbol.setImageDrawable(
                MoreDaysApplication.iconPack?.let{it.getIcon(goalEntry.getSymbol())}?.drawable
            )

        }

        private fun showInputDialog(goalEntry: GoalEntry, context: Context){
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
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_entry_type, parent, false)
        return GoalViewHolder(view, homeViewModel)
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
