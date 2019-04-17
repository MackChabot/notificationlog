package org.team7.notificationlog;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

public class SettingsActivity extends AppCompatActivity implements SettingsActivityFragment.Callback {

    private static final String TAG_NESTED = "TAG_NESTED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.contentSettings, new SettingsActivityFragment())
                    .commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // this if statement is necessary to navigate through nested and main fragments with the toolbar back arrow
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onSupportNavigateUp();
        } else {
            getFragmentManager().popBackStack();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        // this if statement is necessary to navigate through nested and main fragments with the back button
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            NavUtils.navigateUpFromSameTask(this);
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onNestedPreferenceSelected(int key) {
        getFragmentManager().beginTransaction().replace(R.id.contentSettings, NestedPreferenceFragment.newInstance(key), TAG_NESTED).addToBackStack(TAG_NESTED).commit();
    }
}
