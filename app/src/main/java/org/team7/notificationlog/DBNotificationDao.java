package org.team7.notificationlog;


import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface DBNotificationDao {

    @Query("SELECT * FROM notifications")
    LiveData<List<DBNotification>> getAllLive();

    @Query("SELECT * FROM notifications")
    List<DBNotification> getAll();

    @Query("SELECT * FROM notifications WHERE ongoing = 0 AND noclear = 0")
    LiveData<List<DBNotification>> getAllNonPersistentLive();

    @Query("SELECT * FROM notifications WHERE ongoing = 0 AND noclear = 0")
    List<DBNotification> getAllNonPersistent();

    //TODO: consider building exclusion into the query?
    //table with excluded apps? not sure the best way to do that
    @Query("SELECT * FROM notifications WHERE appname NOT IN (:excludedApps)")
    LiveData<List<DBNotification>> getAllFilteredLive(String[] excludedApps);

    @Query("SELECT * FROM notifications WHERE appname NOT IN (:excludedApps)")
    List<DBNotification> getAllFiltered(String[] excludedApps);

    @Insert
    void insert(DBNotification dbn);

    @Insert
    void insertAll(DBNotification... dbns);
}
