package org.team7.notificationlog;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notifications")
public class DBNotification {

    public DBNotification(String notifPackage, String strTimestamp, String appName, String title, String text, String category, boolean ongoing, boolean noClear) {
        this.notifPackage = notifPackage;
        this.strTimestamp = strTimestamp;
        this.appName = appName;
        this.title = title;
        this.text = text;
        this.category = (category == null) ? "noncategorized" : category;
        this.ongoing = ongoing;
        this.noClear = noClear;
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

    @ColumnInfo(name = "category")
    public String category;

    @ColumnInfo(name = "ongoing")
    public boolean ongoing;

    @ColumnInfo(name = "noclear")
    public boolean noClear;

    @Override
    public String toString() {

        return  "nid: "             + nid           + "\n" +
                "notifPackage: "    + notifPackage  + "\n" +
                "timestamp: "       + strTimestamp  + "\n" +
                "appName: "         + appName       + "\n" +
                "title: "           + title         + "\n" +
                "text: "            + text          + "\n" +
                "category: "        + category      + "\n" +
                "ongoing: "         + ongoing       + "\n" +
                "noClear: "         + noClear       + "\n";

    }
}
