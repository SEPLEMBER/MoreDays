package de.wuapps.moredays.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;
import static androidx.room.ForeignKey.NO_ACTION;

@Entity(foreignKeys = @ForeignKey(entity = ActivityEntry.class,
        parentColumns = "uid",
        childColumns = "activity_entry_id",
        onUpdate = NO_ACTION,
        onDelete = CASCADE),
        indices = {@Index(value = {"activity_entry_id"})})
public class Trophy {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    @ColumnInfo(name = "text")
    private String text;
    @ColumnInfo(name = "type")
    private int type;
    @ColumnInfo(name = "goal_id")
    private int goalId;
    @ColumnInfo(name = "timestamp")
    private Date timestamp;

    @ColumnInfo(name = "value")
    private int value;

    @ColumnInfo(name = "activity_entry_id")
    private int activityEntryId;

    public Trophy() {
    }

    @Ignore
    public Trophy(int type, int goalId, int activityEntryId) {
        this.type = type;
        this.goalId = goalId;
        this.timestamp = new Date();
        this.value = -1;
        this.activityEntryId = activityEntryId;
    }

    @Ignore
    public Trophy(String text, int type, int goalId, int value, int activityEntryId) {
        this.timestamp = new Date();
        this.text = text;
        this.type = type;
        this.goalId =goalId;
        this.activityEntryId = activityEntryId;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getGoalId() {
        return goalId;
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getActivityEntryId() {
        return activityEntryId;
    }

    public void setActivityEntryId(int activityEntryId) {
        this.activityEntryId = activityEntryId;
    }
}
