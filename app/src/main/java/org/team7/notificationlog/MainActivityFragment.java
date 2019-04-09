// MainActivityFragment.java
// Fragment in which the DoodleView is displayed
package org.team7.notificationlog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

        // displays weather info
        ListView notificationListView = view.findViewById(R.id.notificationListView);
        notificationArrayAdapter = new NotificationArrayAdapter(this, getActivity().getApplicationContext(), notificationList);
        notificationListView.setAdapter(notificationArrayAdapter);

        // bind to livedata
        mvm.getNotifications().observe(this, new Observer<List<DBNotification>>() {
            @Override
            public void onChanged(List<DBNotification> dbNotifications) {
                notificationArrayAdapter.setNotifData(dbNotifications);
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

    // displays the fragment's menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.options_menu, menu);
    }

    // handle choice from options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // switch based on the MenuItem id
        switch (item.getItemId()) {
//            case R.id.color:
//                ColorDialogFragment colorDialog = new ColorDialogFragment();
//                colorDialog.show(getFragmentManager(), "color dialog");
//                return true; // consume the menu event
//            case R.id.line_width:
//                LineWidthDialogFragment widthDialog =
//                        new LineWidthDialogFragment();
//                widthDialog.show(getFragmentManager(), "line width dialog");
//                return true; // consume the menu event
            case R.id.clearlog:
                confirmClear(); // confirm before erasing image
                return true; // consume the menu event
//            case R.id.save:
//                saveImage(); // check permission and save current image
//                return true; // consume the menu event
//            case R.id.print:
//                doodleView.printImage(); // print the current images
//                return true; // consume the menu event
        }

        return super.onOptionsItemSelected(item);
    }

    // confirm whether image should be erase
    private void confirmClear() {
        ConfirmClearDialogFragment fragment = new ConfirmClearDialogFragment();
        fragment.show(getFragmentManager(), "erase dialog");
    }
}
