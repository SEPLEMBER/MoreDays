package de.wuapps.moredays.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class TrophyAndRelatedGoal (
    @Embedded
    var trophy: Trophy,
    @Relation(
        parentColumn = "goal_id",
        entityColumn = "uid"
    )
    var goal: Goal
)