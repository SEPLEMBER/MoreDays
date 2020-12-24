package de.wuapps.moredays.database.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(
  foreignKeys = [
    ForeignKey(entity = Goal::class,
      parentColumns = ["uid"],
      childColumns = ["goal_id"],
      onDelete = CASCADE
    )],
    indices = [Index(value = ["goal_id"])])
class Activity {
  @PrimaryKey(autoGenerate = true)
  var uid:Long = 0

  var name:String=""

  @ColumnInfo(name = "goal_id")
  var goalId:Long = 0

  var points:Int = 0

  @ColumnInfo(name = "is_active")
  var isActive:Boolean = false

  constructor(){}

  @Ignore
  constructor(name: String, goalId: Long, points: Int) : this() {
    this.uid = 0 //trigger autoincrement
    this.name = name
    this.goalId = goalId
    this.points = points
    this.isActive = true
  }

  @Override
  public override fun toString(): String {
    return String.format("id: %d, name: %s, points: %s", uid, name, points)
  }
}