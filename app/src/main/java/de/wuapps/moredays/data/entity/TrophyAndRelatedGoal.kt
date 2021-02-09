package de.wuapps.moredays.data.entity

import androidx.room.Embedded
import androidx.room.Relation
import de.wuapps.moredays.database.entity.Activity
import de.wuapps.moredays.database.entity.Goal
import de.wuapps.moredays.database.entity.Trophy

data class TrophyAndRelatedGoal (
    @Embedded
    var trophy: Trophy,
    @Relation(
        parentColumn = "goal_id",
        entityColumn = "uid"
    )
    var goal: Goal
)