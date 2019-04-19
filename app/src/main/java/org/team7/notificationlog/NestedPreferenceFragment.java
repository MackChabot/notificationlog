package org.team7.notificationlog;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NestedPreferenceFragment extends PreferenceFragment {

    public static final int NESTED_SCREEN_1_KEY = 1;
    public static final int NESTED_SCREEN_2_KEY = 2;

    private static final String TAG_KEY = "NESTED_KEY";

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

        checkPreferenceResource();
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

//                EditTextPreference preference = new EditTextPreference(screen.getContext());
//                preference.setKey("EditTextPreference");
//                preference.setTitle("Edit Text Preference");
//                preference.setSummary("Click the preference to edit text.");
//                screen.addPreference(preference);
//
                PackageManager pm = this.getActivity().getPackageManager();
//                //get a list of installed apps.
//                List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
//
//                for (ApplicationInfo packageInfo : packages) {
//                    int flags = packageInfo.flags;
//
//
//
//                    SwitchPreference pref = new SwitchPreference(screen.getContext());
//                    pref.setKey(packageInfo.packageName);
//                    pref.setTitle(packageInfo.packageName.substring(packageInfo.packageName.lastIndexOf('.') + 1));
//
//                    screen.addPreference(pref);
//                }
                List<AppList> res = new ArrayList<AppList>();
                List<PackageInfo> packs = pm.getInstalledPackages(0);
                for (int i = 0; i < packs.size(); i++) {
                    PackageInfo p = packs.get(i);
                    if (notExcludedApp(p)) {
                        String pack = p.packageName;
                        String appName = p.applicationInfo.loadLabel(pm).toString();
                        Drawable icon = p.applicationInfo.loadIcon(pm);
                        res.add(new AppList(appName, pack, icon));
                    }
                }

                Collections.sort(res);

                for (AppList app : res) {
                    SwitchPreference pref = new SwitchPreference(screen.getContext());
                    pref.setKey(app.packageName);
                    pref.setTitle(app.name);

                    screen.addPreference(pref);
                }
                break;
        }
    }
    private boolean notExcludedApp(PackageInfo pkgInfo) {
        int flag = pkgInfo.applicationInfo.flags;

        // Ignore certain flags
        if ((flag & ApplicationInfo.FLAG_IS_DATA_ONLY) != 0)
            return false;
        if ((flag & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)
            return false;
        if ((flag & ApplicationInfo.FLAG_TEST_ONLY) != 0)
            return false;
//        return ( ( | /*ApplicationInfo.FLAG_UPDATED_SYSTEM_APP |*/ ApplicationInfo.FLAG_TEST_ONLY
////                | ApplicationInfo.FLAG_SYSTEM
//        ) & ) != 0;
        return true;
    }
}


