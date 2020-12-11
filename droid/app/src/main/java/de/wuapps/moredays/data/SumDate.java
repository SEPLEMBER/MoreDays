package de.wuapps.moredays.data;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.wuapps.moredays.R;

public class SumDate {
    private int sum;
    private String date;
    private int goalId;

    public SumDate(int sum, String date, int goalId) {
        this.setSum(sum);
        this.setDate(date);
        this.setGoalId(goalId);
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public String getDate() {
        return date;
    }

    public String getLocalizedDate(Context context){
        try {
            SimpleDateFormat simpleDb = new SimpleDateFormat("dd.MM.yy");
            Date dateTime = simpleDb.parse(date);
            SimpleDateFormat mFormat = new SimpleDateFormat(context.getString(R.string.date_format_dmy));
            return mFormat.format(dateTime);
        }
        catch(java.text.ParseException ex){
            return date;
        }
    }
    public void setDate(String date) {
        this.date = date;
    }

    public int getGoalId() {
        return goalId;
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }
}

