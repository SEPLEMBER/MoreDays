package de.wuapps.moredays.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Date;

import de.wuapps.moredays.database.converter.DateTypeConverter;

@Entity
public class Journal implements Serializable {
    @PrimaryKey
    @NotNull
    private String date;

    @ColumnInfo(name = "grateful")
    private String grateful;

    @ColumnInfo(name = "great")
    private String great;

    @ColumnInfo(name = "affirmation")
    private String affirmation;

    @ColumnInfo(name = "reflection")
    private String reflection;

    @ColumnInfo(name = "improvement")
    private String improvement;

    @ColumnInfo(name = "image_filename")
    private String imageFilename;

    @ColumnInfo(name = "mood")
    private int mood;

    @ColumnInfo(name = "mood_note")
    private String moodNote;

    public Journal(){
        this.setDate(DateTypeConverter.convertDateToString(new Date()));
        this.grateful = "";
        this.great = "";
        this.affirmation = "";
        this.reflection = "";
        this.improvement = "";
        this.imageFilename = "";
        this.mood = 0;
        this.moodNote = "";
    }

    @Ignore
    public Journal(String date, String grateful, String great, String affirmation, String reflection, String improvement, String imageFilename){
        this.grateful = grateful;
        this.great = great;
        this.affirmation = affirmation;
        this.reflection = reflection;
        this.improvement = improvement;
        this.imageFilename = imageFilename;
        this.setDate(date);
    }



    public String getGrateful() {
        return grateful;
    }

    public void setGrateful(String grateful) {
        this.grateful = grateful;
    }

    public String getGreat() {
        return great;
    }

    public void setGreat(String great) {
        this.great = great;
    }

    public String getAffirmation() {
        return affirmation;
    }

    public void setAffirmation(String affirmation) {
        this.affirmation = affirmation;
    }

    public String getReflection() {
        return reflection;
    }

    public void setReflection(String reflection) {
        this.reflection = reflection;
    }

    public String getImprovement() {
        return improvement;
    }

    public void setImprovement(String improvement) {
        this.improvement = improvement;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageFilename() {
        return imageFilename;
    }

    public void setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
    }

    public String getSummary(){
        StringBuilder sb = new StringBuilder();
        if (grateful.length() > 0) {
            if (grateful.startsWith("#"))
                sb.append(grateful.substring(1));
            else
                sb.append(grateful);
            sb.append("\n");
        }
        if (great.length() > 0) {
            if (great.startsWith("#"))
                sb.append(great.substring(1));
            else
                sb.append(great);
            sb.append("\n");
        }
        if (affirmation.length() > 0) {
            if (affirmation.startsWith("#"))
                sb.append(affirmation.substring(1));
            else
                sb.append(affirmation);
            sb.append("\n");
        }
        return sb.toString();
    }

    public  String getImportant(){
        if (affirmation.startsWith("#"))
            return affirmation.substring(1);
        if (grateful.startsWith("#"))
            return grateful.substring(1);
        if (great.startsWith("#"))
            return great.substring(1);
        if (affirmation.length() > 0)
            return affirmation;
        if (great.length() > 0)
            return great;
        if (grateful.length() > 0)
            return grateful;
        return "";
    }

    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    public String getMoodNote() {
        return moodNote;
    }

    public void setMoodNote(String moodNote) {
        this.moodNote = moodNote;
    }
}
