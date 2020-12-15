package de.wuapps.moredays.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import de.wuapps.moredays.database.converter.ListGoalTypeConverter;
import org.jetbrains.annotations.NotNull
import java.text.SimpleDateFormat
import java.util.*


@Entity
class HundredPercentValue {
  @PrimaryKey
  @NotNull
  var date:String
  @NotNull
  var points:Int = 0
  @NotNull
  @ColumnInfo(name = "active_goals")
  var activeGoals:String=""
  val activeGoalsAsList:List<Goal>
  get() {
    return ListGoalTypeConverter.convertStringToGoalList(activeGoals)
  }
  @Ignore
  constructor(date:String, points:Int, activeGoals:List<Goal>) {
    this.date = date
    this.points = points
    this.setActiveGoals(activeGoals)
  }
  constructor() {
    this.date = (SimpleDateFormat("yyMMdd").format(Date()))
    this.points = 0
    this.activeGoals = ""
  }
  fun setActiveGoals(activeGoals:List<Goal>) {
    this.activeGoals = ListGoalTypeConverter.convertListGoalToString(activeGoals)
  }
}