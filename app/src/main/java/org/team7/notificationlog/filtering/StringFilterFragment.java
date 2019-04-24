package org.team7.notificationlog.filtering;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.team7.notificationlog.R;
import org.team7.notificationlog.db.DBNotification;
import org.team7.notificationlog.db.NotificationDatabase;
import org.team7.notificationlog.db.StringFilter;
import org.team7.notificationlog.main.MainActivityViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import static android.net.wifi.WifiConfiguration.Status.strings;

public class StringFilterFragment extends Fragment implements StringFilterDialog.StringFilterDialogCallback {

    private static final String TAG_APP = "PACKAGE";
    private static String pkg = "";

    FloatingActionButton fab;

    public static StringFilterFragment newInstance(String key) {
        StringFilterFragment fragment = new StringFilterFragment();
        Bundle args = new Bundle();
        args.putString(TAG_APP, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.string_filter_fragment, container, false);

        pkg = getArguments().getString(TAG_APP);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(getAppName(pkg));

        List<StringFilter> initFilters = new ArrayList<>();
        try {
            initFilters = new GetFiltersNonLiveTask(getContext(), pkg).execute().get();
        } catch (Exception ignore) {
        }

        final ListView filterListView = v.findViewById(R.id.filtersList);
        final StringFilterAdapter sfAdapter = new StringFilterAdapter(getContext(), initFilters);
        filterListView.setAdapter(sfAdapter);

        try {
            new GetFiltersTask(getContext(), pkg).execute().get().observe(this, new Observer<List<StringFilter>>() {
                @Override
                public void onChanged(List<StringFilter> stringFilters) {
                    sfAdapter.setFilterData(stringFilters);
                }
            });
        } catch (Exception ignore) {
        }

        final StringFilterDialog.StringFilterDialogCallback callback = this;

        filterListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final StringFilter clicked = (StringFilter) parent.getItemAtPosition(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.delete_filter_title);
                builder.setMessage(R.string.delete_filter_message);
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteTask(getContext(), clicked).execute();
                    }
                });
                builder.create().show();

                return true;
            }
        });

        fab = v.findViewById(R.id.fab);
        fab.bringToFront();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringFilterDialog.newInstance(callback).show(getFragmentManager(), "SFDIALOG");
            }
        });

        return v;
    }

    private String getAppName(String packageName) {

        PackageManager pm = getContext().getPackageManager();
        String appName;

        try {
            appName = (String) pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("SFFRAGMENT", "Unable to find app for package " + packageName);
            appName = null;
        }

        return appName;
    }

    @Override
    public void onAdd(String target, String type, String filter) {
        StringFilter sf = new StringFilter(pkg, target, type, filter);
        new InsertDbTask(getContext()).execute(sf);
    }
}

class DeleteTask extends AsyncTask<Void, Void, Void> {

    // The only way to do this afaik
    @SuppressLint("StaticFieldLeak")
    private Context c;
    private StringFilter sf;

    public DeleteTask(Context context, StringFilter sf) {
        c = context;
        this.sf = sf;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        NotificationDatabase.getDatabase(c).sfDao().deleteFilter(sf);
        return null;
    }
}

class GetFiltersNonLiveTask extends AsyncTask<Void, Void, List<StringFilter>> {

    // The only way to do this afaik
    @SuppressLint("StaticFieldLeak")
    private Context c;
    private String pkg;

    public GetFiltersNonLiveTask(Context context, String pkg) {
        c = context;
        this.pkg = pkg;
    }

    @Override
    protected List<StringFilter> doInBackground(Void... voids) {
        return NotificationDatabase.getDatabase(c).sfDao().getFiltersNonLive(pkg);
    }
}

class GetFiltersTask extends AsyncTask<String, Void, LiveData<List<StringFilter>>> {

    // The only way to do this afaik
    @SuppressLint("StaticFieldLeak")
    private Context c;
    private String pkg;

    public GetFiltersTask(Context context, String pkg) {
        c = context;
        this.pkg = pkg;
    }

    @Override
    protected LiveData<List<StringFilter>> doInBackground(String... strings) {
        return NotificationDatabase.getDatabase(c).sfDao().getFilters(pkg);
    }
}

class InsertDbTask extends AsyncTask<StringFilter, Void, Void> {

    // The only way to do this afaik
    @SuppressLint("StaticFieldLeak")
    private Context c;

    InsertDbTask(Context context) {
        c = context;
    }

    @Override
    protected Void doInBackground(StringFilter... sfs) {
        NotificationDatabase.getDatabase(c).sfDao().insertAll(sfs);
        return null;
    }
}
