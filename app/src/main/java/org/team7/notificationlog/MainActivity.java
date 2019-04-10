package org.team7.notificationlog;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;

public class MainActivity extends AppCompatActivity {
    private boolean preferencesChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!isNotificationServiceEnabled())
            buildNotificationServiceAlertDialog().show();

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(
                    new SharedPreferences.OnSharedPreferenceChangeListener() {
                        @Override
                        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                            preferencesChanged = true;

                            if (key.equals("trackPersistent")) {
                                boolean trackPersistent = sharedPreferences.getBoolean("trackPersistent", false);

                                // TODO Are we using this for anything?

        //                        if (regions != null & regions.size() > 0) {
        //                            pkmnFragment.updateRegions(sharedPreferences);
        //                            pkmnFragment.reset();
        //                        } else {
        //                            SharedPreferences.Editor editor = sharedPreferences.edit();
        //                            regions.add(getString(R.string.region_default));
        //                            editor.putStringSet(REGIONS, regions);
        //                            editor.apply();
        //                        }
                            }
                            //Toast.makeText(MainActivity.this, getString(R.string.quiz_restart), Toast.LENGTH_SHORT).show();
                        }
                    });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        NLService.application_running = false;
    }

    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null && TextUtils.equals(pkgName, cn.getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private AlertDialog buildNotificationServiceAlertDialog(){

        final AlertDialog.Builder failedDialogBuilder = new AlertDialog.Builder(this);
        failedDialogBuilder.setTitle(R.string.app_name);
        failedDialogBuilder.setMessage(R.string.reject_permissions);
        failedDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.app_name);
        alertDialogBuilder.setMessage(R.string.ask_permissions);
        alertDialogBuilder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                    }
                });
        alertDialogBuilder.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        failedDialogBuilder.create().show();
                    }
                });
        return(alertDialogBuilder.create());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    // handle choice from options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // switch based on the MenuItem id
        switch (item.getItemId()) {
            case R.id.clearlog:
                confirmClear(); // confirm before erasing image
                return true; // consume the menu event
            case R.id.settings:
               Intent preferencesIntent = new Intent(this, SettingsActivity.class);
               startActivity(preferencesIntent);
               return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // confirm whether image should be erase
    private void confirmClear() {
        ConfirmClearDialogFragment fragment = new ConfirmClearDialogFragment();
        fragment.show(getSupportFragmentManager(), "erase dialog");
    }
}
