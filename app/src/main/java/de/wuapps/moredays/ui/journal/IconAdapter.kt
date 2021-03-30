package de.wuapps.moredays.ui.journal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.maltaisn.icondialog.data.Icon
import de.wuapps.moredays.R

class IconAdapter :
    ListAdapter<Icon, IconAdapter.IconViewHolder>(IconDiffCallback) {

    class IconViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val imageViewIcon: ImageView = itemView.findViewById(R.id.imageViewIcon)
        private var currentIcon: Icon? = null

        fun bind(icon: Icon) {
            currentIcon = icon
            imageViewIcon.setImageDrawable(icon.drawable!!.mutate())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_icon, parent, false)
        return IconViewHolder(view)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun removeItem(position: Int){
        this.currentList.removeAt(position)
        notifyDataSetChanged()
    }
}

object IconDiffCallback : DiffUtil.ItemCallback<Icon>() {
    override fun areItemsTheSame(oldItem: Icon, newItem: Icon): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Icon, newItem: Icon): Boolean {
        return oldItem == newItem
    }
}

