package org.team7.notificationlog.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.team7.notificationlog.R;
import org.team7.notificationlog.db.DBNotification;
import org.team7.notificationlog.db.StringFilter;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import androidx.fragment.app.Fragment;

public class NotificationArrayAdapter extends ArrayAdapter<DBNotification> {

    private static class ViewHolder {
        ImageView iconImageView;
        TextView titleTextView;
        TextView textTextView;
        TextView timeTextView;
        TextView nameTextView;
    }

    Fragment maf;
    List<DBNotification> dbns;

    private AdapterView.OnItemClickListener itemClickedHandler = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            DBNotification dbn = getItem(position);
            if (dbn == null)
                return;

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Debug Notif");
            builder.setMessage(dbn.toString());
        }
    };

    // constructor to initialize superclass inherited members
    public NotificationArrayAdapter(Fragment fragment, Context context, List<DBNotification> notifications) {
        super(context, -1, notifications);
        maf = fragment;
    }

    // creates the custom views for the ListView's items
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get Weather object for this specified ListView position
        DBNotification dbn = getItem(position);
        if (dbn == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            return convertView;
        }

        ViewHolder viewHolder; // object that reference's list item's views

        // check for reusable ViewHolder from a ListView item that scrolled
        // offscreen; otherwise, create a new ViewHolder
        if (convertView == null) { // no reusable ViewHolder, so create one
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder.iconImageView = convertView.findViewById(R.id.imageView);
            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.notificationTitle);
            viewHolder.textTextView = (TextView) convertView.findViewById(R.id.notificationText);
            viewHolder.timeTextView = (TextView) convertView.findViewById(R.id.notificationTime);
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.applicationName);
            convertView.setTag(viewHolder);
        }
        else { // reuse existing ViewHolder stored as the list item's tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Drawable icon = getAppIcon(dbn.notifPackage);
        if (icon != null)
            viewHolder.iconImageView.setImageDrawable(icon);

        DateFormat fmt = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        Date date = new Date(Long.parseLong(dbn.strTimestamp));

        viewHolder.nameTextView.setText(dbn.appName);
        viewHolder.timeTextView.setText(fmt.format(date));
        viewHolder.titleTextView.setText(dbn.title);
        viewHolder.textTextView.setText(dbn.text);

        return convertView;
    }

    private Drawable getAppIcon(String packageName) {

        PackageManager pm = getContext().getPackageManager();
        Drawable icon = null;

        try {
            icon = pm.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("NotificationLog", "Unable to find app for package " + packageName);
        }

        return icon;
    }

    void setNotifData(List<DBNotification> dbns, Map<String, List<StringFilter>> filters) {

        List<DBNotification> filteredDbn = new ArrayList<>();

        try {
            filteredDbn = new ProcessNotifs(filters).execute(dbns.toArray(new DBNotification[0])).get();
        } catch (Exception ignore) {}

        Log.i("NotifArrayAdapter", "Updating notifications data with " + dbns.size() + " notif");
        Log.i("NotifArrayAdapter", "Filtered data contains " + filteredDbn.size() + " notif");
        this.dbns = filteredDbn;

        this.clear();
        this.addAll(filteredDbn);

        this.notifyDataSetChanged();
    }
}

class ProcessNotifs extends AsyncTask<DBNotification, Void, List<DBNotification>> {

    private Map<String, List<StringFilter>> filters;

    ProcessNotifs(Map<String, List<StringFilter>> filters) {
        this.filters = filters;
    }

    @Override
    protected List<DBNotification> doInBackground(DBNotification... dbns) {

        if (android.os.Debug.isDebuggerConnected())
            android.os.Debug.waitForDebugger();

        List<DBNotification> returned = new ArrayList<>();

        for (DBNotification dbn : dbns) {

            List<StringFilter> filtersForPkg = filters.get(dbn.notifPackage);

            boolean shouldAdd = true;
            if (filtersForPkg != null) {
                for (StringFilter sf : filtersForPkg) {

                    if (!shouldAdd)
                        continue;

                    boolean isMatch;
                    String toCheck;

                    isMatch = sf.type.equals("matches");
                    toCheck = sf.target.equals("Title") ? dbn.title : dbn.text;

                    if (isMatch) {
                        if (toCheck.equals(sf.filterText))
                            shouldAdd = false;
                    } else {
                        if (toCheck.contains(sf.filterText))
                            shouldAdd = false;
                    }
                }
            }

            if (shouldAdd)
                returned.add(dbn);

        }

        Collections.sort(returned, new Comparator<DBNotification>() {
            @Override
            public int compare(DBNotification o1, DBNotification o2) {
                Long l1 = Long.parseLong(o1.strTimestamp);
                Long l2 = Long.parseLong(o2.strTimestamp);

                return Long.compare(l2, l1);
            }
        });

        return returned;
    }
}


