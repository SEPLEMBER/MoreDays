package de.wuapps.moredays.ui.challenge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.wuapps.moredays.MoreDaysApplication
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.Challenge
import de.wuapps.moredays.database.entity.Challenge.Companion.CHALLENGE_FAILED
import de.wuapps.moredays.database.entity.Challenge.Companion.CHALLENGE_NOT_STARTED
import de.wuapps.moredays.database.entity.Challenge.Companion.CHALLENGE_RUNNING
import de.wuapps.moredays.database.entity.Challenge.Companion.CHALLENGE_STATUS_UNDEFINED
import de.wuapps.moredays.database.entity.Challenge.Companion.CHALLENGE_SUCCEEDED
import de.wuapps.moredays.databinding.ListItemChallengeBinding
import java.text.SimpleDateFormat

class ChallengeAdapter :
    ListAdapter<Challenge, ChallengeAdapter.ChallengeViewHolder>(ChallengeDiffCallback) {

    class ChallengeViewHolder(private val binding: ListItemChallengeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val formatter = SimpleDateFormat(binding.root.context.getString(R.string.date_ymd))
        init {
            binding.setClickListener{ view ->
                view.findNavController().navigate(
                    ChallengeListFragmentDirections.navgigationChallengesChallenge(binding.challenge)
                )

            }
        }

        fun bind(item: Challenge) {
            if (item.status == CHALLENGE_SUCCEEDED)
                binding.imageViewTrophy.visibility = View.VISIBLE
            else
                binding.imageViewTrophy.visibility = View.GONE
            binding.textViewDueDate.text = formatter.format(item.deadline.time)
            binding.imageViewIcon.setImageDrawable(
                MoreDaysApplication.iconPack?.let {it.getIcon(item.symbol)}?.drawable
            )
            binding.layout.setBackgroundColor(getColorToStatus(item.status))
            binding.apply {
                challenge = item
                executePendingBindings()
            }
        }
        fun getColorToStatus(state: Int): Int {
            when(state){
                CHALLENGE_STATUS_UNDEFINED -> return getColor(itemView.context, R.color.challenge_status_undefined)
                CHALLENGE_NOT_STARTED -> return getColor(itemView.context, R.color.challenge_status_not_started)
                CHALLENGE_RUNNING -> return getColor(itemView.context, R.color.challenge_status_running)
                CHALLENGE_FAILED -> return getColor(itemView.context, R.color.challenge_status_failed)
                CHALLENGE_SUCCEEDED -> return getColor(itemView.context, R.color.challenge_status_succeeded)
            }
            return getColor(itemView.context, R.color.challenge_status_undefined)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        return ChallengeViewHolder(
            ListItemChallengeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        val challenge = getItem(position)
        if (challenge != null)
            holder.bind(challenge)
    }

}

object ChallengeDiffCallback : DiffUtil.ItemCallback<Challenge>() {
    override fun areItemsTheSame(oldItem: Challenge, newItem: Challenge): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Challenge, newItem: Challenge): Boolean {
        return oldItem.uid == newItem.uid
    }
}
