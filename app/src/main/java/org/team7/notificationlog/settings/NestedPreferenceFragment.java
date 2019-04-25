package org.team7.notificationlog.settings;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.Switch;

import org.team7.notificationlog.db.NotificationDatabase;
import org.team7.notificationlog.main.MainActivityFragment;
import org.team7.notificationlog.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NestedPreferenceFragment extends PreferenceFragment {

    public static final int NESTED_SCREEN_1_KEY = 1;
    public static final int NESTED_SCREEN_2_KEY = 2;

    private static final String TAG_KEY = "NESTED_KEY";

    private List<ExtendedSwitchPreference> validAppPreferences = new ArrayList<>();

    public static NestedPreferenceFragment newInstance(int key) {
        NestedPreferenceFragment fragment = new NestedPreferenceFragment();
        // supply arguments to bundle.
        Bundle args = new Bundle();
        args.putInt(TAG_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        updateValidAppPreferences(this.getContext(), false);
        checkPreferenceResource();
    }

    // TODO call this in the background or something before we have to load these so it doesn't lag the app
    public  void updateValidAppPreferences(Context c, boolean showAllApps) {
        List<String> validPackages = MainActivityFragment.mvm.getPackages(c);

        //if there are no new preferences, don't waste time updating
        if (validAppPreferences.size() == validPackages.size())
            return;

        validAppPreferences.clear();

        final PackageManager pm = c.getPackageManager();

        //get a list of installed apps
        List<PackageInfo> packs = pm.getInstalledPackages(0);

        Collections.sort(packs, new Comparator<PackageInfo>() {
            @Override
            public int compare(PackageInfo p1, PackageInfo p2) {
                return p1.applicationInfo.loadLabel(pm).toString().compareTo(p2.applicationInfo.loadLabel(pm).toString());
            }
        });

        for (PackageInfo p : packs) {
            if (showAllApps || validPackages.contains(p.packageName)) {
                // TODO AppList not used currently, but could be used if adding images later
                ExtendedSwitchPreference pref = new ExtendedSwitchPreference(c);
                pref.setKey(p.packageName);

                int numFilters = NotificationDatabase.getDatabase(getContext()).sfDao().getNumFiltersForPkg(p.packageName);

                String filterSummary = "";
                if (numFilters == 1)
                    filterSummary = getString(R.string.filters_single);
                else
                    filterSummary = getString(R.string.filters_plural, Integer.toString(numFilters));

                pref.setSummary(filterSummary);

                pref.setTitle(p.applicationInfo.loadLabel(pm).toString());
                pref.setDefaultValue(true);
                validAppPreferences.add(pref);
            }
        }
    }

    private void checkPreferenceResource() {
        int key = getArguments().getInt(TAG_KEY);
        // Load the preferences from an XML resource
        switch (key) {
            case NESTED_SCREEN_1_KEY:
                addPreferencesFromResource(R.xml.category_preferences);
                break;

            case NESTED_SCREEN_2_KEY:
                PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(getActivity());
                setPreferenceScreen(screen);

                for (final ExtendedSwitchPreference pref : validAppPreferences) {

                    pref.setSwitchClickListener(new ExtendedSwitchPreference.ExtendedSwitchListener() {
                        @Override
                        public void onCheckedChanged(Switch buttonView, boolean isChecked) {
                        }

                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(getContext(), AppListActivity.class);
                            i.putExtra("PACKAGE", pref.getKey());
                            startActivity(i);
                        }
                    });

                    screen.addPreference(pref);
                }

                break;
        }
    }
}


