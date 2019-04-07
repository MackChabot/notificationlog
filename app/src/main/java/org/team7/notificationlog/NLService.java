package org.team7.notificationlog;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.room.Room;

public class NLService extends NotificationListenerService {
    // true if application is open. False if is just the service that's running
    // TODO probably replace this variable with a broadcast receiver
    protected static boolean application_running = false;

    private String TAG = this.getClass().getSimpleName();

    //    private NLServiceReceiver nlservicereciver;
    @Override
    public void onCreate() {
        super.onCreate();

//        nlservicereciver = new NLServiceReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("com.example.mynotificationtrackerapp.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
//        registerReceiver(nlservicereciver,filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(nlservicereciver);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        Log.i(TAG,"**********  onNotificationPosted");
        Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());

        //Construct dbn
        Bundle extra = sbn.getNotification().extras;

        String title = extra.getString("android.title");
        String text = "";

        CharSequence chs = extra.getCharSequence("android.text");
        if (chs != null)
            text = chs.toString();

        long when = sbn.getNotification().when;
        long post = sbn.getPostTime();
        long actual = when != 0 ? when : post;

        DBNotification dbn =
                new DBNotification(sbn.getPackageName(),
                                    Long.toString(actual),
                                    getAppName(sbn.getPackageName()),
                                    title,
                                    text);

        Log.i(TAG, "Running insert task");
        new InsertDbTask(getApplicationContext()).execute(dbn);

        // If application is running, add the new notification to the list. Otherwise, add it to the database
//        if (application_running) {
//            // Update the list and refresh the listview
//            MainActivityFragment.notificationList.add(sbn);
//            MainActivityFragment.notificationArrayAdapter.notifyDataSetChanged();
//        }
//        else {
//
//        }

//        Intent i = new Intent("com.example.mynotificationtrackerapp.NOTIFICATION_LISTENER_EXAMPLE");
//        i.putExtra("notification_event","onNotificationPosted :" + sbn.getPackageName() + "\n");
//        sendBroadcast(i);
    }

    /**
     * I presume we do nothing when we remove a notif?
     */
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
//        Log.i(TAG,"********** onNOtificationRemoved");
//        Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText +"\t" + sbn.getPackageName());
//
//        // If application is running, add the new notification to the list. Otherwise, add it to the database
//        if (application_running) {
//            // Update the list and refresh the listview
//            MainActivityFragment.notificationList.add(sbn);
//            MainActivityFragment.notificationArrayAdapter.notifyDataSetChanged();
//        }
//        else{
//        }
//        Intent i = new  Intent("com.example.mynotificationtrackerapp.NOTIFICATION_LISTENER_EXAMPLE");
//        i.putExtra("notification_event","onNotificationRemoved :" + sbn.getPackageName() + "\n");
//
//        sendBroadcast(i);
    }

//    class NLServiceReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if(intent.getStringExtra("command").equals("clearall")){
//                NLService.this.cancelAllNotifications();
//            }
//            else if(intent.getStringExtra("command").equals("list")){
//                Intent i1 = new  Intent("com.example.mynotificationtrackerapp.NOTIFICATION_LISTENER_EXAMPLE");
//                i1.putExtra("notification_event","=====================");
//                sendBroadcast(i1);
//                int i=1;
//                for (StatusBarNotification sbn : NLService.this.getActiveNotifications()) {
//                    Intent i2 = new  Intent("com.example.mynotificationtrackerapp.NOTIFICATION_LISTENER_EXAMPLE");
//                    i2.putExtra("notification_event",i +" " + sbn.getPackageName() + "\n");
//                    sendBroadcast(i2);
//                    i++;
//                }
//                Intent i3 = new  Intent("com.example.mynotificationtrackerapp.NOTIFICATION_LISTENER_EXAMPLE");
//                i3.putExtra("notification_event","===== Notification List ====");
//                sendBroadcast(i3);
//
//            }
//
//        }
//    }

    //TODO: when needed, can use similar technique with PackageManager to get app icon
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

    public InsertDbTask(Context context) {
        c = context;
    }

    @Override
    protected Void doInBackground(DBNotification... dbNotifications) {
        NotificationDatabase.getDatabase(c).dbNotificationDao().insertAll(dbNotifications);
        return null;
    }
}
