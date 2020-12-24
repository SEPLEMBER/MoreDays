package de.wuapps.moredays.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Entity
class Challenge { //serializable: damit kann es als Parameter zwischen Intents übergeben werden
  @PrimaryKey(autoGenerate = true)
  var uid:Long = 0
  var title:String=""
  var deadline:Date= Date()
  set(deadline) {
    val calendar = Calendar.getInstance()
    calendar.setTime(deadline)
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    field = calendar.getTime()
  }
  @ColumnInfo(name = "challenge_type")
  var challengeType:Int = 0
  @ColumnInfo(name = "value_type")
  var valueType:Int = 0
  @ColumnInfo(name = "desired_value")
  var desiredValue:Float = 0.toFloat()
  @ColumnInfo(name = "current_value")
  var currentValue:Float = 0.toFloat()
  @ColumnInfo(name = "id")
  var id:Long = 0
  @ColumnInfo(name = "symbol")
  var symbol:Int = 0
  @ColumnInfo(name = "status")
  var status = CHALLENGE_STATUS_UNDEFINED
  @ColumnInfo(name = "start_date")
  var startDate:Date=Date()
  set(startDate) {
    val calendar = Calendar.getInstance()
    calendar.setTime(startDate)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 1)
    field = calendar.getTime()
    val today = Date()
    if (this.deadline != null && (this.deadline.getTime() < startDate.getTime()))
    this.status = CHALLENGE_FAILED
    else if (status < 0)
    {
      if (startDate.getTime() <= today.getTime())
      status = CHALLENGE_RUNNING
      else
      status = CHALLENGE_NOT_STARTED
    }
  }
  constructor() {}
  @Ignore
  constructor(title:String, startDate:Date, deadline:Date, challengeType:Int, valueType:Int, desiredValue:Float, id:Long, symbol:Int) {
    this.uid = 0 //trigger autoincrement
    this.title = title
    this.deadline = deadline
    this.challengeType = challengeType
    this.valueType = valueType
    this.desiredValue = desiredValue
    this.symbol = symbol
    this.id = id
    this.startDate = startDate
    this.currentValue =  0.toFloat()
  }
  companion object {
    var CHALLENGE_TYPE_SCALE = 0
    var CHALLENGE_TYPE_ENTRY_TYPE = 1
    var CHALLENGE_TYPE_CATEGORY = 2
    var CHALLENGE_VALUE_TYPE_LESS = 0
    var CHALLENGE_VALUE_TYPE_MORE = 1
    var CHALLENGE_VALUE_TIMES = 2
    var CHALLENGE_VALUE_POINTS = 3
    var CHALLENGE_STATUS_UNDEFINED = -2
    var CHALLENGE_NOT_STARTED = -1
    var CHALLENGE_RUNNING = 0
    var CHALLENGE_FAILED = 1
    var CHALLENGE_SUCCEEDED = 2
  }
}