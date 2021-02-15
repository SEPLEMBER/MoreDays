package de.wuapps.moredays.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ActivityEntryAndRelatedData(
    @Embedded
    var activityEntry: ActivityEntry,
    @Relation(
        parentColumn = "goal_id",
        entityColumn = "uid"
    )
    var goal: Goal,
    @Relation(
        parentColumn = "activity_id",
        entityColumn = "uid"
    )
    var activity: Activity
)