package org.team7.notificationlog.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface PersistentParcelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void savePersistentParcel(PersistentParcel parcel);

    @Query("SELECT * FROM parcels WHERE package = :packageName AND timestamp = :timestamp")
    PersistentParcel getPersistentParcel(String packageName, String timestamp);

    @Transaction
    @Query("DELETE FROM parcels WHERE (expiresTimestamp IS NOT NULL) AND (expiresTimestamp < :currentTime)")
    void deleteAllExpired(Long currentTime);
}
