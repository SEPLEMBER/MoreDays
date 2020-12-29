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

    @ColumnInfo(name = "timestamp")
    var timestamp: Date = Date()

    constructor() {}

    @Ignore
    constructor(activityId: Long, date: Date) {
        this.activityId = activityId
        this.timestamp = date
    }
    @Ignore
    constructor(activityId: Long) {
        this.activityId = activityId
    }
}