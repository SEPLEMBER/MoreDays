package de.wuapps.moredays.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity
class Scale {
  @PrimaryKey(autoGenerate = true)
  var uid:Long = 0

  var name:String= ""

  constructor(){}
  @Ignore
  constructor(name:String) {
    this.uid = 0 //trigger autoincrement
    this.name = name
  }
}