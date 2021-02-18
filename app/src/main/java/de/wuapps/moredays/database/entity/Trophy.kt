package de.wuapps.moredays.database.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.NO_ACTION
import java.text.SimpleDateFormat
import java.time.Year
import java.util.*


@Entity(
    foreignKeys = [ForeignKey(
        entity = ActivityEntry::class,
        parentColumns = ["uid"],
        childColumns = ["activity_entry_id"],
        onUpdate = NO_ACTION,
        onDelete = CASCADE
    )],
    indices = [Index(value = ["activity_entry_id"])]
)
class Trophy {
    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0
    var type: Int = 0

    @ColumnInfo(name = "goal_id")
    var goalId: Long = 0
    var timestamp: Calendar = Calendar.getInstance()
    var value: Int = 0

    @ColumnInfo(name = "activity_entry_id")
    var activityEntryId: Long = 0

    constructor()

    @Ignore
    constructor(type: Int, goalId: Long, activityEntryId: Long) {
        this.type = type
        this.goalId = goalId
        this.timestamp = Calendar.getInstance()
        this.value = -1
        this.activityEntryId = activityEntryId
    }

    @Ignore
    constructor(type: Int, goalId: Long, value: Int, activityEntryId: Long) {
        this.timestamp = Calendar.getInstance()
        this.type = type
        this.goalId = goalId
        this.value = value
        this.activityEntryId = activityEntryId
    }

    companion object {
        var TROPHY_TYPE_GOAL_DAYS_IN_ROW = 0
        var TROPHY_TYPE_GOAL_TOTAL = 1
        var TROPHY_TYPE_MADE_GOAL = 3
        var TROPHY_TYPE_MADE_GOAL_N_TIMES_IN_WEEK = 4

    }

    fun isOfSameDay(trophy: Trophy):Boolean{
        return trophy.timestamp.get(Calendar.YEAR) == timestamp.get(Calendar.YEAR) && trophy.timestamp.get(Calendar.DAY_OF_YEAR) == timestamp.get(Calendar.DAY_OF_YEAR)
    }

    fun isOfSameDay(date: Calendar):Boolean{
        return date.get(Calendar.YEAR) == timestamp.get(Calendar.YEAR) && date.get(Calendar.DAY_OF_YEAR) == timestamp.get(Calendar.DAY_OF_YEAR)
    }

    fun getFormattedtTimestamp(format: String): String {
        val formatter = SimpleDateFormat(format)
        return formatter.format(timestamp.time)
    }
}

//var TROPHY_SYMBOL_ROW: Int = R.string.font_icon_link_solid
//var TROPHY_SYMBOL_TOTAL: Int = R.string.font_icon_23trophy_solid
//var TROPHY_SYMBOL_CATEGORY: Int = R.string.font_icon_28award_solid
//var TROPHY_SYMBOL_ALL_CATEGORIES: Int = R.string.font_icon_29medal_solid
//var TROPHY_SYMBOL_WEEK: Int = R.string.font_icon_23trophy_solid