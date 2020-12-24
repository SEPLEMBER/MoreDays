package de.wuapps.moredays.ui.scale

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.database.entity.Scale

class ScaleAdapter :
    ListAdapter<Scale, ScaleAdapter.ScaleViewHolder>(ScaleDiffCallback) {

    class ScaleViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        private var currentScale: Scale? = null

        init {
            itemView.setOnClickListener {
                val scaleId = currentScale!!.uid
                it.findNavController().navigate(ScaleListFragmentDirections.navigationScalesScale(scaleId))

            }
        }

        fun bind(scale: Scale) {
            currentScale = scale

            textViewName.text = scale.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScaleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_scale, parent, false)
        return ScaleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScaleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object ScaleDiffCallback : DiffUtil.ItemCallback<Scale>() {
    override fun areItemsTheSame(oldItem: Scale, newItem: Scale): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Scale, newItem: Scale): Boolean {
        return oldItem.uid == newItem.uid
    }
}
