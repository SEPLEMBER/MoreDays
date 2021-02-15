package de.wuapps.moredays.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ScaleAndEntries(
    @Embedded
    var scale: Scale? = null,
    @Relation(
        parentColumn = "uid",
        entityColumn = "scale_id"
    )
    var entries: List<ScaleEntry> = ArrayList()
)