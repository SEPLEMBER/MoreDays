package de.wuapps.moredays.database.entity

import android.os.Parcelable
import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Goal::class,
            parentColumns = ["uid"],
            childColumns = ["goal_id"],
            onDelete = CASCADE
        )],
    indices = [Index(value = ["goal_id"])]
)
class Activity(
    @PrimaryKey(autoGenerate = true)
    var uid: Long,

    var name: String,

    @ColumnInfo(name = "goal_id")
    var goalId: Long,

    var points: Int,

    @ColumnInfo(name = "is_old") //old = formerly used, should not be able to be selected, but keep associated data and show it in bar chart
    var isOld: Boolean
) : Parcelable {
    constructor() : this(0, "", 0, 1, false)

    @Ignore
    constructor(name: String, goalId: Long, points: Int) : this() {
        this.uid = 0 //trigger autoincrement
        this.name = name
        this.goalId = goalId
        this.points = points
        this.isOld = false
    }

    @Override
    override fun toString(): String {
        return "$name ($points P.)"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null)
            return false
        if (other !is Activity)
            return false
        return other.uid == uid && other.name == name && other.points == points && other.isOld == isOld && other.goalId == goalId
    }

    override fun hashCode(): Int {
        var result = uid.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + goalId.hashCode()
        result = 31 * result + points
        result = 31 * result + isOld.hashCode()
        return result
    }
}