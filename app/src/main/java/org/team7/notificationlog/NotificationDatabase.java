package org.team7.notificationlog;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {DBNotification.class}, version = 1)
public abstract class NotificationDatabase extends RoomDatabase {

    public abstract DBNotificationDao dbNotificationDao();

    private static String DBNAME = "main";

    private static NotificationDatabase INSTANCE;
    public static NotificationDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, NotificationDatabase.class, DBNAME).build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
