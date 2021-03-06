package org.team7.notificationlog.settings;

import android.content.Context;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

/**
 * Custom preference for handling a switch with a clickable preference area as well
 */
public class ExtendedSwitchPreference extends SwitchPreference {

    /**
     * Sets listeners for the switch and the background container preference view cell
     *
     * @param listener A valid ExtendedSwitchListener
     */
    public void setSwitchClickListener(ExtendedSwitchListener listener) {
        this.listener = listener;
    }

    private ExtendedSwitchListener listener = null;

    /**
     * Interface gives callbacks in to both parts of the preference
     */
    public interface ExtendedSwitchListener {
        /**
         * Called when the switch is switched
         *
         * @param buttonView
         * @param isChecked
         */
        void onCheckedChanged(Switch buttonView, boolean isChecked);

        /**
         * Called when the preference view is clicked
         *
         * @param view
         */
        void onClick(View view);
    }

    public ExtendedSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ExtendedSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedSwitchPreference(Context context) {
        super(context);
    }

    /**
     * Recursively go through view tree until we find an android.widget.Switch
     *
     * @param view Root view to start searching
     * @return A Switch class or null
     */
    private Switch findSwitchWidget(View view) {
        if (view instanceof Switch) {
            return (Switch) view;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                if (child instanceof ViewGroup) {
                    Switch result = findSwitchWidget(child);
                    if (result != null) return result;
                }
                if (child instanceof Switch) {
                    return (Switch) child;
                }
            }
        }
        return null;
    }

    //Get a handle on the 2 parts of the switch preference and assign handlers to them
    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        final Switch switchView = findSwitchWidget(view);
        if (switchView != null) {
            switchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onCheckedChanged((Switch) v, ((Switch) v).isChecked());
                }
            });
            switchView.setChecked(getSharedPreferences().getBoolean(getKey(), false));
            switchView.setFocusable(true);
            switchView.setEnabled(true);
            //Set the thumb drawable here if you need to. Seems like this code makes it not respect thumb_drawable in the xml.
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onClick(v);
            }
        });
    }
}