package org.team7.notificationlog.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "parcels")
public class PersistentParcel {

    @PrimaryKey(autoGenerate = true)
    public int pid;

    @ColumnInfo(name = "package")
    public String packageName;

    @ColumnInfo(name = "timestamp")
    public String timestamp;

    @ColumnInfo(name = "expiresTimestamp")
    public String expiresTimestamp;

    @ColumnInfo(name = "data", typeAffinity = ColumnInfo.BLOB)
    public byte[] data;
}
