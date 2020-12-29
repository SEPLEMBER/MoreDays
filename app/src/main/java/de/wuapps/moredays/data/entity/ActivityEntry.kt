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
    var activityId: Long  = 0

    @ColumnInfo(name = "goal_id")
    var goalId: Long  = 0

    var points: Int = 0

    @ColumnInfo(name = "timestamp")
    var timestamp: Calendar = Calendar.getInstance()

    constructor() {}

    @Ignore
    constructor(activity:Activity, date: Calendar) {
        this.goalId = activity.goalId
        this.points = activity.points
        this.activityId = activity.uid
        this.timestamp = date
    }
    @Ignore
    constructor(activity:Activity) {
        this.goalId = activity.goalId
        this.points = activity.points
        this.activityId = activity.uid
    }

}