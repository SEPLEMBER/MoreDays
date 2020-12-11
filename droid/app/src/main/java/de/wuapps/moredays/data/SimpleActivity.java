package de.wuapps.moredays.data;

import java.util.Date;

public class SimpleActivity {
    public Date date;
    public String name;
    public int uid;
    public int goalId;
    public int points;

    public SimpleActivity(Date date, String name, int uid, int goalId, int points) {
        this.date = date;
        this.name = name;
        this.uid = uid;
        this.goalId = goalId;
        this.points = points;
    }
}
