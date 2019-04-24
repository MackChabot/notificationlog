package org.team7.notificationlog.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.Fragment;

import org.team7.notificationlog.R;

public class AppListFragment extends Fragment {

    private static final String TAG_APP = "PACKAGE";

    public static AppListFragment newInstance(String key) {
        AppListFragment fragment = new AppListFragment();
        // supply arguments to bundle.
        Bundle args = new Bundle();
        args.putString(TAG_APP, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.app_list_fragment, container, false);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
