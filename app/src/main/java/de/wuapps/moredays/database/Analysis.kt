package de.wuapps.moredays.database

import de.wuapps.moredays.database.entity.ActivityEntry
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.database.entity.Trophy
import de.wuapps.moredays.utilities.isSameDay
import de.wuapps.moredays.utilities.isSameWeek
import java.util.*
import kotlin.collections.ArrayList

class Analysis {


    fun isToday(date: Calendar): Boolean {
        val today = Calendar.getInstance()
        return today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
    }

    //gibt null oder die schwierigste damit verbundene Trophäe zurück -- die anderen werden nur hinzugefügt und nicht alle angezeigt, z.B. Kategorie geschafft wird nicht angezeigt, wenn damit alle Kategorien geschafft wurden.
    /**
     *
     */
    fun checkForNewTrophies(
        goal: Goal,
        entryId: Long,
        date: Calendar,
        trophies: List<Trophy>,
        activityEntries: List<ActivityEntry>
    ): List<Trophy> {
        var newTrophies = ArrayList<Trophy>()
        var trophy: Trophy?
        //check for trophy for given date for goal
        val trophiesOfGivenDateAndGoal = trophies.filter { t ->  t.goalId == goal.uid && t.type == Trophy.TROPHY_TYPE_MADE_GOAL && t.timestamp.isSameDay(date)}
        if (trophiesOfGivenDateAndGoal.isNotEmpty()) {
            return newTrophies //handled already, maybe the last entry made it and the user goes above the desired value...
        }
        val relevantActivityEntries = activityEntries.filter { activityEntry ->  activityEntry.goalId == goal.uid && activityEntry.timestamp.isSameDay(date)}
        val sum: Int = relevantActivityEntries.sumOf{ it.points }
        if (sum >= goal.points) {
            trophy = Trophy(
                Trophy.TROPHY_TYPE_MADE_GOAL, goal.uid, 1,
                entryId
            )
            trophy.timestamp = date
            newTrophies.add(trophy)
        }

        //check for special trophies based on goal made -- but not for entries added for the past
        if (isToday(date) && newTrophies.size > 0) {
            val trophiesOfGivenGoalOfTypeGoalMade = trophies.filter { t ->  t.goalId == goal.uid && t.type == Trophy.TROPHY_TYPE_MADE_GOAL}
            val trophiesOfGivenGoalMadeInThisWeek = trophiesOfGivenGoalOfTypeGoalMade.filter { t -> t.timestamp.isSameWeek(date)}
            var currentInDb: Int
            var current: Int
            //check if at least 3 times in this week
            current = trophiesOfGivenGoalMadeInThisWeek.size
            if (current >= 3) {
                trophy = Trophy(
                    Trophy.TROPHY_TYPE_MADE_GOAL_N_TIMES_IN_WEEK,
                    goal.uid,
                    current,
                    entryId
                )
                trophy.timestamp = date
                newTrophies.add(trophy)
            }
            //check for days in row
            currentInDb = -1
            var matchTrophy = trophies.filter { t ->  t.goalId == goal.uid && t.type == Trophy.TROPHY_TYPE_GOAL_DAYS_IN_ROW}.maxByOrNull { it.value }
            if (matchTrophy!=null) currentInDb = matchTrophy.value
            current = countDaysInRow(trophiesOfGivenGoalOfTypeGoalMade)
            if (current > 2 && current > currentInDb) {
                trophy = Trophy(
                    Trophy.TROPHY_TYPE_GOAL_DAYS_IN_ROW, goal.uid, current,
                    entryId
                )
                trophy.timestamp = date
                newTrophies.add(trophy)
            }
            //check total increased by 5, 10, 25
            currentInDb = -1
            matchTrophy = trophies.filter { t ->  t.goalId == goal.uid && t.type == Trophy.TROPHY_TYPE_GOAL_TOTAL}.maxByOrNull { it.value }
            if (matchTrophy!=null) currentInDb = matchTrophy.value
            current = trophiesOfGivenGoalOfTypeGoalMade.size
            if (current > 4 && current > currentInDb && (current == 5 || current == 10 || current % 25 == 0)) {
                trophy = Trophy(
                    Trophy.TROPHY_TYPE_GOAL_TOTAL, goal.uid, current,
                    entryId
                )
                trophy.timestamp = date
                newTrophies.add(trophy)
            }
        }
        return newTrophies
    }

    /**
     * Count the items with days in row within the given list of trophies, starting from now.
     */
    private fun countDaysInRow(relevantTrophies: List<Trophy>): Int {
        val sortedTrophiesrelevantTrophies = relevantTrophies.sortedByDescending { trophy -> trophy.timestamp }
        var interval = 1 //trophy just made (type made goal) is not part of this list, hence start with yesterday
        val today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        var i = 0
        while ( i<sortedTrophiesrelevantTrophies.size && sortedTrophiesrelevantTrophies.get(i).timestamp.get(Calendar.DAY_OF_YEAR) == (today - interval) ){
            interval++
            i++
        }
        return interval
    }

}