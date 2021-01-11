package de.wuapps.moredays.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import de.wuapps.moredays.utilities.DEFAULLT_ICON_ID
import de.wuapps.moredays.utilities.DEFAULT_COLOR
import kotlinx.coroutines.flow.Flow
import kotlinx.parcelize.Parcelize


@Entity
@Parcelize
class Goal (
  @PrimaryKey(autoGenerate = true)
  var uid:Long,
  var name:String,
  var color:Int,
  var points:Int,
  var symbol:Int,
  @ColumnInfo(name = "order_number")
  var orderNumber:Int,
  @ColumnInfo(name = "is_active")
  var isActive:Boolean,
  @ColumnInfo(name = "is_old") //old = formerly used, should not be able to be selected, but keep associated data and show it in bar chart
  var isOld:Boolean) : Parcelable {
  constructor():this(0, "", DEFAULT_COLOR, 0, DEFAULLT_ICON_ID, 0, true, false)

    //needed for spinner
  override fun toString(): String {
    return "$name ($points P.)"
  }

  override fun equals(other: Any?): Boolean{
    if (other == null)
      return false
    if (!(other is Goal))
      return false
    val otherGoal = other as Goal
    return otherGoal.uid == uid && otherGoal.color == color && otherGoal.points == points && otherGoal.isActive == isActive && otherGoal.isOld == isOld
  }
}