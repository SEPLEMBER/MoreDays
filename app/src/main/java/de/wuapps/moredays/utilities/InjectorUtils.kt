package de.wuapps.moredays.utilities

import android.content.Context
import androidx.fragment.app.Fragment
import de.wuapps.moredays.database.MoreDaysDatabase
import de.wuapps.moredays.database.entity.*
import de.wuapps.moredays.ui.activity.ActivityViewModelFactory
import de.wuapps.moredays.ui.challenge.ChallengeListViewModelFactory
import de.wuapps.moredays.ui.challenge.ChallengeViewModelFactory
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
        return ActivityViewModelFactory(MoreDaysDatabase.getInstance(context.applicationContext).activityDao, goal, activity)
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
        return GoalViewModelFactory(MoreDaysDatabase.getInstance(context.applicationContext).goalDao, goal)
    }

    fun provideHomeViewModelFactory(
        context: Context
    ): HomeViewModelFactory {
        val db = MoreDaysDatabase.getInstance(context.applicationContext)
        return HomeViewModelFactory(db.goalDao, db.scaleDao, db.challengeDao, db.activityEntryDao, db.scaleEntryDao, db.trophyDao)
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
        return JournalViewModelFactory(MoreDaysDatabase.getInstance(context.applicationContext).journalDao, journal)
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
        return ScaleViewModelFactory(MoreDaysDatabase.getInstance(context.applicationContext).scaleDao, scale)
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

    fun provideChallengeListViewModelFactory(
        context: Context
    ): ChallengeListViewModelFactory {
        return ChallengeListViewModelFactory(MoreDaysDatabase.getInstance(context.applicationContext).challengeDao)
    }

    fun provideChallengeViewModelFactory(
        context: Context,
        challenge: Challenge?
    ): ChallengeViewModelFactory {
        val db = MoreDaysDatabase.getInstance(context.applicationContext)
        return ChallengeViewModelFactory(db.challengeDao, db.goalDao, db.activityDao, db.scaleDao, challenge)
    }

}
