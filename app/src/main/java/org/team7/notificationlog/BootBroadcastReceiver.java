package org.team7.notificationlog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// Launches the background service when the phone starts up
public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, NLService.class);
        context.startService(startServiceIntent);
    }
}