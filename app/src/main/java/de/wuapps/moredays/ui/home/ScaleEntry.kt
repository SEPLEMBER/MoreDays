package de.wuapps.moredays.ui.home

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.widget.NumberPicker
import de.wuapps.moredays.R
import de.wuapps.moredays.database.dao.ScaleEntryDao
import de.wuapps.moredays.database.entity.Scale
import de.wuapps.moredays.database.entity.ScaleEntry
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ScaleEntry(
    val scale: Scale,
    val currentValue: Float,
    val entryDao: ScaleEntryDao
):EntryType {
    val SYMBOL = 771
    override fun getName(): String {
        return scale.name
    }

    override fun getInfo(): String {
        return currentValue.toString()
    }

    override fun getSymbol(): Int {
        return SYMBOL
    }

    override fun getId(): String {
        return "scale-${scale.uid}"
    }

    override fun offerInput(context: Context) {
        val layout = LayoutInflater.from(context).inflate(R.layout.float_picker_layout, null)
        val decimalView = layout.findViewById<NumberPicker>(R.id.numberPickerDecimal)
        decimalView.value
        val fractionView = layout.findViewById<NumberPicker>(R.id.numberPickerFraction)
        var fractionValues= Array<String>(100) {i -> i.toString()}
        fractionView.setDisplayedValues(fractionValues)
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder
            .setView(layout)
            .setTitle(scale.name)
            .setPositiveButton(R.string.ok) { _, _ ->
                GlobalScope.launch {
                    entryDao.insert(
                        ScaleEntry(
                            scale.uid, getFloat(
                                decimalView.value,
                                fractionView.value
                            )
                        )
                    )
                }
            }
            .setNegativeButton(R.string.cancel, null)
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    fun getFloat(decimal: Int, fraction: Int): Float
    {
        return decimal.toFloat() + fraction/100F
    }
}