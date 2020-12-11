package de.wuapps.moredays.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;


@Entity
public class Scale implements Serializable { //serializable: damit kann es als Parameter zwischen Intents übergeben werden
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "name")
    private String name;

    public Scale() {
    }

    @Ignore
    public Scale(String name) {
        this.setUid(0); //trigger autoincrement
        this.setName(name);
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

    public void setName(String name) {
        this.name = name;
    }
}
