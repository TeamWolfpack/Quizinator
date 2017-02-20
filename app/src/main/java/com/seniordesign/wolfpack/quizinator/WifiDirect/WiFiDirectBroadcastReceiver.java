package com.seniordesign.wolfpack.quizinator.WifiDirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "P2P_BroRecv";

    public WiFiDirectBroadcastReceiver(){
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        String action = intent.getAction();
        Intent serviceIntent = new Intent(context,
                                            ConnectionService.class);
            serviceIntent.setAction(action);
            serviceIntent.putExtras(intent);
        context.startService(serviceIntent);
    }
}
