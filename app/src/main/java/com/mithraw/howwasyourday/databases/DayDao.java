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
    @Query("SELECT * FROM day ORDER BY year DESC, month DESC, day_of_the_month DESC")
    List<Day> getAll();

    @Query("SELECT * FROM day WHERE day_of_the_month IS :dayOfTheMonth AND month IS :month AND year IS :year")
    List<Day> loadAllByDate(int dayOfTheMonth, int month, int year);

    @Query("SELECT * FROM day WHERE day_of_the_week IS :dayOfTheWeek AND year IS :year")
    List<Day> loadAllByDayOfTheWeek(int dayOfTheWeek, int year);

    @Query("SELECT * FROM day WHERE day_of_the_week IS :dayOfTheWeek AND month IS :month AND year IS :year")
    List<Day> loadAllByDayOfTheWeekByMonth(int dayOfTheWeek, int month, int year);

    @Query("SELECT AVG(rating) FROM day WHERE day_of_the_week IS :dayOfTheWeek AND year IS :year")
    float getAverageRatingPerDayOfTheWeekAndYear(int dayOfTheWeek, int year);

    @Query("SELECT AVG(rating) FROM day WHERE day_of_the_week IS :dayOfTheWeek")
    float getAverageRatingPerDayOfTheWeek(int dayOfTheWeek);

    @Query("SELECT AVG(rating) FROM day WHERE month IS :month")
    float getAverageRatingPerMonth(int month);

    @Query("SELECT AVG(rating) FROM day WHERE month IS :month AND year IS :year")
    float getAverageRatingPerMonthAndYear(int month, int year);

    @Query("SELECT AVG(rating) FROM day WHERE day_of_the_week IS :day_of_the_week AND month IS :month AND year IS :year")
    float getAverageRatingPerDayOfTheWeekPerMonthAndYear(int day_of_the_week,int month, int year);

    @Query("SELECT AVG(rating) FROM day WHERE day_of_the_week IS :dayOfTheWeek")
    float getAverageRatingPerDayOfTheWeekAllTime(int dayOfTheWeek);

    @Query("SELECT AVG(rating) FROM day WHERE month IS :month")
    float getAverageRatingPerMonthAllTime(int month);

    @Query("SELECT * FROM day GROUP BY year")
    List<Day> getYearsRated();

    @Query("SELECT AVG(rating) FROM day WHERE year IS :year")
    float getAverageRatingPerYear(int year);





    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertDay(Day... days);

    @Delete
    void delete(Day day);

    @Update
    void update(Day day);
    @Query("SELECT * FROM day WHERE day_of_the_month IS :dayOfTheMonth AND month IS :month AND year IS :year LIMIT 1")
    List<Day> getDay(int dayOfTheMonth, int month, int year);

}
