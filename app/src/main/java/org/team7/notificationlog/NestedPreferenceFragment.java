package org.team7.notificationlog;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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

                final PackageManager pm = this.getActivity().getPackageManager();

                //get a list of installed apps
                List<PackageInfo> packs = pm.getInstalledPackages(0);


                Collections.sort(packs, new Comparator<PackageInfo>() {
                    @Override
                    public int compare(PackageInfo p1, PackageInfo p2) {
                        return p1.applicationInfo.loadLabel(pm).toString().compareTo(p2.applicationInfo.loadLabel(pm).toString());
                    }
                });

                for (PackageInfo p: packs) {
                    if (notExcludedApp(p, this.getContext())) {
                        // TODO AppList not used currently, but could be used if adding images later
                        SwitchPreference pref = new SwitchPreference(screen.getContext());
                        pref.setKey(p.packageName);
                        pref.setSummary(p.packageName);
                        pref.setTitle(p.applicationInfo.loadLabel(pm).toString());
                        pref.setDefaultValue(true);

                        screen.addPreference(pref);
                    }
                }
                break;
        }
    }
    public static boolean notExcludedApp(PackageInfo pkgInfo, Context c) {
        PackageManager pm = c.getPackageManager();

        int flag = pkgInfo.applicationInfo.flags;

        // Ignore certain flags
        if ((flag & ApplicationInfo.FLAG_IS_DATA_ONLY) != 0)
            return false;
        if ((flag & ApplicationInfo.FLAG_TEST_ONLY) != 0)
            return false;
        if ((flag & ApplicationInfo.FLAG_ALLOW_TASK_REPARENTING) != 0)
            return false;
        if ((flag & ApplicationInfo.FLAG_INSTALLED) == 0)
            return false;
        if ((flag & ApplicationInfo.FLAG_HAS_CODE) == 0)
            return false;
        if ((flag & ApplicationInfo.FLAG_MULTIARCH) != 0)
            return false;

        // Ignore apps that don't have a specific name (some android system apps)
        if (pkgInfo.applicationInfo.packageName.equals(pkgInfo.applicationInfo.loadLabel(pm).toString()))
            return false;

        // Ignore certain other apps because Mack doesn't like them (and because they don't send notifications)
        // Seriously, I can't find a way to filter these things out
        // We can just show them all I guess, but it's incredibly frustrating to scroll through everything

        String blacklist[] = {
            "com.android.egg", // Android Easter Egg
            "com.google.android.ext.shared", //Android Shared Library
            "com.google.android.ext.services", //Android Services Library
            "com.google.android.webview", //Android System Webview
            "com.motorola.audioeffects", //Audio Effects
            "com.android.dreams.basic", //Basic Daydreams
            "com.android.bluetoothmidiservice", //Bluetooth MIDI Service
            "com.androidbookmarkprovider", //Bookmark Provider
            "com.motorola.motocit", //CQATest
            "com.android.providers.calendar", // Calendar Storage
            "com.android.calllogbackup", //Call Log Backup/Restore
            "com.android.captiveportallogin", //CaptivePortalLogin
            "com.android.omadm.service", //Carrier Provisioning Service
            "com.android.certinstaller", //Certificate Installer
            //Cloud Print?
            "com.motorola.colorprofiles", //Color Profiles
            "com.google.android.configupdater", //ConfigUpdater
            "com.android.sdm.plugins.connmo", //ConnMO
            "com.android.providers.contacts", //Contacts Storage
            //DM Config Update?
            "com.motorola.demo", //Demo mode
            "com.motorola.genie", //Device Help
            //Device Management?
            "com.motorola.android.settings.diag_mdlog",//DiagMdlog Settings
            "com.motorola.digitalpersonalization", //Digital Personalization
            "com.android.emergency", //Emergency Information
            //Entitlement?
            "com.android.location.fused", //Fused Location
            "com.google.android.inputmethod.latin", //Google Keyboard
            "com.google.android.inputmethod.japanese", //Google Keyboard
            "com.google.android.inputmethod.korean", //Google Keyboard
            "com.google.android.apps.inputmethod.hindi", //Google Keyboard
            //Google Backup Transport?
            "com.google.android.syncadapters.contacts", //Google Contacts Sync
            "com.motorola.launcherconfig", //Google Launcher Config
            "com.google.android.launcher", //Google Now Launcher
            "com.google.android.onetimeinitializer", // Google One Time Init
            "com.google.android.partnersetup", //Google Partner Setup
            "com.google.android.inputmethod.pinyin", // Google Pinyin input
            "com.google.android.instantapps.supervisor", //Google Play Servies for Instant Apps
            "com.google.android.gsf", //Google Services Framework
            "com.google.android.tts", //Google Text-to-speech Engine
            "com.android.htmlviewer", //HTML Viewer
            "com.android.sprint.hiddenmenuapp", //HiddenMenu
            "oom.android.statementservice", //Intent Filter Verification Service
            "com.android.statementservice", //JavaTcmdHelper
            //TODO continue this. It's a pain





        };
        if (Arrays.asList(blacklist).contains(pkgInfo.applicationInfo.packageName))
            return false;

        return true;
    }
}


