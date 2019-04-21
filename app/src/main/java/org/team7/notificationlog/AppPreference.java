package org.team7.notificationlog;

import android.content.Context;
import android.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AppPreference extends SwitchPreference {
    public AppPreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent ) {
        super.onCreateView(parent);
        LayoutInflater li = (LayoutInflater)getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        return li.inflate( R.layout.app_preference, parent, false);


    }
}
