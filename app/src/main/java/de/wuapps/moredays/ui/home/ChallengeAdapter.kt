package de.wuapps.moredays.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.wuapps.moredays.MoreDaysApplication
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.Challenge
import de.wuapps.moredays.databinding.ListItemChallengeHomeBinding
import java.text.SimpleDateFormat


class ChallengeAdapter :
    ListAdapter<Challenge, ChallengeAdapter.ChallengeViewHolder>(ChallengeDiffCallback) {

    class ChallengeViewHolder(
        private val binding: ListItemChallengeHomeBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private val dateFormatter = SimpleDateFormat(itemView.context.getString(R.string.date_ymd))
        fun bind(item: Challenge) {
            binding.imageViewIcon.setImageDrawable(
                MoreDaysApplication.iconPack?.let {
                    it.getIcon(item.symbol)
                }?.drawable
            )
            binding.textViewDeadline.text = dateFormatter.format(item.deadline.time)
            binding.apply {
                challenge = item
                executePendingBindings()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        return ChallengeViewHolder(
            ListItemChallengeHomeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object ChallengeDiffCallback : DiffUtil.ItemCallback<Challenge>() {
    override fun areItemsTheSame(oldItem: Challenge, newItem: Challenge): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Challenge, newItem: Challenge): Boolean {
        return oldItem.uid == newItem.uid && oldItem.currentValue == newItem.currentValue && oldItem.status == newItem.status
    }
}
