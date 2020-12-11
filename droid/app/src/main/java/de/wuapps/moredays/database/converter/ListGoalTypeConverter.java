package de.wuapps.moredays.database.converter;

import android.util.Log;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.wuapps.moredays.database.entity.Goal;


public class ListGoalTypeConverter {
    private final String DELIMITER_GOAL=";";
    private final String DELIMITER_FIELD=",";
    @TypeConverter
    public String convertListGoalToString(List<Goal> goals) {
        StringBuilder sb = new StringBuilder();
        for (Goal goal : goals){
            sb.append(goal.getUid());
            sb.append(DELIMITER_FIELD);
            sb.append(goal.getName());
            sb.append(DELIMITER_FIELD);
            sb.append(goal.getColor());
            sb.append(DELIMITER_FIELD);
            sb.append(goal.getPoints());
            sb.append(DELIMITER_FIELD);
            sb.append(goal.getSymbol());
            sb.append(DELIMITER_FIELD);
            sb.append(goal.getOrderNumber());
            sb.append(DELIMITER_FIELD);
            if (goal.isActive())
                sb.append(1);
            else
                sb.append(0);
            sb.append(DELIMITER_GOAL);
        }
        return sb.toString();
    }

    @TypeConverter
    public List<Goal> convertStringToGoalList(String data) {
        List<Goal> goals = new ArrayList<Goal>();
        String[] rows = data.split(DELIMITER_GOAL);
        try {
            for (String row : rows) {
                String[] fields = row.split(DELIMITER_FIELD);
                Goal goal = new Goal();
                goal.setUid(Integer.parseInt(fields[0]));
                goal.setName(fields[1]);
                goal.setColor(Integer.parseInt(fields[2]));
                goal.setPoints(Integer.parseInt(fields[3]));
                goal.setSymbol(Integer.parseInt(fields[4]));
                goal.setOrderNumber(Integer.parseInt(fields[5]));
                goal.setActive(Integer.parseInt(fields[6]) > 0);
                goals.add(goal);
            }
        }
        catch (Exception exception){
            Log.e("ListGoalTypeConverter",exception.getMessage());
        }
        return goals;
    }
}

