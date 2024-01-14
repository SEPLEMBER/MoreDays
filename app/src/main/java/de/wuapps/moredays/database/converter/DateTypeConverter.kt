package de.wuapps.moredays.database.converter

import android.util.Log
import androidx.room.TypeConverter
import de.wuapps.moredays.database.entity.Journal
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class DateTypeConverter {
    @TypeConverter
    fun calendarToTimestamp(calendar: Calendar): Long = calendar.timeInMillis

    @TypeConverter
    fun timestampToCalendar(value: Long): Calendar =
        Calendar.getInstance().apply { timeInMillis = value }

    companion object {

        @TypeConverter
        fun convertDateToString(date: Date, format: String = "yyMMdd"): String {
            return SimpleDateFormat(format).format(date)
        }

        @TypeConverter
        fun convertStringToDate(strDate: String, format: String = "yyMMdd"): Date {
            var date = Date()
            var adaptableFormat = format
            if (strDate.length != format.length){
                if (strDate.length == Journal.DATE_FORMAT.length)
                    adaptableFormat = Journal.DATE_FORMAT
                else if (strDate.length == Journal.DATE_FORMAT_MINUTE.length)
                    adaptableFormat = Journal.DATE_FORMAT_MINUTE
                else if (strDate.length == Journal.DATE_FORMAT_HOUR.length)
                    adaptableFormat = Journal.DATE_FORMAT_HOUR
            }
            try {
                (SimpleDateFormat(adaptableFormat).parse(strDate)).also {
                    if (it != null)
                        date = it
                }
            } catch (ex: ParseException) {
                //@todo log
                Log.w("DateTypeConverter", "convertStringToDate, ParseException: " + ex.message)
            }
            return date
        }
    }
}
