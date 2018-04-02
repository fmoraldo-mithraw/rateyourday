package com.example.mithraw.howwasyourday.databases;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

@Database(entities = Day.class, version = 2)
public abstract class DaysDatabase extends RoomDatabase {
    public abstract DayDao dayDao();
    private static DaysDatabase ourInstance =  null;

    public static DaysDatabase getInstance(Context ctx) {
        if(ourInstance == null) {
            ourInstance = Room.databaseBuilder(ctx,
                    DaysDatabase.class, "database-name").addMigrations(MIGRATION_1_2).build();
        }
        return ourInstance;
    }
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE INDEX index_Day_day_of_the_week on day (day_of_the_week);");
        }
    };
    // WARNING, has to be public for the self generation, but you must use the getInstance.
    public DaysDatabase() {
    }
}
