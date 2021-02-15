package de.wuapps.moredays.database.data

import java.util.*

data class SimpleActivity (
    val date: Calendar,
    val name: String,
    val uid: Long,
    val goalId: Long,
    val points: Int
)