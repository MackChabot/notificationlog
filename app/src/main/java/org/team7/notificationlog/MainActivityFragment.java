// MainActivityFragment.java
// Fragment in which the DoodleView is displayed
package org.team7.notificationlog;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class MainActivityFragment extends Fragment {
    public static NotificationArrayAdapter notificationArrayAdapter;

    // called when Fragment's view needs to be created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        setHasOptionsMenu(true); // this fragment has menu items to display

        NLService.application_running = true;

        MainActivityViewModel mvm = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        // List of StatusBarNotification objects representing the forecast
        List<DBNotification> notificationList = mvm.getNotificationsBase();

        // displays notification info
        ListView notificationListView = view.findViewById(R.id.notificationListView);
        notificationArrayAdapter = new NotificationArrayAdapter(this, getActivity().getApplicationContext(), notificationList);
        notificationListView.setAdapter(notificationArrayAdapter);

        // bind to livedata
        mvm.getNotifications(this.getActivity().getApplicationContext()).observe(this, new Observer<List<DBNotification>>() {
            @Override
            public void onChanged(List<DBNotification> dbNotifications) {
                notificationArrayAdapter.setNotifData(dbNotifications);
            }
        });

        notificationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DBNotification dbn = (DBNotification) parent.getItemAtPosition(position);

                PackageManager pm = getContext().getPackageManager();
                Intent i = pm.getLaunchIntentForPackage(dbn.notifPackage);

                if (i != null)
                    startActivity(i);
            }
        });

        notificationListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DBNotification dbn = (DBNotification) parent.getItemAtPosition(position);
                if (dbn == null)
                    return false;

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Debug Notif");
                builder.setMessage(dbn.toString());
                builder.create().show();
                return true;
            }
        });

        return view;
    }

}
