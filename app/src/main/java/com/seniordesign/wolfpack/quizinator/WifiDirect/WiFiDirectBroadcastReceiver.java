package com.seniordesign.wolfpack.quizinator.WifiDirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * A BroadcastReceiver that notifies of important wifi p2p events.
 * @creation 10/26/2016
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = "WifiDirectBroadcastReceiver";

    /*
     * @author kuczynskij (10/26/2016)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // start ConnectionService
        Intent serviceIntent = new Intent(
                context, ConnectionService.class);
        // put in action and extras
        serviceIntent.setAction(action);
        serviceIntent.putExtras(intent);
        // start the connection service
    	context.startService(serviceIntent);
    }
}
