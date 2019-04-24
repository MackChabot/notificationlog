package org.team7.notificationlog.main;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.team7.notificationlog.db.DBNotification;
import org.team7.notificationlog.db.NotificationDatabase;
import org.team7.notificationlog.db.StringFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class MainActivityViewModel extends AndroidViewModel {

    LiveData<List<DBNotification>> notifData;

    public MainActivityViewModel(Application application) {
        super(application);
    }

    public LiveData<List<DBNotification>> getNotifications(Context c) {
        if (notifData == null)
            updateData(c);
        return notifData;
    }

    public List<String> getPackages(Context c) {
        try {
            return new GetAllPackagesTask(getApplication().getApplicationContext()).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return new ArrayList<String>();
        }
    }

    public List<DBNotification> getNotificationsBase() {

        try {
            return new GetAllTask(getApplication().getApplicationContext()).execute().get();
        } catch (ExecutionException | InterruptedException ignore) {
            // We have no option but to do nothing
        }

        return null;
    }

    private void updateData(Context c) {
        try {
            boolean trackPersistent = PreferenceManager.getDefaultSharedPreferences(c).getBoolean("trackPersistent", false);

            if (trackPersistent)
                notifData = new GetAllLiveTask(getApplication().getApplicationContext()).execute().get();
            else
                notifData = new GetAllNonPersistentLiveTask(getApplication().getApplicationContext()).execute().get();
        } catch (ExecutionException | InterruptedException ignore) {
            // We have no option but to do nothing
        }
    }

    public static ArrayList<String> getValidCategories(Context c) {
        ArrayList<String> cats = new ArrayList<>();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);

        if (sp.getBoolean("catNoCat", true)) cats.add("noncategorized");
//        cats.add("noncategorized");
        if (sp.getBoolean("catAlarm", true)) cats.add(Notification.CATEGORY_ALARM);
        if (sp.getBoolean("carErr", true)) cats.add(Notification.CATEGORY_ERROR);
        if (sp.getBoolean("catProgress", true)) cats.add(Notification.CATEGORY_PROGRESS);
        if (sp.getBoolean("catService", true)) cats.add(Notification.CATEGORY_SERVICE);
        if (sp.getBoolean("catEvent", true)) cats.add(Notification.CATEGORY_EVENT);
        if (sp.getBoolean("catSys", true)) cats.add(Notification.CATEGORY_SYSTEM);
        if (sp.getBoolean("catEmail", true)) cats.add(Notification.CATEGORY_EMAIL);
        if (sp.getBoolean("catTransport", true)) cats.add(Notification.CATEGORY_TRANSPORT);
        if (sp.getBoolean("catMsg", true)) cats.add(Notification.CATEGORY_MESSAGE);
        // Navigation is hardcoded for compatibility with API 23 (our min API). If not hardcoded, it doesn't build.
        // With it, it just don't work. However, it won't work either way so it's fine
        if (sp.getBoolean("catNavigation", true)) cats.add("navigation");
        if (sp.getBoolean("catCall", true)) cats.add(Notification.CATEGORY_CALL);
        if (sp.getBoolean("catPromo", true)) cats.add(Notification.CATEGORY_PROMO);
        if (sp.getBoolean("catRecommendation", true))
            cats.add(Notification.CATEGORY_RECOMMENDATION);
        if (sp.getBoolean("catReminder", true)) cats.add(Notification.CATEGORY_REMINDER);
        if (sp.getBoolean("catSocial", true)) cats.add(Notification.CATEGORY_SOCIAL);

        return cats;
    }

    public static ArrayList<String> getValidApps(Context c) {
        ArrayList<String> apps = new ArrayList<>();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);

        //get a list of installed apps
        List<PackageInfo> packs = c.getPackageManager().getInstalledPackages(0);
        for (PackageInfo p : packs)
            if (sp.getBoolean(p.packageName, true))
                apps.add(p.packageName);

        return apps;
    }

    public Map<String, List<StringFilter>> constructFilters(Context c) {

        Map<String, List<StringFilter>> filterMap = new HashMap<>();

        List<StringFilter> allFilters = new ArrayList<>();
        try {
            allFilters = new GetAllFiltersTask(c).execute().get();
        } catch (Exception ignore) {
        }

        for (StringFilter sf : allFilters) {

            List<StringFilter> filtersForPackage = filterMap.get(sf.appPackage);

            if (filtersForPackage == null) {
                filtersForPackage = new ArrayList<>();
                filtersForPackage.add(sf);
                filterMap.put(sf.appPackage, filtersForPackage);
            } else {
                filtersForPackage.add(sf);
                filterMap.put(sf.appPackage, filtersForPackage);
            }
        }

        return filterMap;
    }
}

class GetAllFiltersTask extends AsyncTask<Void, Void, List<StringFilter>> {

    // The only way to do this afaik
    @SuppressLint("StaticFieldLeak")
    private Context c;

    public GetAllFiltersTask(Context context) {
        c = context;
    }

    @Override
    protected List<StringFilter> doInBackground(Void... voids) {
        return NotificationDatabase.getDatabase(c).sfDao().getAllFilters();
    }
}


class GetAllTask extends AsyncTask<Void, Void, List<DBNotification>> {

    // The only way to do this afaik
    @SuppressLint("StaticFieldLeak")
    private Context c;

    public GetAllTask(Context context) {
        c = context;
    }

    @Override
    protected List<DBNotification> doInBackground(Void... voids) {
        return NotificationDatabase.getDatabase(c).dbNotificationDao().getAll(MainActivityViewModel.getValidCategories(c), MainActivityViewModel.getValidApps(c));
    }
}

class GetAllLiveTask extends AsyncTask<Void, Void, LiveData<List<DBNotification>>> {

    // The only way to do this afaik
    @SuppressLint("StaticFieldLeak")
    private Context c;

    public GetAllLiveTask(Context context) {
        c = context;
    }

    @Override
    protected LiveData<List<DBNotification>> doInBackground(Void... voids) {
        return NotificationDatabase.getDatabase(c).dbNotificationDao().getAllLive(MainActivityViewModel.getValidCategories(c), MainActivityViewModel.getValidApps(c));
    }
}

class GetAllNonPersistentLiveTask extends AsyncTask<Void, Void, LiveData<List<DBNotification>>> {

    // The only way to do this afaik
    @SuppressLint("StaticFieldLeak")
    private Context c;

    public GetAllNonPersistentLiveTask(Context context) {
        c = context;
    }

    @Override
    protected LiveData<List<DBNotification>> doInBackground(Void... voids) {
        return NotificationDatabase.getDatabase(c).dbNotificationDao().getAllNonPersistentLive(MainActivityViewModel.getValidCategories(c), MainActivityViewModel.getValidApps(c));
    }
}

class GetAllPackagesTask extends AsyncTask<Void, Void, List<String>> {

    // The only way to do this afaik
    @SuppressLint("StaticFieldLeak")
    private Context c;

    public GetAllPackagesTask(Context context) {
        c = context;
    }

    @Override
    protected List<String> doInBackground(Void... voids) {
        return NotificationDatabase.getDatabase(c).dbNotificationDao().getAllPackages();
    }
}
