package de.wuapps.moredays.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import androidx.room.PrimaryKey;

import java.io.Serializable;


@Entity
public class Goal implements Serializable { //serializable: damit kann es als Parameter zwischen Intents übergeben werden
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "color")
    private int color;

    @ColumnInfo(name = "points")
    private int points;

    @ColumnInfo(name = "symbol")
    private int symbol;

    @ColumnInfo(name = "order_number")
    private int orderNumber;

    @ColumnInfo(name = "is_active")
    private boolean isActive;

    public Goal() {
    }

    @Ignore
    public Goal(String name, int color, int points, int symbol, int orderNumber) {
        this.setUid(0); //trigger autoincrement
        this.setName(name);
        this.setColor(color);
        this.setPoints(points);
        this.setSymbol(symbol);
        this.setOrderNumber(orderNumber);
        this.setActive(true);
    }

    @Ignore
    public Goal(String name, int color, int points, int symbol) {
        this(name, color, points, symbol, 100);
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getSymbol() {
        return symbol;
    }

    public void setSymbol(int symbol) {
        this.symbol = symbol;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

}


