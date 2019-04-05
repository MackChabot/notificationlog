package org.team7.notificationlog;

import android.app.Notification;
import android.content.Context;
import android.service.notification.StatusBarNotification;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class NotificationArrayAdapter extends ArrayAdapter<StatusBarNotification> {
    private static class ViewHolder {
        TextView titleTextView;
        TextView textTextView;
        TextView packageTextView;
    }
    // constructor to initialize superclass inherited members
    public NotificationArrayAdapter(Context context, List<StatusBarNotification> notifications) {
        super(context, -1, notifications);
    }

    // creates the custom views for the ListView's items
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get Weather object for this specified ListView position
        StatusBarNotification sbn = getItem(position);

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

        // get other data from Weather object and place into views
        Context context = getContext(); // for loading String resources
        viewHolder.titleTextView.setText(context.getString( R.string.basic_string_format, sbn.getNotification().extras.getString("android.title")));
        viewHolder.textTextView.setText(context.getString( R.string.basic_string_format, sbn.getNotification().extras.getString("android.text")));
        viewHolder.packageTextView.setText(context.getString( R.string.basic_string_format, sbn.getPackageName()));

        return convertView; // return completed list item to display
    }
}
