package de.wuapps.moredays.ui.challenge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.Challenge
import de.wuapps.moredays.ui.challenge.ChallengeAdapter
import de.wuapps.moredays.ui.challenge.ChallengeListFragmentDirections

class ChallengeAdapter :
    ListAdapter<Challenge, ChallengeAdapter.ChallengeViewHolder>(ChallengeDiffCallback) {

    class ChallengeViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        private var currentChallenge: Challenge? = null

        init {
            itemView.setOnClickListener {
                val challengeId = currentChallenge!!.uid
                it.findNavController().navigate(ChallengeListFragmentDirections.navgigationChallengesChallenge(challengeId))

            }
        }

        fun bind(challenge: Challenge) {
            currentChallenge = challenge

            textViewName.text = challenge.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_challenge, parent, false)
        return ChallengeViewHolder(view)
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
        return oldItem.uid == newItem.uid
    }
}
