package com.mithraw.howwasyourday.databases;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface DayDao {
    @Query("SELECT * FROM day")
    List<Day> getAll();

    @Query("SELECT * FROM day WHERE day_of_the_month IS :dayOfTheMonth AND month IS :month AND year IS :year")
    List<Day> loadAllByDate(int dayOfTheMonth, int month, int year);

    @Query("SELECT * FROM day WHERE day_of_the_week IS :dayOfTheWeek AND year IS :year")
    List<Day> loadAllByDayOfTheWeek(int dayOfTheWeek, int year);

    @Query("SELECT * FROM day WHERE day_of_the_week IS :dayOfTheWeek AND month IS :month AND year IS :year")
    List<Day> loadAllByDayOfTheWeekByMonth(int dayOfTheWeek, int month, int year);

    @Query("SELECT * FROM day WHERE month IS :month AND year IS :year")
    List<Day> loadAllByMonth(int month, int year);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertDay(Day... days);

    @Delete
    void delete(Day day);

    @Update
    void update(Day day);

}
