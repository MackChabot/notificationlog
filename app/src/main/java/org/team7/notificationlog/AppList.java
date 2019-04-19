package org.team7.notificationlog;

import android.graphics.drawable.Drawable;

public class AppList implements Comparable<AppList> {

    public String name;
    public String packageName;
    public Drawable  icon; // Uhh, how do we use this in a preference?

    public AppList(String name, String packageName, Drawable icon) {
        this.name = name;
        this.packageName = packageName;
        this.icon = icon;
    }

    @Override
    public int compareTo(AppList app) {
        return this.name.compareTo(app.name);
    }
}
