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
    var imageFilename: String
) : Parcelable {
    constructor() : this(
        SimpleDateFormat(DATE_FORMAT).format(Date()),
        "",
        "",
        ""
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

    override fun equals(other: Any?): Boolean {
        if (other == null)
            return false
        if (other !is Journal)
            return false
        return other.date == date && other.note == note && other.affirmation == affirmation && other.imageFilename == imageFilename
    }

    override fun hashCode(): Int {
        var result = date.hashCode()
        result = 31 * result + affirmation.hashCode()
        result = 31 * result + note.hashCode()
        result = 31 * result + imageFilename.hashCode()
        return result
    }

    companion object {
        const val DATE_FORMAT = "yyMMdd"
    }
}