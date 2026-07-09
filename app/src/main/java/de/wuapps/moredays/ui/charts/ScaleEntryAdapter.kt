package de.wuapps.moredays.ui.charts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.ScaleEntry
import de.wuapps.moredays.databinding.ListItemScaleEntryBinding
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

class ScaleEntryAdapter: ListAdapter<ScaleEntry, ScaleEntryAdapter.ScaleEntryViewHolder>(ScaleEntryDiffCallback) {
    var showIntegerValues: Boolean = false

    class ScaleEntryViewHolder(private val binding: ListItemScaleEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val dateFormatter = SimpleDateFormat(itemView.context.getString(R.string.date_time))

        fun bind(item: ScaleEntry, showIntegerValues: Boolean) {
            binding.textViewDate.text = dateFormatter.format(item.timestamp.time)
            binding.textViewValue.text = if (showIntegerValues) {
                item.value.roundToInt().toString()
            } else {
                item.value.toString()
            }
            binding.apply {
                scaleEntry = item
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScaleEntryViewHolder {
        return ScaleEntryViewHolder(
            ListItemScaleEntryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ScaleEntryViewHolder, position: Int) {
        val scaleEntry = getItem(position)
        if (scaleEntry != null)
            holder.bind(scaleEntry, showIntegerValues)
    }

    fun getScaleEntryId(position: Int): Long {
        return getItem(position).uid
    }
}
object ScaleEntryDiffCallback : DiffUtil.ItemCallback<ScaleEntry>() {
    override fun areItemsTheSame(oldItem: ScaleEntry, newItem: ScaleEntry): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ScaleEntry, newItem: ScaleEntry): Boolean {
        return oldItem.uid == newItem.uid
    }
}
