package de.wuapps.moredays.ui.home

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.wuapps.moredays.R
import de.wuapps.moredays.database.entity.Scale


class ScaleAdapter(private val homeViewModel: HomeViewModel) :
    ListAdapter<Scale, ScaleAdapter.ScaleViewHolder>(ScaleDiffCallback) {

    class ScaleViewHolder(itemView: View, private val homeViewModel: HomeViewModel) :
        RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        private var currentScale: Scale? = null

        init {
            itemView.setOnClickListener {
                showInputDialog(itemView.context)
            }
        }

        fun bind(scale: Scale) {
            currentScale = scale
            textViewName.text = scale.name
        }

        fun showInputDialog(context: Context) {
            if (currentScale != null) {
                val layout =
                    LayoutInflater.from(context).inflate(R.layout.float_picker_layout, null)
                val decimalView = layout.findViewById<NumberPicker>(R.id.numberPickerDecimal)
                decimalView.value
                val fractionView = layout.findViewById<NumberPicker>(R.id.numberPickerFraction)
                val fractionValues = Array(100) { i -> i.toString() }
                fractionView.minValue = 0
                fractionView.maxValue = 99
                decimalView.minValue = currentScale!!.minValue
                decimalView.maxValue = currentScale!!.maxValue
                fractionView.displayedValues = fractionValues
                val dialogBuilder = AlertDialog.Builder(context)
                dialogBuilder
                    .setView(layout)
                    .setTitle(currentScale!!.name)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        homeViewModel.addScaleEntry(
                            currentScale!!.uid,
                            decimalView.value.toFloat() + fractionView.value / 100F
                        )
                    }
                    .setNegativeButton(R.string.cancel, null)
                val alertDialog = dialogBuilder.create()
                alertDialog.show()
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScaleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_entry_type, parent, false)
        return ScaleViewHolder(view, homeViewModel)
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
