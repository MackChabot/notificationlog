package org.team7.notificationlog.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {DBNotification.class, StringFilter.class}, version = 3)
public abstract class NotificationDatabase extends RoomDatabase {

    public abstract DBNotificationDao dbNotificationDao();

    public abstract StringFilterDao sfDao();

    private static String DBNAME = "main";

    private static NotificationDatabase INSTANCE;

    public static NotificationDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, NotificationDatabase.class, DBNAME)
                    .addMigrations(Migration_1_2)
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private static final Migration Migration_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE notifications ADD COLUMN ongoing BOOLEAN");
            database.execSQL("ALTER TABLE notifications ADD COLUMN noclear BOOLEAN");
            database.execSQL("UPDATE notifications SET ongoing = FALSE WHERE ongoing = NULL");
            database.execSQL("UPDATE notifications SET noclear = FALSE WHERE noclear = NULL");
        }
    };


}
