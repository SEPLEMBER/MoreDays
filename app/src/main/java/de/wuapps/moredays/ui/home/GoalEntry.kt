package de.wuapps.moredays.ui.home

import de.wuapps.moredays.data.entity.GoalAndRelatedData
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.Goal
import java.util.*
import java.util.Calendar.DAY_OF_YEAR
import java.util.Calendar.YEAR


class GoalEntry(
    private val goalData: GoalAndRelatedData,
    private val date: Calendar
)  {
    private var currentValue = -1
    private val goal: Goal = goalData.goal!!

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
        return other.goal == goal && other.currentValue == currentValue
    }

    override fun hashCode(): Int {
        var result = date.hashCode()
        result = 31 * result + currentValue
        result = 31 * result + goal.hashCode()
        return result
    }
}