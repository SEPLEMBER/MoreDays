package de.wuapps.moredays.utilities

import android.content.Context
import androidx.preference.PreferenceManager
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.database.entity.Journal
import de.wuapps.moredays.database.entity.Scale
import de.wuapps.moredays.ui.activity.ActivityViewModelFactory
import de.wuapps.moredays.ui.charts.GoalsChartsViewModelFactory
import de.wuapps.moredays.ui.charts.ScalesChartViewModelFactory
import de.wuapps.moredays.ui.goal.GoalListViewModelFactory
import de.wuapps.moredays.ui.goal.GoalViewModelFactory
import de.wuapps.moredays.ui.home.HomeViewModelFactory
import de.wuapps.moredays.ui.journal.JournalListViewModelFactory
import de.wuapps.moredays.ui.journal.JournalViewModelFactory
import de.wuapps.moredays.ui.scale.ScaleListViewModelFactory
import de.wuapps.moredays.ui.scale.ScaleViewModelFactory
import de.wuapps.moredays.ui.tools.history.ActivityEntryListViewModelFactory
import de.wuapps.moredays.ui.tools.trophy.TrophyListViewModelFactory

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {

    fun provideActivityViewModelFactory(
        context: Context,
        goal: Goal?,
        activity: Activity?
    ): ActivityViewModelFactory {
        return ActivityViewModelFactory(
            MoreDaysDatabase.getInstance(context.applicationContext).activityDao,
            goal,
            activity
        )
    }

    fun provideGoalListViewModelFactory(
        context: Context
    ): GoalListViewModelFactory {
        return GoalListViewModelFactory(MoreDaysDatabase.getInstance(context.applicationContext).goalDao)
    }

    fun provideGoalViewModelFactory(
        context: Context,
        goal: Goal?
    ): GoalViewModelFactory {
        val db = MoreDaysDatabase.getInstance(context.applicationContext)
        return GoalViewModelFactory(db.goalDao, db.hundredPercentValueDao, goal)
    }

    fun provideHomeViewModelFactory(
        context: Context
    ): HomeViewModelFactory {
        val db = MoreDaysDatabase.getInstance(context.applicationContext)
        return HomeViewModelFactory(
            db.goalDao,
            db.scaleDao,
            db.activityEntryDao,
            db.scaleEntryDao,
            db.trophyDao
        )
    }

    fun provideJournalListViewModelFactory(
        context: Context
    ): JournalListViewModelFactory {
        return JournalListViewModelFactory(MoreDaysDatabase.getInstance(context.applicationContext).journalDao)
    }

    fun provideJournalViewModelFactory(
        context: Context,
        journal: Journal?
    ): JournalViewModelFactory {
        return JournalViewModelFactory(
            MoreDaysDatabase.getInstance(context.applicationContext).journalDao,
            journal,
            PreferenceManager.getDefaultSharedPreferences(context)
        )
    }

    fun provideScaleListViewModelFactory(
        context: Context
    ): ScaleListViewModelFactory {
        return ScaleListViewModelFactory(MoreDaysDatabase.getInstance(context.applicationContext).scaleDao)
    }

    fun provideScaleViewModelFactory(
        context: Context,
        scale: Scale?
    ): ScaleViewModelFactory {
        return ScaleViewModelFactory(
            MoreDaysDatabase.getInstance(context.applicationContext).scaleDao,
            scale
        )
    }

    fun provideActivityEntryListViewModelFactory(
        context: Context
    ): ActivityEntryListViewModelFactory {
        return ActivityEntryListViewModelFactory(MoreDaysDatabase.getInstance(context.applicationContext).activityEntryDao)
    }

    fun provideTrophyListViewModelFactory(
        context: Context
    ): TrophyListViewModelFactory {
        return TrophyListViewModelFactory(MoreDaysDatabase.getInstance(context.applicationContext).trophyDao)
    }

    fun provideGoalsChartsViewModelFactory(
        context: Context
    ): GoalsChartsViewModelFactory {
        val db = MoreDaysDatabase.getInstance(context.applicationContext)
        return GoalsChartsViewModelFactory(
            db.goalDao,
            db.activityEntryDao,
            db.trophyDao,
            db.hundredPercentValueDao
        )
    }

    fun provideScalesChartViewModelFactory(
        context: Context
    ): ScalesChartViewModelFactory {
        val db = MoreDaysDatabase.getInstance(context.applicationContext)
        return ScalesChartViewModelFactory(db.scaleEntryDao, db.scaleDao)
    }

}
