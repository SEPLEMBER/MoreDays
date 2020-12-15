package de.wuapps.moredays.database.converter

import androidx.room.TypeConverter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateTypeConverter {
    @TypeConverter
    fun convertDateToLong(date: Date): Long {
        return date.getTime()
    }

    @TypeConverter
    fun convertLongToDate(time: Long): Date {
        return Date(time)
    }

    companion object {

        @TypeConverter
        fun convertDateToString(date: Date): String {
            return (SimpleDateFormat("yyMMdd").format(date))
        }

        @TypeConverter
        fun convertStringToDate(strDate: String): Date {
            var date = Date()
            try {
                date = (SimpleDateFormat("yyMMdd").parse(strDate))
            } catch (ex: ParseException) {
                //@todo log
            }
            return date
        }
    }
}
