package org.team7.notificationlog;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class StringFilterFragment extends Fragment {

    private static final String TAG_APP = "PACKAGE";

    public static StringFilterFragment newInstance(String key) {
        StringFilterFragment fragment = new StringFilterFragment();
        // supply arguments to bundle.
        Bundle args = new Bundle();
        args.putString(TAG_APP, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
