package de.wuapps.moredays.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ScaleData(
    @Embedded
    var scaleEntry: ScaleEntry,
    @Relation(
        parentColumn = "scale_id",
        entityColumn = "uid"
    )
    var scale: Scale,
)