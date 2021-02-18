package de.wuapps.moredays.database

import android.util.Log
import de.wuapps.moredays.database.dao.ActivityEntryDao
import de.wuapps.moredays.database.dao.ChallengeDao
import de.wuapps.moredays.database.dao.ScaleEntryDao
import de.wuapps.moredays.database.dao.TrophyDao
import de.wuapps.moredays.database.entity.*
import de.wuapps.moredays.ui.tools.atEndOfDay
import kotlinx.coroutines.flow.collectLatest
import java.util.*
import kotlin.collections.ArrayList

class Analysis {

    /**
     * Update all pending/not running/eventually finished challenges.
     *
     */
    fun checkChallenges(challenges: List<Challenge>, activityEntries: List<ActivityEntry>, scales: List<Scale>, scaleEntries: List<ScaleEntry>, trophies: List<Trophy>):List<Challenge> {
        val changedChallenges = ArrayList<Challenge>()
        val now = Calendar.getInstance()
        for (c in challenges) {
            var challengeNeedsUpdate = false
            if (c.status == Challenge.CHALLENGE_FAILED || c.status == Challenge.CHALLENGE_SUCCEEDED) {
                //nothing to do
            } else {
                //check deadline for running or not started
                if ((c.status == Challenge.CHALLENGE_RUNNING || c.status == Challenge.CHALLENGE_NOT_STARTED) && (c.deadline.time < now.time)
                ) {
                    c.status = Challenge.CHALLENGE_FAILED
                    challengeNeedsUpdate = true
                }
                else if (c.status == Challenge.CHALLENGE_NOT_STARTED && c.startDate
                        .getTime() < now.time
                ) {
                    c.status = Challenge.CHALLENGE_RUNNING
                    challengeNeedsUpdate = true
                }
                //check running
                if (c.status == Challenge.CHALLENGE_RUNNING) {
                    val oldValue = c.currentValue
                    c.currentValue = calculateCurrentValueForChallenge(c, scaleEntries, activityEntries, trophies)
                    if (oldValue != c.currentValue) challengeNeedsUpdate = true
                    if (c.valueType == Challenge.CHALLENGE_VALUE_TYPE_LESS) {
                        if (c.currentValue < c.desiredValue) {
                            c.status = Challenge.CHALLENGE_SUCCEEDED
                            challengeNeedsUpdate = true
                        }
                    } else {
                        if (c.currentValue > c.desiredValue) {
                            c.status = Challenge.CHALLENGE_SUCCEEDED
                            challengeNeedsUpdate = true
                        }
                    }
                }
                if (challengeNeedsUpdate) changedChallenges.add(c)
            }
        }
        return changedChallenges
    }


    /**
     * Get the current value of a given challenge, i.e. calculate it according to the challenge
     * type and the data in the database.
     */
     fun calculateCurrentValueForChallenge(
        challenge: Challenge,
        scaleEntries: List<ScaleEntry>,
        activityEntries: List<ActivityEntry>,
        trophies: List<Trophy>
    ): Int {
        var currentValue = 0
        if (challenge.challengeType == Challenge.CHALLENGE_TYPE_SCALE) {
            val relevantScaleEntries = scaleEntries.filter { scaleEntry ->  scaleEntry.scaleId == challenge.id}
            if (relevantScaleEntries.size > 0)
                currentValue = relevantScaleEntries.sortedByDescending { scale -> scale.timestamp }.first().value.toInt()
        } else if (challenge.challengeType == Challenge.CHALLENGE_TYPE_ACTIVITY) {
            val entriesInTimespan = activityEntries.filter { activityEntry -> activityEntry.uid == challenge.id && activityEntry.timestamp<challenge.deadline && activityEntry.timestamp>challenge.startDate }
            currentValue = entriesInTimespan.size
        } else if (challenge.challengeType == Challenge.CHALLENGE_TYPE_GOAL) {
            if (challenge.valueType == Challenge.CHALLENGE_VALUE_TIMES) {
                val relevantTrophies = trophies.filter { trophy -> trophy.goalId == challenge.id && trophy.timestamp<challenge.deadline && trophy.timestamp>challenge.startDate }
                currentValue = relevantTrophies.size
            } else if (challenge.valueType == Challenge.CHALLENGE_VALUE_POINTS) {
                val entriesInTimespan = activityEntries.filter { activityEntry -> activityEntry.goalId == challenge.id && activityEntry.timestamp<challenge.deadline && activityEntry.timestamp>challenge.startDate }
                currentValue = entriesInTimespan.sumBy { it.points }
            }
        }
        return currentValue
    }

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
        var trophy: Trophy? = null
        //check for trophy for given date for goal
        val trophiesOfGivenDateAndGoal = trophies.filter { trophy ->  trophy.goalId == goal.uid && trophy.type == Trophy.TROPHY_TYPE_MADE_GOAL && trophy.isOfSameDay(date)}
        if (trophiesOfGivenDateAndGoal.size > 0)
            return newTrophies //handled already, maybe the last entry made it and the user goes above the desired value...
        val relevantActivityEntries = activityEntries.filter { activityEntry ->  activityEntry.goalId == goal.uid && activityEntry.isOfSameDay(date)}
        val sum: Int = relevantActivityEntries.sumBy { it.points }
        if (sum >= goal.points) {
            trophy = Trophy(
                Trophy.TROPHY_TYPE_MADE_GOAL, goal.uid, 1,
                entryId
            )
            trophy.timestamp = date.atEndOfDay()
            newTrophies.add(trophy)
        }

        //check for special trophies based on goal made -- but not for entries added for the past
        if (isToday(date) && newTrophies.size > 0) {
            val trophiesOfGivenGoal = trophies.filter { trophy ->  trophy.goalId == goal.uid}.sortedByDescending { it.timestamp }
            val trophiesOfGivenGoalOfTypeGoalMade =
                trophiesOfGivenGoal.filter { trophy -> trophy.type == Trophy.TROPHY_TYPE_MADE_GOAL }
            var currentInDb = -1
            var current = -1
            //check if at least 3 times in this week
            current = getCountInThisWeek(trophiesOfGivenGoalOfTypeGoalMade)
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
            currentInDb = -1
            var matchTrophy = trophies.filter { trophy ->  trophy.goalId == goal.uid && trophy.type == Trophy.TROPHY_TYPE_GOAL_DAYS_IN_ROW}.maxByOrNull { it.value }
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
            matchTrophy = trophies.filter { trophy ->  trophy.goalId == goal.uid && trophy.type == Trophy.TROPHY_TYPE_GOAL_TOTAL}.maxByOrNull { it.value }
            if (matchTrophy!=null) currentInDb = matchTrophy.value
            current = trophiesOfGivenGoalOfTypeGoalMade.size
            if (current > currentInDb && (current == 5 || current == 10 || current % 25 == 0)) {
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
    fun countDaysInRow(relevantTrophies: List<Trophy>): Int {
        var interval = 0
        val today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        var i = 0
        while ( i<relevantTrophies.size && relevantTrophies.get(i).timestamp.get(Calendar.DAY_OF_YEAR) >= (today - interval) ){
            interval++
            i++
        }
        return interval
    }


    /**
     * Count the numbers of trophies for the given week
     */
    fun getCountInThisWeek(relevantTrophies: List<Trophy>): Int {
        if (relevantTrophies.size <= 1) return 0
        var count = 0
        val currentWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
        return relevantTrophies.filter { trophy -> trophy.timestamp.get(Calendar.WEEK_OF_YEAR) == currentWeek }
            .count()
    }

}