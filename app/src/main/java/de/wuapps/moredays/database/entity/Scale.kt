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
    var uid: Long,
    var name: String,
    @ColumnInfo(name = "min_value")
    var minValue: Int,
    @ColumnInfo(name = "max_value")
    var maxValue: Int,
    @ColumnInfo(name = "is_int")
    var isIntValue: Boolean
) : Parcelable {
    constructor() : this(0, "", 0, 100, false)

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
        if (other !is Scale)
            return false
        return other.uid == uid && other.name == name && other.minValue == minValue && other.maxValue == maxValue
    }

    override fun hashCode(): Int {
        var result = uid.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + minValue
        result = 31 * result + maxValue
        return result
    }

    //needed for spinner
    override fun toString(): String {
        return name
    }
}