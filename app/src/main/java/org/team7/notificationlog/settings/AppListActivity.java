package org.team7.notificationlog.settings;

import android.os.Bundle;

import org.team7.notificationlog.R;
import org.team7.notificationlog.filtering.StringFilterFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AppListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_list_fragment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            String pkg = getIntent().getExtras().getString("PACKAGE");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_app_fragment, StringFilterFragment.newInstance(pkg))
                    .commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
