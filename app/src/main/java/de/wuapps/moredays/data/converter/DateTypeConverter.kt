package de.wuapps.moredays.database.converter

import androidx.room.TypeConverter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateTypeConverter {
    @TypeConverter
    fun calendarToDatestamp(calendar: Calendar): Long = calendar.timeInMillis

    companion object {

        @TypeConverter
        fun convertDateToString(date: Date): String {
            return (SimpleDateFormat("yyMMdd").format(date))
        }

        @TypeConverter
        fun convertStringToDate(strDate: String): Date {
            var date = Date()
            try {
                (SimpleDateFormat("yyMMdd").parse(strDate)).also { if (it != null) date = it }
            } catch (ex: ParseException) {
                //@todo log
            }
            return date
        }
    }

    @TypeConverter
    fun datestampToCalendar(value: Long): Calendar =
        Calendar.getInstance().apply { timeInMillis = value }
}
