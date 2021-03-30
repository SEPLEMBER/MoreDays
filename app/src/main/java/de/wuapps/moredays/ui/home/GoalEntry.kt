package de.wuapps.moredays.ui.home

import android.util.Log
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.database.entity.GoalAndRelatedData
import java.util.*
import java.util.Calendar.DAY_OF_YEAR
import java.util.Calendar.YEAR


class GoalEntry(private val goalData: GoalAndRelatedData) {
    var date = Calendar.getInstance()
        get() = field
        private set(value) {
            field = value
            currentValue = -1
        }
    private var currentValue = -1
    private val goal: Goal = goalData.goal!!
    constructor(goalData: GoalAndRelatedData, date: Calendar) : this(goalData){
        this.date = date
    }
    fun getCopy(copyDate:Calendar):GoalEntry {
        val goalEntry = GoalEntry(goalData, copyDate)
        return goalEntry
    }
    fun getCurrentValue(): Int {
        if (currentValue < 0) {
            val year = date.get(YEAR)
            val day = date.get(DAY_OF_YEAR)
            currentValue = goalData.entries.filter { entry ->
                entry.timestamp.get(YEAR) == year && entry.timestamp.get(DAY_OF_YEAR) == day
            }.sumBy { entry -> entry.points }
        }
        return currentValue
    }

    fun getGoal(): Goal {
        return goal
    }
    fun getDesiredValue(): Int {
        return goal.points
    }

    fun getName(): String {
        return goal.name
    }

    fun getInfo(): String {
        return "${getCurrentValue()} / ${goal.points}"
    }

    fun getSymbol(): Int {
        return goal.symbol
    }

    fun getActivities(): List<Activity>{
        return goalData.activities
    }

    fun getId(): String {
        return "goal-${goal.uid}"
    }

    fun getColor(): Int {
        return goal.color
    }

    override fun equals(other: Any?): Boolean {
        if (other == null)
            return false
        if (other !is GoalEntry)
            return false
        val result = other.goal == goal && other.getCurrentValue() == getCurrentValue()
        val x = other.date.get(DAY_OF_YEAR) == date.get(DAY_OF_YEAR)
        val y = other.date == date
        Log.w("trapp", result.toString() + ": " + other.goal.name + ":dates:" + x.toString() + y.toString())
        return result && x && y //other.goal == goal && other.getCurrentValue() == getCurrentValue()
    }

    override fun hashCode(): Int {
        var result = date.hashCode()
        result = 31 * result + getCurrentValue()
        result = 31 * result + goal.hashCode()
        return result
    }
}