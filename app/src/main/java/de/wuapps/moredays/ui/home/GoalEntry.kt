package de.wuapps.moredays.ui.home

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import de.wuapps.moredays.database.dao.ActivityEntryDao
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.ActivityEntry
import de.wuapps.moredays.database.entity.Goal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GoalEntry(val goal: Goal, allActivities: List<Activity>, val currentValue: Int, val entryDao: ActivityEntryDao):EntryType {
    private val activities:List<Activity> = allActivities.filter { activity -> activity.goalId == goal.uid }

    override fun getName(): String {
        return goal.name
    }

    override fun getInfo(): String {
        return "$currentValue / ${goal.points}"
    }

    override fun getSymbol(): Int {
        return goal.symbol
    }

    override fun getId(): String {
        return "goal-${goal.uid}"
    }

    override fun offerInput(context: Context) {
        val dialogBuilder = AlertDialog.Builder(context)
        val stringList: MutableList<String> = mutableListOf()
        activities.forEach{activity -> stringList.add(activity.toString())}
        val values : Array<String> = stringList.toTypedArray()
        dialogBuilder.setTitle(goal.name).setItems(values) { _, position -> GlobalScope.launch{entryDao.insert(
            ActivityEntry(activities.get(position).uid))}
        }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }


}