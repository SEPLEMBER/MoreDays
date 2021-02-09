package de.wuapps.moredays.data.entity

import androidx.room.Embedded
import androidx.room.Relation
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.ActivityEntry
import de.wuapps.moredays.database.entity.Goal

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