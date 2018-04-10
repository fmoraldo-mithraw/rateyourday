package com.mithraw.howwasyourday.databases;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;


@Entity(primaryKeys = {"day_of_the_month", "month", "year"},
        indices = {@Index(value = {"day_of_the_week"}, unique = false), @Index(value = {"week", "year"}, unique = false), @Index(value = {"date_time"}, unique = false)})
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
    private long date_time;
    private int week;

    public Day(int dayOfTheWeek, int day, int month, int year, int week, long date_time, int rating, String titleText, String log) {
        this.dayOfTheWeek = dayOfTheWeek;
        this.day = day;
        this.month = month;
        this.year = year;
        this.rating = rating;
        this.titleText = titleText;
        this.log = log;
        this.week = week;
        this.date_time = date_time;
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

    public long getDate_time() {
        return date_time;
    }

    public void setDate_time(long date_time) {
        this.date_time = date_time;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }
}
