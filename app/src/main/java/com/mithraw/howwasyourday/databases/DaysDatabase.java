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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/*
Manage the db
Two handlers, the backup db to save the db safely and the regular one
 */
@Database(entities = Day.class, version = 5, exportSchema = false)
public abstract class DaysDatabase extends RoomDatabase {
    public abstract DayDao dayDao();

    private static DaysDatabase ourInstance = null;
    private static DaysDatabase backupInstance = null;
    private static DaysDatabase importInstance = null;
    private static final String databaseName = "database-name";



    private static final String databaseBackupName = "database-backup";
    private static final String databaseImportName = "database-import";
    private static Context mCtx;

    public static String getDatabaseBackupName() {
        return databaseBackupName;
    }

    public static String getDatabaseImportName() {
        return databaseImportName;
    }

    public static String getDatabaseName() {
        return databaseName;
    }

    public static DaysDatabase getInstance(Context ctx) {
        if (ourInstance == null) {
            mCtx = ctx;
            ourInstance = Room.databaseBuilder(ctx,
                    DaysDatabase.class, databaseName).addMigrations(MIGRATION_1_2).addMigrations(MIGRATION_2_3).addMigrations(MIGRATION_3_4).addMigrations(MIGRATION_4_5).build();
        }
        return ourInstance;
    }
    public static void cleanDatabase(DaysDatabase database){
        List<Day> ds = database.dayDao().getAllRemoved();
        database.beginTransaction();
        for (Day d: ds) {
            database.dayDao().delete(d);
        }
        database.endTransaction();
    }
    public static void copyBackupToDatabase(Context ctx){
        DaysDatabase backup = getBackupNewInstance(ctx);
        List<Day> ds = backup.dayDao().getAll();
        Logger.getLogger("DaysDatabase").log(new LogRecord(Level.INFO, "FMORALDO : copyBackupToDatabase : count "+ds.size()));
        DaysDatabase db = getInstance(ctx);
        db.beginTransaction();
        db.dayDao().insertDayNoForceArray(ds);
        db.setTransactionSuccessful();
        db.endTransaction();
        Logger.getLogger("DaysDatabase").log(new LogRecord(Level.INFO, "FMORALDO : copyBackupToDatabase : End"));
    }
    public static int copyImportToDatabase(Context ctx){
        DaysDatabase backup = getImportInstance(ctx);
        List<Day> ds = backup.dayDao().getAll();
        Logger.getLogger("DaysDatabase").log(new LogRecord(Level.INFO, "FMORALDO : copyBackupToDatabase : count "+ds.size()));
        DaysDatabase db = getInstance(ctx);
        db.beginTransaction();
        db.dayDao().insertDayNoForceArray(ds);
        db.setTransactionSuccessful();
        db.endTransaction();
        Logger.getLogger("DaysDatabase").log(new LogRecord(Level.INFO, "FMORALDO : copyBackupToDatabase : End"));
        return ds.size();
    }

    public static void copyDatabaseToBackup(Context ctx){
        DaysDatabase backup = getBackupNewInstance(ctx);
        List<Day> ds = getInstance(ctx).dayDao().getAlIncludingRemoved();
        Logger.getLogger("DaysDatabase").log(new LogRecord(Level.INFO, "FMORALDO : copyDatabaseToBackup : count " + ds.size()));
        backup.beginTransaction();
        Logger.getLogger("DaysDatabase").log(new LogRecord(Level.INFO, "FMORALDO : copyDatabaseToBackup : insert"));
        backup.dayDao().insertDayArray(getInstance(ctx).dayDao().getAlIncludingRemoved());
        backup.setTransactionSuccessful();
        backup.endTransaction();
        Logger.getLogger("DaysDatabase").log(new LogRecord(Level.INFO, "FMORALDO : copyDatabaseToBackup : end"));
    }
    public static DaysDatabase getBackupInstance(Context ctx) {
        if (backupInstance == null) {
            mCtx = ctx;
            backupInstance = Room.databaseBuilder(ctx,
                    DaysDatabase.class, databaseBackupName).addMigrations(MIGRATION_1_2).addMigrations(MIGRATION_2_3).addMigrations(MIGRATION_3_4).addMigrations(MIGRATION_4_5).build();
        }
        return backupInstance;
    }
    public static DaysDatabase getImportInstance(Context ctx) {
        if (importInstance == null) {
            mCtx = ctx;
            importInstance = Room.databaseBuilder(ctx,
                    DaysDatabase.class, databaseImportName).addMigrations(MIGRATION_1_2).addMigrations(MIGRATION_2_3).addMigrations(MIGRATION_3_4).addMigrations(MIGRATION_4_5).build();
        }
        return importInstance;
    }
    public static DaysDatabase getBackupNewInstance(Context ctx) {
        if (backupInstance != null)
            backupInstance.close();
        backupInstance = Room.databaseBuilder(ctx,
                DaysDatabase.class, databaseBackupName).addMigrations(MIGRATION_1_2).addMigrations(MIGRATION_2_3).addMigrations(MIGRATION_3_4).addMigrations(MIGRATION_4_5).build();
        return backupInstance;
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
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE day ADD is_removed INTEGER DEFAULT 0 NOT NULL;");
            database.execSQL("CREATE INDEX index_Day_is_removed on day (is_removed);");
        }
    };
    static final Migration MIGRATION_4_5 = new Migration(4,5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE day ADD longitude REAL  DEFAULT 0 NOT NULL;");
            database.execSQL("ALTER TABLE day ADD latitude REAL  DEFAULT 0 NOT NULL;");
            database.execSQL("CREATE INDEX index_Day_latitude_longitude on day (latitude,longitude);");
        }
    };

    // WARNING, has to be public for the self generation, but you must use the getInstance.
    public DaysDatabase() {
    }
}
