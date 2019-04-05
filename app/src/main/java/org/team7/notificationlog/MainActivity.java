package org.team7.notificationlog;

import android.service.notification.StatusBarNotification;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // List of Weather objects representing the forecast
    public static List<StatusBarNotification> notificationList = new ArrayList<>();
//    private List<String> stringList = new ArrayList<>();

    // ArrayAdapter for binding Weather objects to a ListView
    public static NotificationArrayAdapter notificationArrayAdapter;
    private ListView notificationListView; // displays weather info

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //notificationList.add("First String");

        // create ArrayAdapter to bind weatherList to the weatherListView
        notificationListView = (ListView) findViewById(R.id.notificationListView);
        notificationArrayAdapter = new NotificationArrayAdapter(this, notificationList);
//        notificationArrayAdapter = new NotificationArrayAdapter(this, notificationList);
        notificationListView.setAdapter(notificationArrayAdapter);
    }

    public void update(){
        notificationListView = (ListView) findViewById(R.id.notificationListView);
        notificationArrayAdapter = new NotificationArrayAdapter(this, notificationList);
//        notificationArrayAdapter = new NotificationArrayAdapter(this, notificationList);
        notificationListView.setAdapter(notificationArrayAdapter);
    }
}
