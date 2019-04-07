package org.team7.notificationlog;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notifications")
public class DBNotification {

    public DBNotification(String notifPackage, String strTimestamp, String appName, String title, String text) {
        this.notifPackage = notifPackage;
        this.strTimestamp = strTimestamp;
        this.appName = appName;
        this.title = title;
        this.text = text;
    }

    @PrimaryKey(autoGenerate = true)
    public int nid;

    @ColumnInfo(name = "package")
    public String notifPackage;

    @ColumnInfo(name = "timestamp")
    public String strTimestamp;

    @ColumnInfo(name = "appname")
    public String appName;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "notiftext")
    public String text;
}
