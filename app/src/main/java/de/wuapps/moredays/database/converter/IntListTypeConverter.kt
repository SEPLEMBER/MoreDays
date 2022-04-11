package de.wuapps.moredays.database.converter

import androidx.room.TypeConverter

class IntListTypeConverter {
    private val SEPARATOR = "|"
    @TypeConverter
    fun fromIntList(values: List<Int>): String {
        if (values.isEmpty()) {
            return ""
        }
        return values.joinToString(SEPARATOR)
    }

    @TypeConverter
    fun toIntList(joinedValue: String): List<Int> {
        if (joinedValue.isBlank()) {
            return ArrayList()
        }
        val intList = ArrayList<Int>()
        joinedValue.split(SEPARATOR).map{ intList.add(it.toInt())}
        return intList
    }
}