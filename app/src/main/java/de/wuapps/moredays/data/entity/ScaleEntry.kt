package de.wuapps.moredays.database.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.util.*

@Entity(foreignKeys = [ForeignKey(
  entity = Scale::class,
  parentColumns = ["uid"],
  childColumns = ["scale_id"],
  onDelete = CASCADE)],
  indices = [Index(value = ["scale_id"])])
class ScaleEntry {
  @PrimaryKey(autoGenerate = true)
  var uid:Long = 0
  @ColumnInfo(name = "scale_id")
  var scaleId:Long = 0
  @ColumnInfo(name = "timestamp")
  var timestamp: Date = Date()

  var value:Float = 0.toFloat()

  constructor() {}

  @Ignore
  constructor(scaleId:Long, value:Float) {
    this.scaleId = scaleId
    this.value = value
    this.timestamp = Date()
  }
  @Ignore
  constructor(scaleId:Long, value:Float, date:Date) {
    this.scaleId = scaleId
    this.value = value
    this.timestamp = date
  }
}