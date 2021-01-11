package de.wuapps.moredays.data.entity

import androidx.room.Embedded
import androidx.room.Relation
import de.wuapps.moredays.database.entity.Scale
import de.wuapps.moredays.database.entity.ScaleEntry

data class ScaleAndEntries(
    @Embedded
    var scale: Scale? = null,
    @Relation(
        parentColumn = "uid",
        entityColumn = "scale_id"
    )
    var entries: List<ScaleEntry> = ArrayList()
)