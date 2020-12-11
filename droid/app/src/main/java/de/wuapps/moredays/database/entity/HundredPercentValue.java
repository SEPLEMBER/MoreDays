package de.wuapps.moredays.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.wuapps.moredays.database.converter.ListGoalTypeConverter;


@Entity
public class HundredPercentValue implements Serializable {
    @PrimaryKey
    @NotNull
    private String date;

    @NotNull
    @ColumnInfo(name = "points")
    private int points;

    @NotNull
    @ColumnInfo(name = "activeGoals")
    private String activeGoals;

    @Ignore
    public HundredPercentValue(String date, int points, List<Goal> activeGoals) {
        this.setDate(date);
        this.setPoints(points);
        this.setActiveGoals((new ListGoalTypeConverter()).convertListGoalToString(activeGoals));
    }

    public  HundredPercentValue(){
        this.setDate((new SimpleDateFormat("yyMMdd").format(new Date())));
        this.setPoints(0);
        this.setActiveGoals("");
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getActiveGoals() {
        return activeGoals;
    }

    public void setActiveGoals(String activeGoals) {
        this.activeGoals = activeGoals;
    }

    public void setActiveGoals(List<Goal> activeGoals){
        this.activeGoals = (new ListGoalTypeConverter()).convertListGoalToString(activeGoals);
    }
    public List<Goal> getActiveGoalsAsList(){
        return (new ListGoalTypeConverter()).convertStringToGoalList(activeGoals);
    }
}
