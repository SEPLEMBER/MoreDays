package de.wuapps.moredays.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.wuapps.moredays.database.converter.DateTypeConverter
import kotlinx.parcelize.Parcelize
import org.jetbrains.annotations.NotNull
import java.util.*

@Parcelize
@Entity
data class Journal(
    @PrimaryKey
    @NotNull
    var date: String = DateTypeConverter.convertDateToString(Date()),
    var affirmation: String = "",
    var note: String = "",
    @ColumnInfo(name = "image_filename")
    var imageFilename: String = "",
    @ColumnInfo(name = "symbols")
    val icons: MutableList<Int> = ArrayList<Int>(),
    var color: Int = 0
) : Parcelable