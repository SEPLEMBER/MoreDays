package de.wuapps.moredays.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Scale.class,
        parentColumns = "uid",
        childColumns = "scale_id",
        onDelete = CASCADE),
        indices = {@Index(value = {"scale_id"})})
public class ScaleEntry {
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "scale_id")
    private int scaleId;

    @ColumnInfo(name = "timestamp")
    private Date timestamp;

    @ColumnInfo(name = "value")
    private float value;

    public ScaleEntry() {
    }

    @Ignore
    public ScaleEntry(int scaleId, float value) {
        this.setScaleId(scaleId);
        this.setValue(value);
        this.setTimestamp(new Date());
    }

    @Ignore
    public ScaleEntry(int scaleId, float value, Date date) {
        this.setScaleId(scaleId);
        this.setValue(value);
        this.setTimestamp(date);
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getScaleId() {
        return scaleId;
    }

    public void setScaleId(int scaleId) {
        this.scaleId = scaleId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
