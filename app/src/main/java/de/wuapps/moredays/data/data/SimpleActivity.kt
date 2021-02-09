package de.wuapps.moredays.data.data

import java.util.*

data class SimpleActivity (
    val date: Calendar,
    val name: String,
    val uid: Long,
    val goalId: Long,
    val points: Int
)