package org.team7.notificationlog.db;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Parcel;
import android.util.Log;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PersistentParcelLoader {

    private static void saveParcel(Context c, String packageName, String timestamp, Parcel p) {

        PersistentParcel pp = new PersistentParcel();
        pp.packageName = packageName;
        pp.timestamp = timestamp;

        Long lTimestamp = Long.parseLong(timestamp);
        Date expDate = new Date(lTimestamp + TimeUnit.HOURS.toMillis(1));
        String strExp = String.valueOf(expDate.getTime());
        pp.expiresTimestamp = strExp;
        pp.data = p.marshall();

        new InsertDbTask(c).execute(pp);
        p.recycle();
    }

    private static Parcel loadParcel(Context c, String packageName, String timestamp) {
        PersistentParcel pp;
        try {
            pp = new GetTask(c, packageName, timestamp).execute().get();
            if (pp == null)
                return null;
        } catch (InterruptedException | ExecutionException e) {
            Log.e("PersistentParcelLoader", "Could not load persistent parcel", e);
            return null;
        }

        Parcel p = Parcel.obtain();
        p.unmarshall(pp.data, 0, pp.data.length);

        return p;
    }

    public static void savePendingIntent(Context c, String packageName, String timestamp, PendingIntent pi) {

        Parcel p = Parcel.obtain();
        PendingIntent.writePendingIntentOrNullToParcel(pi, p);
        saveParcel(c, packageName, timestamp, p);
    }

    public static PendingIntent loadPendingIntent(Context c, String packageName, String timestamp) {
        Parcel p = loadParcel(c, packageName, timestamp);
        PendingIntent pi = PendingIntent.readPendingIntentOrNullFromParcel(p);
        return pi;
    }
}

class InsertDbTask extends AsyncTask<PersistentParcel, Void, Void> {

    // The only way to do this afaik
    @SuppressLint("StaticFieldLeak")
    private Context c;

    InsertDbTask(Context context) {
        c = context;
    }

    @Override
    protected Void doInBackground(PersistentParcel... pp) {
        if (pp[0] != null)
            NotificationDatabase.getDatabase(c).persistentParcelDao().savePersistentParcel(pp[0]);
        return null;
    }
}

class GetTask extends AsyncTask<Void, Void, PersistentParcel> {

    // The only way to do this afaik
    @SuppressLint("StaticFieldLeak")
    private Context c;
    private String packageName;
    private String timestamp;

    public GetTask(Context context, String packageName, String timestamp) {
        c = context;
        this.packageName = packageName;
        this.timestamp = timestamp;
    }

    @Override
    protected PersistentParcel doInBackground(Void... voids) {
        return NotificationDatabase.getDatabase(c).persistentParcelDao().getPersistentParcel(packageName, timestamp);
    }
}