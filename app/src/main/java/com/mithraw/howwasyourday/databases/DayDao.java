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
    @Query("SELECT * FROM day WHERE is_removed IS NOT 0")
    List<Day> getAllRemoved();
    @Query("SELECT * FROM day")
    List<Day> getAlIncludingRemoved();

    @Query("SELECT * FROM day WHERE is_removed IS 0 ORDER BY year DESC, month DESC, day_of_the_month DESC")
    List<Day> getAll();

    @Query("SELECT * FROM day WHERE day_of_the_month IS :dayOfTheMonth AND month IS :month AND year IS :year AND is_removed IS 0 ORDER BY year DESC, month DESC, day_of_the_month DESC")
    List<Day> getAllByDate(int dayOfTheMonth, int month, int year);

    @Query("SELECT AVG(rating) FROM day WHERE day_of_the_week IS :dayOfTheWeek AND year IS :year AND is_removed IS 0 ")
    float getAverageRatingPerDayOfTheWeekAndYear(int dayOfTheWeek, int year);

    @Query("SELECT AVG(rating) FROM day WHERE day_of_the_week IS :dayOfTheWeek AND is_removed IS 0 ")
    float getAverageRatingPerDayOfTheWeek(int dayOfTheWeek);

    @Query("SELECT AVG(rating) FROM day WHERE month IS :month AND is_removed IS 0 ")
    float getAverageRatingPerMonth(int month);

    @Query("SELECT AVG(rating) FROM day WHERE month IS :month AND year IS :year AND is_removed IS 0 ")
    float getAverageRatingPerMonthAndYear(int month, int year);

    @Query("SELECT * FROM day WHERE week IS :week AND year IS :year AND is_removed IS 0 ")
    List<Day> getDaysPerWeekAndYear(int week, int year);

    @Query("SELECT * FROM day WHERE date_time > :startDateInt AND date_time < :endDateInt AND day_of_the_week IN (:ids)  AND is_removed IS 0 ORDER BY year DESC, month DESC, day_of_the_month DESC")
    List<Day> getByBoundsAndAdditions(long startDateInt, long endDateInt, int[] ids);

    @Query("SELECT AVG(rating) FROM day WHERE date_time > :startDateInt AND date_time < :endDateInt AND day_of_the_week IS :id AND is_removed IS 0 ")
    float getAverageRatingByBoundsAndDayOfTheMonth(long startDateInt, long endDateInt, int id);

    @Query("SELECT AVG(rating) FROM day WHERE year IS :year AND is_removed IS 0 ")
    float getAverageRatingPerYear(int year);

    @Query("SELECT * FROM day  WHERE is_removed IS 0 GROUP BY year")
    List<Day> getYearsRated();

    @Query("SELECT AVG(rating) FROM day WHERE month IS :month AND is_removed IS 0 ")
    float getAverageRatingPerMonthAllTime(int month);

    @Query("SELECT AVG(rating) FROM day WHERE day_of_the_week IS :day_of_the_week AND month IS :month AND year IS :year AND is_removed IS 0 ")
    float getAverageRatingPerDayOfTheWeekPerMonthAndYear(int day_of_the_week,int month, int year);

    @Query("SELECT AVG(rating) FROM day WHERE day_of_the_week IS :dayOfTheWeek AND is_removed IS 0 ")
    float getAverageRatingPerDayOfTheWeekAllTime(int dayOfTheWeek);


    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertDay(Day... days);

    @Insert (onConflict = OnConflictStrategy.FAIL)
    void insertDayNoForce(Day... days);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void remove(Day day);

    @Delete
    void delete(Day day);

    @Update
    void update(Day day);
    @Query("SELECT * FROM day WHERE day_of_the_month IS :dayOfTheMonth AND month IS :month AND year IS :year LIMIT 1")
    List<Day> getDay(int dayOfTheMonth, int month, int year);

}
