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
  var uid:Long = 0
  var text:String=""
  var type:Int = 0
  @ColumnInfo(name = "goal_id")
  var goalId:Long = 0
  var timestamp:Calendar = Calendar.getInstance()
  var value:Int = 0
  @ColumnInfo(name = "activity_entry_id")
  var activityEntryId:Long = 0
  constructor()

    @Ignore
  constructor(type:Int, goalId:Long, activityEntryId:Long) {
    this.type = type
    this.goalId = goalId
    this.timestamp = Calendar.getInstance()
    this.value = -1
    this.activityEntryId = activityEntryId
  }
  @Ignore
  constructor(text:String, type:Int, goalId:Long, value:Int, activityEntryId:Long) {
    this.timestamp = Calendar.getInstance()
    this.text = text
    this.type = type
    this.goalId = goalId
    this.value = value
    this.activityEntryId = activityEntryId
  }
}