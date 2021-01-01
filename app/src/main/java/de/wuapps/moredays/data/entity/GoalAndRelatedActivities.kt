package de.wuapps.moredays.data.entity

import androidx.room.Embedded
import androidx.room.Relation
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.Goal

data class GoalAndRelatedActivities(
    @Embedded
    var goal: Goal? = null,
    @Relation(
        parentColumn = "uid",
        entityColumn = "goal_id"
    )
    var activities: List<Activity> = ArrayList<Activity>()
)