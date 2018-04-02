package com.example.mithraw.howwasyourday;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;


@Entity(primaryKeys = {"day_of_the_month", "month", "year"},
        indices = {@Index(value = {"day_of_the_week"}, unique = false)})
public class Day {

    @ColumnInfo(name = "day_of_the_week")
    private int dayOfTheWeek;


    @ColumnInfo(name = "day_of_the_month")
    private int day;


    private int month;

    private int year;
    private int rating;
    private String titleText;
    private String log;


    public Day(int dayOfTheWeek, int day, int month, int year, int rating, String titleText, String log) {
        this.dayOfTheWeek = dayOfTheWeek;
        this.day = day;
        this.month = month;
        this.year = year;
        this.rating = rating;
        this.titleText = titleText;
        this.log = log;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public int getDayOfTheWeek() {
        return dayOfTheWeek;
    }

    public void setDayOfTheWeek(int dayOfTheWeek) {
        this.dayOfTheWeek = dayOfTheWeek;
    }
}
