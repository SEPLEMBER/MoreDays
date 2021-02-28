package de.wuapps.moredays.database.entity

import android.os.Parcelable
import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.wuapps.moredays.database.converter.DateTypeConverter
import kotlinx.parcelize.Parcelize
import org.jetbrains.annotations.NotNull
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
@Entity
data class Journal(
    @NotNull
    @PrimaryKey
    var date: String,
    var affirmation: String,
    var note: String,
    @ColumnInfo(name = "image_filename")
    var imageFilename: String,
    @ColumnInfo(name = "symbols")
    val icons: MutableList<Int>,
    var color: Int
) : Parcelable {
    constructor() : this(
        SimpleDateFormat(DATE_FORMAT).format(Date()),
        "",
        "",
        "",
        ArrayList<Int>(),
        0
    )

    constructor(date: Date) : this() {
        this.date = SimpleDateFormat(DATE_FORMAT).format(date)
    }

    fun getDateAsCalendar(): Calendar {
        if (date.isEmpty() || date.length != DATE_FORMAT.length) {
            Log.e("Journal", "date is invalid:" + date)
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