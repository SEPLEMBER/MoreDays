package de.wuapps.moredays.database.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.NO_ACTION
import java.util.*


@Entity(
  foreignKeys = [ForeignKey(entity = ActivityEntry::class,
    parentColumns = ["uid"],
    childColumns = ["activity_entry_id"],
    onUpdate = NO_ACTION,
    onDelete = CASCADE)],
    indices = [Index(value = ["activity_entry_id"])])
class Trophy {
  @PrimaryKey(autoGenerate = true)
  var uid:Int = 0
  var text:String=""
  var type:Int = 0
  @ColumnInfo(name = "goal_id")
  var goalId:Int = 0
  var timestamp:Date= Date()
  var value:Int = 0
  @ColumnInfo(name = "activity_entry_id")
  var activityEntryId:Int = 0
  constructor() {}
  @Ignore
  constructor(type:Int, goalId:Int, activityEntryId:Int) {
    this.type = type
    this.goalId = goalId
    this.timestamp = Date()
    this.value = -1
    this.activityEntryId = activityEntryId
  }
  @Ignore
  constructor(text:String, type:Int, goalId:Int, value:Int, activityEntryId:Int) {
    this.timestamp = Date()
    this.text = text
    this.type = type
    this.goalId = goalId
    this.activityEntryId = activityEntryId
  }
}