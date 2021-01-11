package de.wuapps.moredays.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.wuapps.moredays.utilities.DEFAULLT_ICON_ID
import de.wuapps.moredays.utilities.DEFAULT_COLOR
import kotlinx.parcelize.Parcelize


@Entity
@Parcelize
class Goal(
  @PrimaryKey(autoGenerate = true)
  var uid: Long,
  var name: String,
  var color: Int,
  var points: Int,
  var symbol: Int,
  @ColumnInfo(name = "order_number")
  var orderNumber: Int,
  @ColumnInfo(name = "is_active")
  var isActive: Boolean,
  @ColumnInfo(name = "is_old") //old = formerly used, should not be able to be selected, but keep associated data and show it in bar chart
  var isOld: Boolean
) : Parcelable {
    constructor() : this(0, "", DEFAULT_COLOR, 0, DEFAULLT_ICON_ID, 0, true, false)

    //needed for spinner
    override fun toString(): String {
        return "$name ($points P.)"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null)
            return false
        if (other !is Goal)
            return false
        return other.uid == uid && other.color == color && other.points == points && other.isActive == isActive && other.isOld == isOld
    }

    override fun hashCode(): Int {
        var result = uid.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + color
        result = 31 * result + points
        result = 31 * result + symbol
        result = 31 * result + isActive.hashCode()
        result = 31 * result + isOld.hashCode()
        return result
    }
}