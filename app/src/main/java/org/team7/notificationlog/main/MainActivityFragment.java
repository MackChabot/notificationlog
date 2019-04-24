package org.team7.notificationlog.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.team7.notificationlog.db.StringFilter;
import org.team7.notificationlog.service.NLService;
import org.team7.notificationlog.R;
import org.team7.notificationlog.db.DBNotification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class MainActivityFragment extends Fragment {
    public static NotificationArrayAdapter notificationArrayAdapter;
    public static MainActivityViewModel mvm;

    // called when Fragment's view needs to be created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        setHasOptionsMenu(true); // this fragment has menu items to display

        NLService.application_running = true;

        mvm = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        final List<DBNotification> notificationList = mvm.getNotificationsBase();

        // displays notification info
        final ListView notificationListView = view.findViewById(R.id.notificationListView);
        notificationArrayAdapter = new NotificationArrayAdapter(this, getActivity().getApplicationContext(), notificationList);
        notificationListView.setAdapter(notificationArrayAdapter);

        // bind to livedata
        mvm.getNotifications(this.getActivity().getApplicationContext()).observe(this, new Observer<List<DBNotification>>() {
            @Override
            public void onChanged(List<DBNotification> dbNotifications) {
                Map<String, List<StringFilter>> filters = mvm.constructFilters(getContext());
                notificationArrayAdapter.setNotifData(dbNotifications, filters);
            }
        });

        Toolbar appBar = view.findViewById(R.id.toolbar);
        appBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationListView.setSelection(0);
            }
        });

        // Default spinner value to preference value
        final Spinner sort = view.findViewById(R.id.sortSpinner);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String pref = sharedPref.getString(getString(R.string.sortKey), "Time");
        switch (pref) {
            case "App":
                sort.setSelection(0);
                break;
            case "Time":
                sort.setSelection(1);
                break;
        }

        sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.sortKey), (String) sort.getSelectedItem());
                editor.apply();

                switch ((String) sort.getSelectedItem()) {
                    case "App":
                        Collections.sort(notificationList, new Comparator<DBNotification>() {
                            @Override
                            public int compare(DBNotification d1, DBNotification d2) {
                                // Sort by App, then by time
                                int cmp1 = d1.appName.compareTo(d2.appName);
                                if (cmp1 != 0)
                                    return cmp1;
                                return d2.strTimestamp.compareTo(d1.strTimestamp);
                            }
                        });
                        break;
                    case "Time":
                        Collections.sort(notificationList, new Comparator<DBNotification>() {
                            @Override
                            public int compare(DBNotification d1, DBNotification d2) {
                                return d2.strTimestamp.compareTo(d1.strTimestamp);
                            }
                        });
                        break;
                }
                notificationListView.setAdapter(notificationArrayAdapter);
                notificationArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
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
