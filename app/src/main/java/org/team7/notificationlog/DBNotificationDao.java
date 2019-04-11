package org.team7.notificationlog;


import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface DBNotificationDao {

    @Query("SELECT * FROM notifications WHERE category IN (:validCategories)")
    LiveData<List<DBNotification>> getAllLive(List<String> validCategories);

    @Query("SELECT * FROM notifications WHERE category IN (:validCategories)")
    List<DBNotification> getAll(List<String> validCategories);

    @Query("SELECT * FROM notifications WHERE ongoing = 0 AND noclear = 0 AND category IN (:validCategories)")
    LiveData<List<DBNotification>> getAllNonPersistentLive(List<String> validCategories);

    @Query("SELECT * FROM notifications WHERE ongoing = 0 AND noclear = 0 AND category IN (:validCategories)")
    List<DBNotification> getAllNonPersistent(List<String> validCategories);

    //TODO: consider building exclusion into the query?
    //table with excluded apps? not sure the best way to do that
    @Query("SELECT * FROM notifications WHERE appname NOT IN (:excludedApps) AND category IN (:validCategories)")
    LiveData<List<DBNotification>> getAllFilteredLive(String[] excludedApps, List<String> validCategories);

    @Query("SELECT * FROM notifications WHERE appname NOT IN (:excludedApps) AND category IN (:validCategories)")
    List<DBNotification> getAllFiltered(String[] excludedApps, List<String> validCategories);

    @Insert
    void insert(DBNotification dbn);

    @Insert
    void insertAll(DBNotification... dbns);
}
