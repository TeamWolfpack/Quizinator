package com.seniordesign.wolfpack.quizinator.WifiDirect;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

/**
 *  A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 *
 * @creation 10/26/2016
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "p2pBroadcastReceiver";

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private Activity mActivity;

    public WiFiDirectBroadcastReceiver() {
        super();
    }

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager,
                                       WifiP2pManager.Channel channel,
                                       Activity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }

    /*
     * @author kuczynskij (10/26/2016)
     */
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        String action = intent.getAction();
//        // start ConnectionService
//        Intent serviceIntent = new Intent(
//                context, ConnectionService.class);
//        // put in action and extras
//        serviceIntent.setAction(action);
//        serviceIntent.putExtras(intent);
//        // start the connection service
//        context.startService(serviceIntent);
//    }

    /*
     * @author kuczynskij (10/26/2016)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                //mActivity.setIsWifiP2pEnabled(true);
                Toast.makeText(context, "Wi-Fi Direct is enabled",
                        Toast.LENGTH_LONG).show();
            } else {
                //mActivity.setIsWifiP2pEnabled(false);
                Toast.makeText(context, "Wi-Fi Direct is not enabled",
                        Toast.LENGTH_LONG).show();
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            // The peer list has changed!  We should probably do something about that.
            if (mManager != null) {
                mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peers) {
                        Log.d(TAG, String.format("PeerListListener: %d peers available", peers.getDeviceList().size()));

                        // DO WHATEVER YOU WANT HERE
                        // YOU CAN GET ACCESS TO ALL THE DEVICES YOU FOUND FROM peers OBJECT

                    }
                });
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections

            // Connection state changed!  We should probably do something about
            // that.

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing

//            DeviceListFragment fragment = (DeviceListFragment) mActivity.getFragmentManager()
//                    .findFragmentById(R.id.frag_list);
//            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
//                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));

        }

        action = intent.getAction();
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
