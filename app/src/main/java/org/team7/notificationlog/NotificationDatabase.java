package org.team7.notificationlog;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {DBNotification.class}, version = 2)
public abstract class NotificationDatabase extends RoomDatabase {

    public abstract DBNotificationDao dbNotificationDao();

    private static String DBNAME = "main";

    private static NotificationDatabase INSTANCE;
    public static NotificationDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, NotificationDatabase.class, DBNAME)
                    .addMigrations(Migration_1_2)
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    static final Migration Migration_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE notifications ADD COLUMN ongoing BOOLEAN");
            database.execSQL("ALTER TABLE notifications ADD COLUMN noclear BOOLEAN");
            database.execSQL("UPDATE notifications SET ongoing = 0 WHERE ongoing = NULL");
            database.execSQL("UPDATE notifications SET noclear = 0 WHERE noclear = NULL");
        }
    };


}
