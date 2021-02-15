package de.wuapps.moredays.database.converter

import android.util.Log
import androidx.room.TypeConverter
import de.wuapps.moredays.database.entity.Goal


class ListGoalTypeConverter {

    companion object {

        private const val DELIMITER_GOAL: String = ";"
        private const val DELIMITER_FIELD: String = ","

        @TypeConverter
        fun convertListGoalToString(goals: List<Goal>): String {
            val sb = StringBuilder()
            for (goal in goals) {
                sb.append(goal.uid)
                sb.append(DELIMITER_FIELD)
                sb.append(goal.name)
                sb.append(DELIMITER_FIELD)
                sb.append(goal.color)
                sb.append(DELIMITER_FIELD)
                sb.append(goal.points)
                sb.append(DELIMITER_FIELD)
                sb.append(goal.symbol)
                sb.append(DELIMITER_FIELD)
                sb.append(goal.orderNumber)
                sb.append(DELIMITER_FIELD)
                if (goal.isActive)
                    sb.append(1)
                else
                    sb.append(0)
                sb.append(DELIMITER_GOAL)
            }
            return sb.toString()
        }

        @TypeConverter
        fun convertStringToGoalList(data: String): List<Goal> {
            val goals = ArrayList<Goal>()
            val rows =
                data.split((DELIMITER_GOAL).toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            try {
                for (row in rows) {
                    val fields =
                        row.split((DELIMITER_FIELD).toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val goal = Goal()
                    goal.uid = fields[0].toLong()
                    goal.name = fields[1]
                    goal.color = fields[2].toInt()
                    goal.points = fields[3].toInt()
                    goal.symbol = fields[4].toInt()
                    goal.orderNumber = fields[5].toInt()
                    goal.isActive = fields[6].toInt() > 0
                    goals.add(goal)
                }
            } catch (exception: Exception) {
                Log.e("ListGoalTypeConverter", exception.message.toString())
            }
            return goals
        }

    }
}