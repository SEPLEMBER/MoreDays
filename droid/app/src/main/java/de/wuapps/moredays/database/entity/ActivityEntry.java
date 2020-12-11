package de.wuapps.moredays.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;


import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Activity.class,
        parentColumns = "uid",
        childColumns = "activity_id",
        onDelete = CASCADE),
        indices = {@Index(value = {"activity_id"})})
public class ActivityEntry {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "activity_id")
    private int activityId;

    @ColumnInfo(name = "timestamp")
    private Date timestamp;

    public ActivityEntry() {
    }

    @Ignore
    public ActivityEntry(int activityId, Date date) {
       this.activityId = activityId;
       this.timestamp = date;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }
}
