package org.team7.notificationlog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.List;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

// class for the Erase Image dialog
public class ConfirmClearDialogFragment extends DialogFragment {
    // create an AlertDialog and return it
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        // set the AlertDialog's message
        builder.setMessage(R.string.clear_log_dialog);

        // add Erase Button
        builder.setPositiveButton(R.string.confirm,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new RemoveAllTask(getContext()).execute();
                    }
                }
        );

        // add cancel Button
        builder.setNegativeButton(android.R.string.cancel, null);
        return builder.create(); // return dialog
    }

    // gets a reference to the MainActivityFragment
    private MainActivityFragment getNotificationFragment() {
        return (MainActivityFragment) getFragmentManager().findFragmentById(
                R.id.notificationFragment);
    }
}

class RemoveAllTask extends AsyncTask<Void, Void, Void> {

    // The only way to do this afaik
    @SuppressLint("StaticFieldLeak")
    private Context c;

    public RemoveAllTask(Context context) {
        c = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        NotificationDatabase ndb = NotificationDatabase.getDatabase(c);
        ndb.clearAllTables();
        ndb.query(new SimpleSQLiteQuery("UPDATE sqlite_sequence SET seq = 0 WHERE name = \'notifications\'"));

        return null;
    }
}
