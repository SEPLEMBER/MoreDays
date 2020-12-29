package de.wuapps.moredays.database.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.util.*

@Entity(
  foreignKeys = [
      ForeignKey(
    entity = Activity::class,
    parentColumns = ["uid"],
    childColumns = ["activity_id"],
    onDelete = CASCADE
  )],
    indices = [Index(value =  ["activity_id" ])])
class ActivityEntry {
    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0

    @ColumnInfo(name = "activity_id")
    var activityId: Long = 0

    @ColumnInfo(name = "goal_id")
    var goalId: Long = 0 //redundant but makes things easier :)

    @ColumnInfo(name = "timestamp")
    var timestamp: Calendar = Calendar.getInstance()

    constructor() {}

    @Ignore
    constructor(activityId: Long, goalId: Long, date: Calendar) {
        this.activityId = activityId
        this.timestamp = date
        this.goalId = goalId
    }
    @Ignore
    constructor(activityId: Long, goalId: Long) {
        this.activityId = activityId
        this.goalId = goalId
    }
}