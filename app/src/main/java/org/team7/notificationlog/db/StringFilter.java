package org.team7.notificationlog.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "filters")
public class StringFilter {

    public StringFilter() {
    }

    public StringFilter(String appPackage, String target, String type, String filterText) {
        this.appPackage = appPackage;
        this.target = target;
        this.type = type;
        this.filterText = filterText;
    }

    @PrimaryKey(autoGenerate = true)
    public long nid;

    @ColumnInfo(name = "package")
    public String appPackage;

    @ColumnInfo(name = "target")
    public String target;

    @ColumnInfo(name = "type")
    public String type;

    @ColumnInfo(name = "text")
    public String filterText;
}
