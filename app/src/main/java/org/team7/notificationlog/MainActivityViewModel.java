package org.team7.notificationlog;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class MainActivityViewModel extends AndroidViewModel {

    LiveData<List<DBNotification>> notifData;

    public MainActivityViewModel(Application application) {
        super(application);
    }

    public LiveData<List<DBNotification>> getNotifications() {
        if (notifData == null)
            updateData();
        return notifData;
    }

    public List<DBNotification> getNotificationsBase() {

        try {
            return new GetAllTask(getApplication().getApplicationContext()).execute().get();
        } catch (ExecutionException | InterruptedException ignore) {
            // We have no option but to do nothing
        }

        return null;
    }

    private void updateData() {
        try {
            notifData = new GetAllLiveTask(getApplication().getApplicationContext()).execute().get();
        } catch (ExecutionException | InterruptedException ignore) {
            // We have no option but to do nothing
        }
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
        return NotificationDatabase.getDatabase(c).dbNotificationDao().getAll();
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
        return NotificationDatabase.getDatabase(c).dbNotificationDao().getAllLive();
    }
}
