package de.wuapps.moredays.database

import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.ActivityEntry
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.database.entity.Trophy
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import java.util.Calendar

class AnalysisTest : TestCase() {

    private val analysis = Analysis()
    private lateinit var yesterday: Calendar
    private lateinit var dayBeforeYesterday: Calendar
    private val today = Calendar.getInstance()
    private lateinit var tomorrow: Calendar

    @Before
    override fun setUp() {
        val dayOfYearToday = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DATE, -1)
        dayBeforeYesterday = Calendar.getInstance()
        dayBeforeYesterday.add(Calendar.DATE, -2)
        tomorrow = Calendar.getInstance()
        tomorrow.add(Calendar.DATE, 1)
    }

    fun testIsToday() {
        var actual = analysis.isToday(yesterday)
        assertEquals("is yesterday today", false, actual)
        actual = analysis.isToday(today)
        assertEquals("is today today", true, actual)
    }

    @Test
    fun testCheckForNewTrophies() {
        val goal = Goal()
        val entryId = 1L
        val date = today
        val trophies = ArrayList<Trophy>()
        val activityEntries = ArrayList<ActivityEntry>()

        //check when trophy made goal was made today already, i.e. we do more than 100% in this goal
        goal.uid = 1
        trophies.add(
            Trophy(
                Trophy.TROPHY_TYPE_MADE_GOAL,
                1,
                1,
                dayBeforeYesterday
            )
        ) //should be ignored, not today
        trophies.add(
            Trophy(
                Trophy.TROPHY_TYPE_MADE_GOAL,
                2,
                1,
                today
            )
        ) //should be ignored wrong goal
        trophies.add(Trophy(Trophy.TROPHY_TYPE_MADE_GOAL, 1, 1, today)) //should be considered
        var actual = analysis.checkForNewTrophies(goal, entryId, date, trophies, activityEntries)
        assertEquals("no new trophy for saame day and goal", 0, actual.size)
        trophies.clear()
        //check for new trophy goal made
        goal.points = 3
        activityEntries.add(
            ActivityEntry(
                Activity("test", 1, 1),
                yesterday
            )
        ) //should be ignored, not today
        activityEntries.add(
            ActivityEntry(
                Activity("test", 2, 1),
                today
            )
        ) //should be ignored, wrong goalid
        activityEntries.add(ActivityEntry(Activity("test", 1, 1))) //should be taken
        activityEntries.add(ActivityEntry(Activity("test", 1, 2))) //should be taken
        actual = analysis.checkForNewTrophies(goal, entryId, date, trophies, activityEntries)
        assertEquals("new trophy for goal made", 1, actual.size)
        assertEquals(
            "new trophy for goal made, type made goal",
            Trophy.TROPHY_TYPE_MADE_GOAL,
            actual[0].type
        )
        //check for 3 times this week
        trophies.clear()
        val startOfWeek = Calendar.getInstance()
        if (today.get(Calendar.DAY_OF_WEEK) == 1)
            startOfWeek.set(Calendar.DAY_OF_WEEK, 2)
        else
            startOfWeek.set(Calendar.DAY_OF_WEEK, 1)
        val midOfWeek = Calendar.getInstance()
        if (today.get(Calendar.DAY_OF_WEEK) == 3)
            midOfWeek.set(Calendar.DAY_OF_WEEK, 2)
        else
            midOfWeek.set(Calendar.DAY_OF_WEEK, 3)
        val endOfWeek = Calendar.getInstance()
        if (today.get(Calendar.DAY_OF_WEEK) == 5)
            endOfWeek.set(Calendar.DAY_OF_WEEK, 4)
        else
            endOfWeek.set(Calendar.DAY_OF_WEEK, 5)
        trophies.add(Trophy(Trophy.TROPHY_TYPE_MADE_GOAL, 1, 1, startOfWeek)) //should be considered
        trophies.add(
            Trophy(
                Trophy.TROPHY_TYPE_MADE_GOAL,
                2,
                1,
                today
            )
        ) //should be ignored wrong goal
        trophies.add(Trophy(Trophy.TROPHY_TYPE_MADE_GOAL, 1, 1, midOfWeek)) //should be considered
        trophies.add(Trophy(Trophy.TROPHY_TYPE_MADE_GOAL, 1, 1, endOfWeek)) //should be considered
        actual = analysis.checkForNewTrophies(goal, entryId, date, trophies, activityEntries)
        assertEquals("new trophies for 3 times in week", 2, actual.size)
        assertEquals(
            "new trophy  for 3 times in week, type made goal",
            Trophy.TROPHY_TYPE_MADE_GOAL,
            actual[0].type
        )
        assertEquals(
            "new trophy  for 3 times in week, type n times in week",
            Trophy.TROPHY_TYPE_MADE_GOAL_N_TIMES_IN_WEEK,
            actual[1].type
        )

        //check for 3 days in row -- depending on todays day, trophies may match 3times per week too
        trophies.clear()
        val dateLongerAgo = Calendar.getInstance()
        dateLongerAgo.add(Calendar.DATE, -4)
        trophies.add(
            Trophy(
                Trophy.TROPHY_TYPE_MADE_GOAL,
                1,
                1,
                dateLongerAgo
            )
        ) //should be ignored, 1 day gap
        trophies.add(
            Trophy(
                Trophy.TROPHY_TYPE_MADE_GOAL,
                1,
                1,
                dayBeforeYesterday
            )
        ) //should be considered
        trophies.add(Trophy(Trophy.TROPHY_TYPE_MADE_GOAL, 1, 1, yesterday)) //should be considered
        trophies.add(
            Trophy(
                Trophy.TROPHY_TYPE_MADE_GOAL,
                2,
                1,
                today
            )
        ) //should be ignored wrong goal
        actual = analysis.checkForNewTrophies(goal, entryId, date, trophies, activityEntries)
        actual = actual.filter { trophy -> trophy.type == Trophy.TROPHY_TYPE_GOAL_DAYS_IN_ROW }
        assertEquals("new trophy  for 3 times in row, type days in row", 1, actual.size)
        if (actual.isNotEmpty())
            assertEquals("new trophy  for 3 times in row, value 3", 3, actual[0].value)
        trophies.clear()
        //check for total == 5
        val dateBefore42Days = Calendar.getInstance()
        dateBefore42Days.add(Calendar.DATE, -42)
        val dateBefore20Days = Calendar.getInstance()
        dateBefore20Days.add(Calendar.DATE, -20)
        trophies.add(
            Trophy(
                Trophy.TROPHY_TYPE_MADE_GOAL,
                1,
                1,
                dateBefore42Days
            )
        ) //should be considered
        trophies.add(
            Trophy(
                Trophy.TROPHY_TYPE_MADE_GOAL,
                1,
                1,
                dateBefore20Days
            )
        ) //should be considered
        trophies.add(
            Trophy(
                Trophy.TROPHY_TYPE_MADE_GOAL,
                1,
                1,
                dateLongerAgo
            )
        ) //should be considered
        trophies.add(
            Trophy(
                Trophy.TROPHY_TYPE_MADE_GOAL,
                1,
                1,
                dayBeforeYesterday
            )
        ) //should be considered
        trophies.add(Trophy(Trophy.TROPHY_TYPE_MADE_GOAL, 1, 1, yesterday)) //should be considered
        trophies.add(
            Trophy(
                Trophy.TROPHY_TYPE_MADE_GOAL,
                2,
                1,
                today
            )
        ) //should be ignored wrong goal
        actual = analysis.checkForNewTrophies(goal, entryId, date, trophies, activityEntries)
        actual = actual.filter { trophy -> trophy.type == Trophy.TROPHY_TYPE_GOAL_TOTAL }
        assertEquals("new trophy for total of five, type total", 1, actual.size)
        if (actual.isNotEmpty())
            assertEquals("new trophy for total of five, value 3", 5, actual[0].value)
    }
}