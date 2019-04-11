package org.team7.notificationlog;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.SpannableString;
import android.util.Log;

import java.util.HashMap;

public class NLService extends NotificationListenerService {
    // true if application is open. False if is just the service that's running
    // TODO probably replace this variable with a broadcast receiver
    protected static boolean application_running = false;

    private String TAG = this.getClass().getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        Log.i(TAG,"**********  onNotificationPosted");
        Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName() + "\tcat: " + sbn.getNotification().category);
        Log.i(TAG, "Running insert task");

        boolean trackPersistent = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("trackPersistent", false);
        boolean isOngoing = checkFlag(sbn.getNotification(), Notification.FLAG_ONGOING_EVENT);
        boolean noClear = checkFlag(sbn.getNotification(), Notification.FLAG_NO_CLEAR);
        boolean validCategory = checkValidCategory(sbn.getNotification());

        if ( validCategory && (trackPersistent || (!isOngoing && !noClear)) )
            new InsertDbTask(getApplicationContext()).execute(getDbn(sbn));
    }

    private DBNotification getDbn(StatusBarNotification sbn) {
        Bundle extra = sbn.getNotification().extras;

        boolean isOngoing = checkFlag(sbn.getNotification(), Notification.FLAG_ONGOING_EVENT);
        boolean noClear = checkFlag(sbn.getNotification(), Notification.FLAG_NO_CLEAR);

        String title = extra.getString("android.title");

        // Title may be null because it's a SpannableString
        if (title == null) {
            try {
                SpannableString spannableTitle = (SpannableString) extra.get("android.title");
                if (spannableTitle != null)
                    title = spannableTitle.toString();
            } catch (ClassCastException ignore) {} // leave title null
        }

        String text = "";

        CharSequence chs = extra.getCharSequence("android.text");
        if (chs != null)
            text = chs.toString();

        long when = sbn.getNotification().when;
        long post = sbn.getPostTime();
        long actual = when != 0 ? when : post;

        return new DBNotification(sbn.getPackageName(),
                Long.toString(actual),
                getAppName(sbn.getPackageName()),
                title,
                text,
                sbn.getNotification().category,
                isOngoing,
                noClear);
    }

    private boolean checkFlag(Notification n, int flag) {
        return (n.flags & flag) == flag;
    }

    private boolean checkValidCategory(Notification n) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        HashMap<String, String> categories = new HashMap<>();

        categories.put(null, "catNoCat");
        categories.put(Notification.CATEGORY_ALARM, "catAlarm");
        categories.put(Notification.CATEGORY_ERROR, "carErr");
        categories.put(Notification.CATEGORY_PROGRESS, "catProgress");
        categories.put(Notification.CATEGORY_SERVICE, "catService");
        categories.put(Notification.CATEGORY_EVENT, "catEvent");
        categories.put(Notification.CATEGORY_SYSTEM, "catSys");
        categories.put(Notification.CATEGORY_EMAIL, "catEmail");
        categories.put(Notification.CATEGORY_TRANSPORT, "catTransport");
        categories.put(Notification.CATEGORY_MESSAGE, "catMsg");
        // Navigation is hardcoded for compatibility with API 23 (our min API). If not hardcoded, it doesn't build.
        // With it, it just don't work. However, it won't work either way so it's fine
        categories.put("navigation", "catNavigation");
        categories.put(Notification.CATEGORY_CALL, "catCall");
        categories.put(Notification.CATEGORY_PROMO, "catPromo");
        categories.put(Notification.CATEGORY_RECOMMENDATION, "catRecommendation");
        categories.put(Notification.CATEGORY_REMINDER, "catReminder");
        categories.put(Notification.CATEGORY_SOCIAL,"catSocial");

        return sp.getBoolean(categories.get(n.category), true);
    }


    private String getAppName(String packageName) {

        PackageManager pm = getApplicationContext().getPackageManager();
        String appName;

        try {
            appName = (String) pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Unable to find app for package " + packageName);
            appName = null;
        }

        return appName;
    }

}

class InsertDbTask extends AsyncTask<DBNotification, Void, Void> {

    // The only way to do this afaik
    @SuppressLint("StaticFieldLeak")
    private Context c;

    InsertDbTask(Context context) {
        c = context;
    }

    @Override
    protected Void doInBackground(DBNotification... dbNotifications) {
        NotificationDatabase.getDatabase(c).dbNotificationDao().insertAll(dbNotifications);
        return null;
    }
}
