package de.wuapps.moredays.database.entity

import android.os.Parcelable
import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.wuapps.moredays.database.converter.DateTypeConverter
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


@Parcelize
@Entity
data class HundredPercentValue(
    @PrimaryKey
    var date: String,
    var points: Int
) : Parcelable {
    constructor() : this(
        SimpleDateFormat(Journal.DATE_FORMAT).format(Date()), 0
    )

    constructor(points: Int) : this() {
        this.points = points
    }
    constructor(date: Date) : this() {
        this.date = SimpleDateFormat(DATE_FORMAT).format(date)
    }

    fun getDateAsCalendar(): Calendar {
        if (date.isEmpty() || date.length != DATE_FORMAT.length) {
            Log.e("Journal", "date is invalid:$date")
            return Calendar.getInstance()
        }
        val tmp = DateTypeConverter.convertStringToDate(date)
        val calendar = Calendar.getInstance()
        calendar.time = tmp
        return calendar
    }

    companion object {
        const val DATE_FORMAT = "yyMMdd"
    }
}