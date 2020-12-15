package de.wuapps.moredays.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity
class Goal {
  @PrimaryKey(autoGenerate = true)
  var uid:Int = 0
  var name:String = ""
  var color:Int = 0
  var points:Int = 0
  var symbol:Int = 0
  @ColumnInfo(name = "order_number")
  var orderNumber:Int = 0
  @ColumnInfo(name = "is_active")
  var isActive:Boolean = false
  constructor() {}
  @Ignore
  @JvmOverloads constructor(name:String, color:Int, points:Int, symbol:Int, orderNumber:Int = 100) {
    this.uid = 0 //trigger autoincrement
    this.name = name
    this.color = color
    this.points = points
    this.symbol = symbol
    this.orderNumber = orderNumber
    this.isActive = true
  }
}