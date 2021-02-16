package de.wuapps.moredays.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import de.wuapps.moredays.utilities.DEFAULLT_ICON_ID
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity
@Parcelize
class Challenge(
    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0,
    var title: String = "",
    var deadline: Calendar = Calendar.getInstance(),
    @ColumnInfo(name = "challenge_type")
    var challengeType: Int = CHALLENGE_TYPE_GOAL,
    @ColumnInfo(name = "value_type")
    var valueType: Int = CHALLENGE_VALUE_TIMES,
    @ColumnInfo(name = "desired_value")
    var desiredValue: Int = 1,
    @ColumnInfo(name = "current_value")
    var currentValue: Int = 0,
    @ColumnInfo(name = "start_value")
    var startValue: Int = 0,
    @ColumnInfo(name = "id") //goalid|activityid|scaleid
    var id: Long = 0,
    @ColumnInfo(name = "symbol")
    var symbol: Int = DEFAULLT_ICON_ID,
    @ColumnInfo(name = "status")
    var status:Int = CHALLENGE_NOT_STARTED,
    @ColumnInfo(name = "start_date")
    var startDate: Calendar = Calendar.getInstance()):Parcelable{

    companion object {
        var CHALLENGE_TYPE_SCALE = 0
        var CHALLENGE_TYPE_ACTIVITY = 1
        var CHALLENGE_TYPE_GOAL = 2
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