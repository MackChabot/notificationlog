package org.team7.notificationlog.db;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface StringFilterDao {

    @Query("SELECT COUNT(*) FROM filters WHERE package = :pkg")
    int getNumFiltersForPkg(String pkg);

    @Query("SELECT DISTINCT package FROM filters")
    List<String> getPackagesWithFilters();

    @Query("SELECT * FROM filters")
    List<StringFilter> getAllFilters();

    @Query("SELECT * FROM filters WHERE package = :appPackage")
    LiveData<List<StringFilter>> getFilters(String appPackage);

    @Query("SELECT * FROM filters WHERE package = :appPackage")
    List<StringFilter> getFiltersNonLive(String appPackage);

    @Delete
    void deleteFilter(StringFilter sf);

    @Insert
    void insert(StringFilter sf);

    @Insert
    void insertAll(StringFilter... sf);

}
