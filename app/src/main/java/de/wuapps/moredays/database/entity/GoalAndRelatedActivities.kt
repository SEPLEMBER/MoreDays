package de.wuapps.moredays.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class GoalAndRelatedActivities(
    @Embedded
    var goal: Goal? = null,
    @Relation(
        parentColumn = "uid",
        entityColumn = "goal_id"
    )
    var activities: List<Activity> = ArrayList()
)