package de.wuapps.moredays.ui.home

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
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
            init {
                binding.setClickListener { view ->
                    run {
                        val toast = Toast.makeText(view.context, binding.challenge!!.toString(dateFormatter), Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.TOP, 0, 450)
                        toast.show()
                    }
                }
            }
       fun bind(item: Challenge) {
            binding.imageViewIcon.setImageDrawable(
                MoreDaysApplication.iconPack?.let {
                    it.getIcon(item.symbol)
                }?.drawable
            )
           if (item.status == Challenge.CHALLENGE_SUCCEEDED)
               binding.layout.setBackgroundColor(getColor(itemView.context, R.color.challenge_status_succeeded))
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
        return oldItem.uid == newItem.uid
    }

    override fun areContentsTheSame(oldItem: Challenge, newItem: Challenge): Boolean {
        return oldItem == newItem
    }
}
