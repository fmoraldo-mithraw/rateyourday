package com.mithraw.howwasyourday.databases;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

@Database(entities = Day.class, version = 3, exportSchema = false)
public abstract class DaysDatabase extends RoomDatabase {
    public abstract DayDao dayDao();

    private static DaysDatabase ourInstance = null;

    public static DaysDatabase getInstance(Context ctx) {
        if (ourInstance == null) {
            ourInstance = Room.databaseBuilder(ctx,
                    DaysDatabase.class, "database-name").addMigrations(MIGRATION_1_2).addMigrations(MIGRATION_2_3).build();
        }
        return ourInstance;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE INDEX index_Day_day_of_the_week on day (day_of_the_week);");
        }
    };
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            //Add Columns
            database.execSQL("ALTER TABLE day ADD date_time INTEGER DEFAULT 0 NOT NULL;");
            database.execSQL("ALTER TABLE day ADD week INTEGER DEFAULT 0 NOT NULL;");
            Cursor mCursor = database.query("SELECT * FROM day ");
            if (mCursor != null) {
                Logger.getLogger("DaysDatabase").log(new LogRecord(Level.INFO, "FMORALDO : Migration 2 to 3 : mCursor != null"));
                Calendar cal = Calendar.getInstance();
                while (mCursor.moveToNext()) {
                    int day = mCursor.getInt(mCursor.getColumnIndex("day_of_the_month"));
                    int month = mCursor.getInt(mCursor.getColumnIndex("month"));
                    int year = mCursor.getInt(mCursor.getColumnIndex("year"));
                    cal.set(year, month, day, 0, 0, 0);
                    ContentValues cv = new ContentValues();
                    cv.put("date_time", cal.getTimeInMillis());
                    cv.put("week", cal.get(Calendar.WEEK_OF_YEAR));
                    Logger.getLogger("DaysDatabase").log(new LogRecord(Level.INFO, "FMORALDO : Migration 2 to 3 : "+year+"/"+month+"/"+day+" Date_time :"+cal.getTimeInMillis()+" week number:"+cal.get(Calendar.WEEK_OF_YEAR)));
                    database.update("day", 5, cv, "day_of_the_month=" + day + " AND month=" + month + " AND year=" + year, null);
                }
            }
            database.execSQL("CREATE INDEX index_Day_week_year on day (week, year);");
            database.execSQL("CREATE INDEX index_Day_date_time on day (date_time);");
        }
    };

    // WARNING, has to be public for the self generation, but you must use the getInstance.
    public DaysDatabase() {
    }
}
