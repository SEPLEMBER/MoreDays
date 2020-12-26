package de.wuapps.moredays.ui.trophy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.Trophy


class TrophyAdapter() :
    ListAdapter<Trophy, TrophyAdapter.TrophyViewHolder>(TrophyDiffCallback) {

    class TrophyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        private var currentTrophy: Trophy? = null

        fun bind(trophy: Trophy) {
            currentTrophy = trophy
            textViewName.text = trophy.text
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

object TrophyDiffCallback : DiffUtil.ItemCallback<Trophy>() {
    override fun areItemsTheSame(oldItem: Trophy, newItem: Trophy): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Trophy, newItem: Trophy): Boolean {
        return oldItem.uid == newItem.uid
    }
}
