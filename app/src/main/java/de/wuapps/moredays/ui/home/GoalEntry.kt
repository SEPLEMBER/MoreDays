package de.wuapps.moredays.ui.home

import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.ActivityEntry
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.database.entity.GoalAndRelatedData
import java.util.Calendar
import java.util.Calendar.DAY_OF_YEAR
import java.util.Calendar.YEAR


class GoalEntry(private val goalData: GoalAndRelatedData) {
    var date: Calendar = Calendar.getInstance()
        private set(value) {
            field = value
            year = date.get(YEAR)
            day = date.get(DAY_OF_YEAR)
            activityEntriesToday.clear()
            currentValue = -1
        }
    private var year = date.get(YEAR)
    private var day = date.get(DAY_OF_YEAR)
    private var currentValue = -1
    private var activityEntriesToday = mutableListOf<ActivityEntry>()
    private val goal: Goal = goalData.goal!!

    constructor(goalData: GoalAndRelatedData, date: Calendar) : this(goalData) {
        this.date = date
    }

    fun getCopy(copyDate: Calendar): GoalEntry {
        return GoalEntry(goalData, copyDate)
    }

    fun getCurrentValue(): Int {
        if (currentValue == -1) {
            activityEntriesToday.clear()
            activityEntriesToday.addAll(goalData.entries.filter { entry ->
                entry.timestamp.get(YEAR) == year && entry.timestamp.get(DAY_OF_YEAR) == day
            })
            currentValue = activityEntriesToday.sumOf { it.points }
        }
        return currentValue
    }

    fun countActivityDoneToday(activityId: Long): Int {
        // Ensure activityEntriesToday is populated
        getCurrentValue()
        return activityEntriesToday.filter { entry -> entry.activityId == activityId }.size
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
        return if (goal.points == 0) "${getCurrentValue()}"
        else "${getCurrentValue()} / ${goal.points}"
    }

    fun getSymbol(): Int {
        return goal.symbol
    }

    fun getActivities(): List<Activity> {
        return goalData.activities.filter { !it.isOld }
    }

    fun getId(): String {
        return "goal-${goal.uid}"
    }

    fun getColor(): Int {
        return goal.color
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GoalEntry) return false

        if (goal != other.goal) return false
        if (year != other.year) return false
        if (day != other.day) return false

        // Important: Compare both the sum and the count of activities
        // to detect changes in 0-point activities.
        if (getCurrentValue() != other.getCurrentValue()) return false
        if (this.activityEntriesToday.size != other.activityEntriesToday.size) return false

        return true
    }

    override fun hashCode(): Int {
        var result = goal.hashCode()
        result = 31 * result + year
        result = 31 * result + day
        result = 31 * result + getCurrentValue()
        result = 31 * result + activityEntriesToday.size
        return result
    }
}
