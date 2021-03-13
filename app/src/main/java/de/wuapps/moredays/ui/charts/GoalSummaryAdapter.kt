package de.wuapps.moredays.ui.charts

import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.wuapps.moredays.MoreDaysApplication
import de.wuapps.moredays.R
import de.wuapps.moredays.databinding.ListItemGoalSummaryBinding

class GoalSummaryAdapter :
    ListAdapter<GoalsChartsViewModel.GoalSummary, GoalSummaryAdapter.GoalSummaryViewHolder>(GoalSummaryDiffCallback) {
    class GoalSummaryViewHolder(private val binding: ListItemGoalSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GoalsChartsViewModel.GoalSummary) {
            binding.imageViewIcon.setImageDrawable(
                MoreDaysApplication.iconPack?.let { it.getIcon(item.goal.symbol) }?.drawable
            )
            binding.imageViewIcon.setColorFilter(item.goal.color)
            binding.lblMade.setText(HtmlCompat.fromHtml(String.format("&#127941; %d %s", item.countDaysMade, "made"), HtmlCompat.FROM_HTML_MODE_LEGACY))
            binding.lblPoints.setText(HtmlCompat.fromHtml(String.format("&#127941; %d %s", item.sumPoints, "points"), HtmlCompat.FROM_HTML_MODE_LEGACY))
            binding.lblInfo.setText(HtmlCompat.fromHtml(String.format("&#127941; %d %s", item.countDaysInRow, "chain"), HtmlCompat.FROM_HTML_MODE_LEGACY))

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalSummaryViewHolder {
        return GoalSummaryViewHolder(
            ListItemGoalSummaryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: GoalSummaryViewHolder, position: Int) {
        val goalSummary = getItem(position)
        if (goalSummary != null)
            holder.bind(goalSummary)
    }

}
object GoalSummaryDiffCallback : DiffUtil.ItemCallback<GoalsChartsViewModel.GoalSummary>() {
    override fun areItemsTheSame(oldItem: GoalsChartsViewModel.GoalSummary, newItem: GoalsChartsViewModel.GoalSummary): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: GoalsChartsViewModel.GoalSummary, newItem: GoalsChartsViewModel.GoalSummary): Boolean {
        return oldItem.goal.uid == newItem.goal.uid
    }
}
