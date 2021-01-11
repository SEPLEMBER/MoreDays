package de.wuapps.moredays.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
class Scale(
    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0L,
    var name: String = "",
    @ColumnInfo(name = "min_value")
    var minValue: Int = 0,
    @ColumnInfo(name = "max_value")
    var maxValue: Int = 100
) : Parcelable {
    constructor() : this(0, "", 0, 100)

    @Ignore
    constructor(name: String) : this() {
        this.uid = 0 //trigger autoincrement
        this.name = name
    }

    @Ignore
    constructor(name: String, minValue: Int, maxValue: Int) : this() {
        this.uid = 0 //trigger autoincrement
        this.name = name
        this.minValue = minValue
        this.maxValue = maxValue
    }

    override fun equals(other: Any?): Boolean {
        if (other == null)
            return false
        if (!(other is Scale))
            return false
        val otherScale = other as Scale
        return otherScale.uid == uid && otherScale.name.equals(name) && otherScale.minValue == minValue && otherScale.maxValue == maxValue
    }
}