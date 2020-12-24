package de.wuapps.moredays.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.coroutines.flow.Flow
import kotlinx.parcelize.Parcelize


@Entity
@Parcelize
data class Goal (
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
  constructor():this(0, "", 0, 0, 0, 0, true, false) {}
}