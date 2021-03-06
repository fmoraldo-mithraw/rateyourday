package com.mithraw.howwasyourday.databases;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/*
DayDao, all the methods used to interact with the database
 */
@Dao
public interface DayDao {
    @Query("SELECT * FROM day WHERE is_removed IS NOT 0")
    List<Day> getAllRemoved();

    @Query("SELECT * FROM day")
    List<Day> getAlIncludingRemoved();

    @Query("SELECT * FROM day WHERE is_removed IS 0 ORDER BY year DESC, month DESC, day_of_the_month DESC")
    List<Day> getAll();
    @Query("SELECT * FROM day WHERE rating is 5 AND is_removed IS 0 ORDER BY year DESC, month DESC, day_of_the_month DESC")
    List<Day> getAllGoodMemories();
    @Query("SELECT * FROM day WHERE rating is 4 AND is_removed IS 0 ORDER BY year DESC, month DESC, day_of_the_month DESC")
    List<Day> getAllGoodMemories4();
    @Query("SELECT * FROM day WHERE is_removed IS 0 AND latitude IS NOT 0 AND longitude IS NOT 0 ORDER BY year DESC, month DESC, day_of_the_month DESC LIMIT 1")
    List<Day> getLastOneWithCoordinate();

    @Query("SELECT COUNT(*) FROM day WHERE rating IS :rate AND is_removed IS 0")
    int getNumberOfDayByRate(int rate);
    @Query("SELECT COUNT(*) FROM day WHERE rating IS :rate AND month IS :month AND year is :year AND is_removed IS 0")
    int getNumberOfDayByRateByMonthAndYear(int rate, int month, int year);
    @Query("SELECT COUNT(*) FROM day WHERE rating IS :rate AND year is :year AND is_removed IS 0")
    int getNumberOfDayByRateByYear(int rate, int year);


    @Query("SELECT COUNT(*) FROM day WHERE is_removed IS 0")
    int getNumberOfDays();
    @Query("SELECT COUNT(*) FROM day WHERE month IS :month AND year is :year AND is_removed IS 0")
    int getNumberOfDaysByMonthAndYear(int month, int year);
    @Query("SELECT COUNT(*) FROM day WHERE year is :year AND is_removed IS 0")
    int getNumberOfDaysByYear(int year);


    @Query("SELECT AVG(rating) FROM day WHERE is_removed IS 0")
    float getAverageDay();
    @Query("SELECT AVG(rating) FROM day WHERE month IS :month AND year is :year AND is_removed IS 0")
    float getAverageDayByMonthAndYear(int month, int year);
    @Query("SELECT AVG(rating) FROM day WHERE year is :year AND is_removed IS 0")
    float getAverageDayByYear(int year);


    @Query("SELECT * FROM day WHERE day_of_the_month IS :dayOfTheMonth AND month IS :month AND year IS :year AND is_removed IS 0 ORDER BY year DESC, month DESC, day_of_the_month DESC")
    List<Day> getAllByDate(int dayOfTheMonth, int month, int year);

    @Query("SELECT AVG(rating) FROM day WHERE day_of_the_week IS :dayOfTheWeek AND year IS :year AND is_removed IS 0 ")
    float getAverageRatingPerDayOfTheWeekAndYear(int dayOfTheWeek, int year);

    @Query("SELECT AVG(rating) FROM day WHERE day_of_the_week IS :dayOfTheWeek AND year IS :year AND month IS :month AND is_removed IS 0 ")
    float getAverageRatingPerDayOfTheWeekByMonthAndYear(int dayOfTheWeek,int month, int year);

    @Query("SELECT * FROM day WHERE is_removed IS 0 AND (titleText LIKE '%' ||:queryText || '%' OR log LIKE '%' || :queryText || '%') ORDER BY year DESC, month DESC, day_of_the_month DESC")
    List<Day> search(String queryText);

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

    @Query("SELECT * FROM day WHERE date_time > :startDateInt AND date_time < :endDateInt  AND is_removed IS 0 ORDER BY year DESC, month DESC, day_of_the_month DESC")
    List<Day> getByBounds(long startDateInt, long endDateInt);

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
    void insertDayArray(List<Day> days);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertDay(Day... days);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insertDayNoForce(Day... days);

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insertDayNoForceArray(List<Day> days);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void remove(Day day);

    @Delete
    void delete(Day day);

    @Update
    void update(Day day);

    @Query("SELECT * FROM day WHERE day_of_the_month IS :dayOfTheMonth AND month IS :month AND year IS :year AND is_removed IS 0 LIMIT 1")
    List<Day> getDay(int dayOfTheMonth, int month, int year);

    @Query("SELECT * FROM day WHERE date_time > :startDateInt AND date_time < :endDateInt AND day_of_the_week IN (:idsDays) AND rating IN (:idsRate) AND latitude IS NOT 0 AND longitude IS NOT 0 AND is_removed IS 0 ORDER BY year DESC, month DESC, day_of_the_month DESC")
    List<Day> getByBoundsAndDaysAndRatingNoBadLocations(long startDateInt, long endDateInt, int[] idsDays, int[] idsRate);


}
