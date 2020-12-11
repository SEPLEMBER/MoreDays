package de.wuapps.moredays.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Goal.class,
        parentColumns = "uid",
        childColumns = "goal_id",
        onDelete = CASCADE),
        indices = {@Index(value = {"goal_id"})})
public class Activity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "goal_id")
    private int goalId;

    @ColumnInfo(name = "points")
    private int points;

    @ColumnInfo(name = "is_active")
    private boolean isActive;

    @Ignore
    public Activity(String name, int goalId, int points) {
        this.setUid(0); //trigger autoincrement
        this.setName(name);
        this.setGoalId(goalId);
        this.setPoints(points);
        this.setActive(true);
    }

    public Activity() {
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

//    public int getIcon() {
//        return icon;
//    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return String.format("id: %d, name: %s, points: %s", uid, name, points);
    }

    public int getGoalId() {
        return goalId;
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
