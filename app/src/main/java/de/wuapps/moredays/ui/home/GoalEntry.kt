package de.wuapps.moredays.ui.home

import android.app.AlertDialog
import android.content.Context
import de.wuapps.moredays.data.entity.GoalAndRelatedData
import de.wuapps.moredays.database.dao.ActivityEntryDao
import de.wuapps.moredays.database.entity.ActivityEntry
import de.wuapps.moredays.database.entity.Goal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.Calendar.DAY_OF_YEAR
import java.util.Calendar.YEAR

class GoalEntry(
    private val goalData: GoalAndRelatedData,
    private val date: Calendar,
    private val entryDao: ActivityEntryDao
) : EntryType {
    private var currentValue = -1
    private val goal: Goal = goalData.goal!!

    override fun getCurrentValue(): Int {
        if (currentValue < 0) {
            val year = date.get(YEAR)
            val day = date.get(DAY_OF_YEAR)
            currentValue = goalData.entries.filter { entry ->
                entry.timestamp.get(YEAR) == year && entry.timestamp.get(DAY_OF_YEAR) == day
            }.sumBy { entry -> entry.points }
        }
        return currentValue
    }

    override fun getDesiredValue(): Int {
        return goal.points
    }

    override fun getName(): String {
        return goal.name
    }

    override fun getInfo(): String {
        return "${getCurrentValue()} / ${goal.points}"
    }

    override fun getSymbol(): Int {
        return goal.symbol
    }

    override fun getId(): String {
        return "goal-${goal.uid}"
    }

    override fun showInputDialog(context: Context) {
        val dialogBuilder = AlertDialog.Builder(context)
        val stringList: MutableList<String> = mutableListOf()
        goalData.activities.forEach { activity -> stringList.add(activity.toString()) }
        val values: Array<String> = stringList.toTypedArray()
        dialogBuilder.setTitle(goal.name).setItems(values) { _, position ->
            val activity = goalData.activities[position]
            GlobalScope.launch {
                entryDao.insert(
                    ActivityEntry(activity)
                )
            }
        }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    override fun getColor(): Int {
        return goal.color
    }

    override fun equals(other: Any?): Boolean {
        if (other == null)
            return false
        if (other !is GoalEntry)
            return false
        return other.goal == goal && other.currentValue == currentValue
    }

    override fun hashCode(): Int {
        var result = date.hashCode()
        result = 31 * result + currentValue
        result = 31 * result + goal.hashCode()
        return result
    }


}