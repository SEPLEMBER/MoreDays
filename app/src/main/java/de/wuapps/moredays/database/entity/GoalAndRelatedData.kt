package de.wuapps.moredays.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class GoalAndRelatedData(
    //@Embedded
    //var scales: List<Scale> = ArrayList<Scale>(),
    @Embedded
    var goal: Goal? = null,
    @Relation(
        parentColumn = "uid",
        entityColumn = "goal_id"
    )
    var activities: List<Activity> = ArrayList(),
    @Relation(
        parentColumn = "uid",
        entityColumn = "goal_id"
    )
    var entries: List<ActivityEntry> = ArrayList()
)