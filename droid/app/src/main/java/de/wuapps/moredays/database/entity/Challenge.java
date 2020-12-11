package de.wuapps.moredays.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Entity
public class Challenge implements Serializable { //serializable: damit kann es als Parameter zwischen Intents übergeben werden
    public static int CHALLENGE_TYPE_SCALE = 0;
    public static int CHALLENGE_TYPE_ENTRY_TYPE = 1;
    public static int CHALLENGE_TYPE_CATEGORY = 2;
    public static int CHALLENGE_VALUE_TYPE_LESS = 0;
    public static int CHALLENGE_VALUE_TYPE_MORE = 1;
    public static int CHALLENGE_VALUE_TIMES = 2;
    public static int CHALLENGE_VALUE_POINTS = 3;
    public static int CHALLENGE_STATUS_UNDEFINED = -2;
    public static int CHALLENGE_NOT_STARTED = -1;
    public static int CHALLENGE_RUNNING = 0;
    public static int CHALLENGE_FAILED = 1;
    public static int CHALLENGE_SUCCEEDED = 2;

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "deadline")
    private Date deadline;

    @ColumnInfo(name = "challenge_type")
    private int challengeType;

    @ColumnInfo(name = "value_type")
    private int valueType;

    @ColumnInfo(name = "desired_value")
    private float desiredValue;

    @ColumnInfo(name = "current_value")
    private float currentValue;

    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "symbol")
    private int symbol;

    @ColumnInfo(name = "status")
    private int status = CHALLENGE_STATUS_UNDEFINED;

    @ColumnInfo(name = "start_date")
    private Date startDate;

    public Challenge() {
    }

    @Ignore
    public Challenge(String title, Date startDate, Date deadline, int challengeType, int valueType, float desiredValue, int id, int symbol) {
        this.setUid(0); //trigger autoincrement
        this.setTitle(title);
        this.setDeadline(deadline);
        this.setChallengeType(challengeType);
        this.setValueType(valueType);
        this.setDesiredValue(desiredValue);
        this.setSymbol(symbol);
        this.setId(id);
        this.setStartDate(startDate);
        this.setCurrentValue(0);
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 1);
        this.startDate = calendar.getTime();
        Date today = new Date();
        if ( deadline != null && (deadline.getTime() < startDate.getTime()))
            this.setStatus(CHALLENGE_FAILED);
        else if (status < 0) {
            if (startDate.getTime() <= today.getTime())
                status = CHALLENGE_RUNNING;
            else
                status = CHALLENGE_NOT_STARTED;
        }
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(deadline);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        this.deadline = calendar.getTime();
    }

    public int getChallengeType() {
        return challengeType;
    }

    public void setChallengeType(int challengeType) {
        this.challengeType = challengeType;
    }

    public int getValueType() {
        return valueType;
    }

    public void setValueType(int valueType) {
        this.valueType = valueType;
    }

    public float getDesiredValue() {
        return desiredValue;
    }

    public void setDesiredValue(float desiredValue) {
        this.desiredValue = desiredValue;
    }

    public float getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(float currentValue) {
        this.currentValue = currentValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSymbol() {
        return symbol;
    }

    public void setSymbol(int symbol) {
        this.symbol = symbol;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
