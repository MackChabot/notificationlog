package org.team7.notificationlog;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import androidx.fragment.app.Fragment;

public class NotificationArrayAdapter extends ArrayAdapter<DBNotification> {

    private static class ViewHolder {
        TextView titleTextView;
        TextView textTextView;
        TextView packageTextView;
    }

    Fragment maf;
    List<DBNotification> dbns;

    // constructor to initialize superclass inherited members
    public NotificationArrayAdapter(Fragment fragment, Context context, List<DBNotification> notifications) {
        super(context, -1, notifications);
        maf = fragment;
    }

    // creates the custom views for the ListView's items
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("NotifArrayAdapter", "getView called");

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
            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.notificationTitle);
            viewHolder.textTextView = (TextView) convertView.findViewById(R.id.notificationText);
            viewHolder.packageTextView = (TextView) convertView.findViewById(R.id.notificationPackage);
            convertView.setTag(viewHolder);
        }
        else { // reuse existing ViewHolder stored as the list item's tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // VERY TEMPORARY
        String title = dbn.title;

        DateFormat fmt = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        Date date = new Date(Long.parseLong(dbn.strTimestamp));
        title += " - " + fmt.format(date);

        viewHolder.titleTextView.setText(title);
        // END VERY TEMPORARY

        viewHolder.textTextView.setText(dbn.text);
        viewHolder.packageTextView.setText(dbn.appName);

        return convertView; // return completed list item to display
    }

    public void setNotifData(List<DBNotification> dbns) {
        Log.i("NotifArrayAdapter", "Updating notification data with " + dbns.size() + " notifs");
        this.dbns = dbns;

        this.clear();
        this.addAll(dbns);

        // new notifs first
        this.sort(new Comparator<DBNotification>() {
            @Override
            public int compare(DBNotification o1, DBNotification o2) {
                Long l1 = Long.parseLong(o1.strTimestamp);
                Long l2 = Long.parseLong(o2.strTimestamp);

                return Long.compare(l2, l1);
            }
        });

        this.notifyDataSetChanged();
    }
}
