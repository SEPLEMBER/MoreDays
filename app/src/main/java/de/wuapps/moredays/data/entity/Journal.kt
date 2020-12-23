package de.wuapps.moredays.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import de.wuapps.moredays.database.converter.DateTypeConverter
import org.jetbrains.annotations.NotNull
import java.util.*
@Entity
class Journal
{
  @PrimaryKey
  @NotNull
  var date:String=""
  @ColumnInfo(name = "note")
  var affirmation:String=""
  @ColumnInfo(name = "affirmation")
  var note:String=""
  @ColumnInfo(name = "image_filename")
  var imageFilename:String=""
  @ColumnInfo(name = "mood")
  var mood:Int = 0

  constructor() {
    this.date = DateTypeConverter.convertDateToString(Date())
  }
  @Ignore
  constructor(date:String, affirmation:String, imageFilename:String) {
    this.affirmation = affirmation
    this.imageFilename = imageFilename
    this.date = date
  }
}