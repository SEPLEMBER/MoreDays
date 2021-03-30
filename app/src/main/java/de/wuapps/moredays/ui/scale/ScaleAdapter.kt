package de.wuapps.moredays.ui.scale

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.wuapps.moredays.database.entity.Scale
import de.wuapps.moredays.databinding.ListItemScaleBinding

class ScaleAdapter :
    ListAdapter<Scale, ScaleAdapter.ScaleViewHolder>(ScaleDiffCallback) {

    class ScaleViewHolder( private val binding: ListItemScaleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.setClickListener {
                it.findNavController()
                    .navigate(ScaleListFragmentDirections.navigationScalesScale(binding.scale))

            }
        }

        fun bind(item: Scale) {
            binding.apply {
                scale = item
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScaleViewHolder {
        return ScaleAdapter.ScaleViewHolder(
            ListItemScaleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ScaleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object ScaleDiffCallback : DiffUtil.ItemCallback<Scale>() {
    override fun areItemsTheSame(oldItem: Scale, newItem: Scale): Boolean {
        return oldItem.uid == newItem.uid
    }

    override fun areContentsTheSame(oldItem: Scale, newItem: Scale): Boolean {
        return oldItem == newItem
    }
}
