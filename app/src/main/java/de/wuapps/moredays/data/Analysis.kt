package de.wuapps.moredays.data

import de.wuapps.moredays.database.dao.ActivityEntryDao
import de.wuapps.moredays.database.dao.ChallengeDao
import de.wuapps.moredays.database.dao.ScaleEntryDao
import de.wuapps.moredays.database.dao.TrophyDao
import de.wuapps.moredays.database.entity.Challenge
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.database.entity.Trophy
import kotlinx.coroutines.flow.collectLatest
import java.util.*
import kotlin.collections.ArrayList

class Analysis {

    /**
     * Update all pending/not running/eventually finished challenges.
     *
     */
    suspend fun updateChallenges(dao: ChallengeDao, scaleEntryDao: ScaleEntryDao,
                                 entryDao: ActivityEntryDao,
                                 trophyDao: TrophyDao) {
        val allChallenges: ArrayList<Challenge> = ArrayList<Challenge>()
        dao.getAll().collectLatest { allChallenges.addAll(it) }

        val now = Calendar.getInstance()
        for (c in allChallenges) {
            if (c.status == Challenge.CHALLENGE_FAILED || c.status == Challenge.CHALLENGE_SUCCEEDED) {
                //nothing to do
            } else {
                //check deadline for running or not started
                if ((c.status == Challenge.CHALLENGE_RUNNING || c.status == Challenge.CHALLENGE_NOT_STARTED) && (c.deadline.time < now.time)
                ) c.status = Challenge.CHALLENGE_FAILED
                else if (c.status == Challenge.CHALLENGE_NOT_STARTED && c.startDate
                        .getTime() < now.time
                ) c.status = Challenge.CHALLENGE_RUNNING
                //check running
                if (c.status == Challenge.CHALLENGE_RUNNING) {
                    c.currentValue = calculateCurrentValueForChallenge(c, scaleEntryDao, entryDao, trophyDao)
                    if (c.valueType == Challenge.CHALLENGE_VALUE_TYPE_LESS) {
                        if (c.currentValue < c.desiredValue) {
                            c.status = Challenge.CHALLENGE_SUCCEEDED
                        }
                    } else {
                        if (c.currentValue >= c.desiredValue) {
                            c.status = Challenge.CHALLENGE_SUCCEEDED
                        }
                    }
                }
                dao.update(c)
            }
        }
    }


    /**
     * Get the current value of a given challenge, i.e. calculate it according to the challenge
     * type and the data in the database.
     */
    suspend fun calculateCurrentValueForChallenge(
        challenge: Challenge,
        scaleEntryDao: ScaleEntryDao,
        entryDao: ActivityEntryDao,
        trophyDao: TrophyDao
    ): Float {
        var currentValue = -1f
        if (challenge.challengeType == Challenge.CHALLENGE_TYPE_SCALE) {
            currentValue = scaleEntryDao.getLatestValue(challenge.uid)
        } else if (challenge.challengeType == Challenge.CHALLENGE_TYPE_ENTRY_TYPE) {
            currentValue = entryDao.getCountToActivityInTimeSpan(
                challenge.uid,
                challenge.startDate,
                challenge.deadline
            ).toFloat()
        } else if (challenge.challengeType == Challenge.CHALLENGE_TYPE_CATEGORY) {
            if (challenge.valueType == Challenge.CHALLENGE_VALUE_TIMES) {
                currentValue = trophyDao.getCountToTimeSpan(
                    challenge.uid,
                    Trophy.TROPHY_TYPE_MADE_GOAL,
                    challenge.startDate,
                    challenge.deadline
                ).toFloat()
            } else if (challenge.valueType == Challenge.CHALLENGE_VALUE_POINTS) {
                currentValue = entryDao.getSumToGoalInTimeSpan(
                    challenge.uid,
                    challenge.startDate,
                    challenge.deadline
                ).toFloat()
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
    suspend fun checkForNewTrophies(
        goal: Goal,
        entryId: Long,
        date: Calendar,
        trophyDao: TrophyDao,
        activityEntryDao: ActivityEntryDao
    ): List<Trophy> {
        var newTrophies = ArrayList<Trophy>()
        var trophy: Trophy? = null
        //check for trophy for today for goal
        if (trophyDao.getCountToGoalAndTypeForGivenDate(
                goal.uid,
                Trophy.TROPHY_TYPE_MADE_GOAL,
                date
            ) > 0
        )
            return newTrophies //handled already, maybe the last entry made it and the user goes above the desired value...
        val sum: Int = activityEntryDao.getSumByGoalIdForGivenDate(goal.uid, date)
        if (sum >= goal.points) {
            trophy = Trophy(
                Trophy.TROPHY_TYPE_MADE_GOAL, goal.uid, 1,
                entryId
            )
            trophy.timestamp = date
            trophyDao.insert(trophy)
            newTrophies.add(trophy)
        }

        //check for special trophies based on goal made -- but not for entries added for the past
        if (isToday(date) && newTrophies.size > 0) {
            val trophiesOfGivenGoal: List<Trophy> =
                trophyDao.getByGoalId(goal.uid).sortedByDescending { it.timestamp }
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
                trophyDao.insert(trophy)
                newTrophies.add(trophy)
            }
            currentInDb = trophyDao.getMaxValueToGoalAndType(
                goal.uid,
                Trophy.TROPHY_TYPE_GOAL_DAYS_IN_ROW
            ) ?: -1
            current = countDaysInRow(trophiesOfGivenGoalOfTypeGoalMade)
            if (current > 2 && current > currentInDb) {
                trophy = Trophy(
                    Trophy.TROPHY_TYPE_GOAL_DAYS_IN_ROW, goal.uid, current,
                    entryId
                )
                trophy.timestamp = date
                trophyDao.insert(trophy)
                newTrophies.add(trophy)
            }
            //check total increased by 5, 10, 25
            currentInDb = trophyDao.getMaxValueToGoalAndType(
                goal.uid,
                Trophy.TROPHY_TYPE_GOAL_TOTAL
            ) ?: -1
            current = trophiesOfGivenGoalOfTypeGoalMade.size
            if (current > currentInDb && (current == 5 || current == 10 || current % 25 == 0)) {
                trophy = Trophy(
                    Trophy.TROPHY_TYPE_GOAL_TOTAL, goal.uid, current,
                    entryId
                )
                trophy.timestamp = date
                trophyDao.insert(trophy)
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