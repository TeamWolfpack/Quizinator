package com.seniordesign.wolfpack.quizinator.WifiDirect;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Message;
import android.util.Log;

import com.seniordesign.wolfpack.quizinator.Activities.GamePlayActivity;
import com.seniordesign.wolfpack.quizinator.Activities.HostGameActivity;
import com.seniordesign.wolfpack.quizinator.Activities.ManageGameplayActivity;

import java.util.ArrayList;
import java.util.List;

import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.MSG_STARTCLIENT;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.MSG_STARTSERVER;

/**
 * Creates an instance of a WifiDirect Application because it is a
 * global singleton.
 */
public class WifiDirectApp extends Application {

    private static final String TAG = "WifiDirectApp";

    public WifiP2pManager mP2pMan;
    public WifiP2pManager.Channel mP2pChannel;
    public boolean mP2pConnected;
    public String mMyAddress;
    // the p2p name that is configured from UI.
    public String mDeviceName;
    public WiFiDirectBroadcastReceiver mReceiver;

    public WifiP2pDevice mThisDevice;
    // set when connection info available, reset
    //  when WIFI_P2P_CONNECTION_CHANGED_ACTION
    public WifiP2pInfo mP2pInfo;

    public boolean mIsServer;
    public IntentFilter mIntentFilter;

    public HostGameActivity mHomeActivity;
    public GamePlayActivity mGameplayActivity;
    public ManageGameplayActivity mManageActivity;

    // update on every peers available
    public List<WifiP2pDevice> mPeers = new ArrayList<>();

    // Singleton instance
    private static WifiDirectApp sInstance = null;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        sInstance = this;
        instantiateIntentFilter();
    }

    /**
     * Getter to access Singleton instance.
     * @return the WifiDirectApp
     */
    public static WifiDirectApp getInstance() {
        return sInstance ;
    }

    private void instantiateIntentFilter(){
        mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    /**
     * bcast listener always gets enable/disable intent
     * and persists to shared pref.
     *
     * @return true if P2P is enabled on device
     */
    public boolean isP2pEnabled() {
        String state =
                AppPreferences.getStringFromPref(this,
                        AppPreferences.PREF_NAME, AppPreferences.P2P_ENABLED);
        Log.d(TAG, "isP2pEnabled: " + (state != null && "1".equals(state.trim())));
        return state != null && "1".equals(state.trim());
    }

    /**
     * Determines if the instance of the WifiDirectApp is currently
     * a host or not.
     * @return true if device is host
     */
    public int isHost() {
        Log.d(TAG, "isHost: " + (mIsServer ? 15 : 0));
        return mIsServer ? 15 : 0;
    }

    /**
     * Upon p2p connection available, group owner start server socket
     * channel start socket server and select monitor the socket.
     */
    public void startSocketServer() {
        Log.d(TAG, "startSocketServer");
        Message msg =
                ConnectionService.getInstance().getHandler().obtainMessage();
            msg.what = MSG_STARTSERVER;
        ConnectionService.getInstance().getHandler().sendMessage(msg);
    }

    /**
     * Upon p2p connection available, non group owner start
     * socket channel connect to group owner.
     */
    public void startSocketClient(String hostname) {
        Log.d(TAG, "startSocketClient : client connect to group owner : " + hostname);
        Message msg = ConnectionService.getInstance().getHandler().obtainMessage();
            msg.what = MSG_STARTCLIENT;
            msg.obj = hostname;
        ConnectionService.getInstance().getHandler().sendMessage(msg);
    }

    public List<WifiP2pDevice> getConnectedPeers() {
        Log.d(TAG, "getConnectedPeers");
        ArrayList<WifiP2pDevice> peers = new ArrayList<>();
        for (WifiP2pDevice d : mPeers) {
            if (d.status == WifiP2pDevice.CONNECTED) {
                peers.add(d);
                Log.d(TAG, "getConnectedPeers: Device Connected " + d.toString());
            }
        }
        return peers;
    }

    public List<WifiP2pDevice> getFilteredPeerList() {
        List<WifiP2pDevice> filteredPeers = new ArrayList<>();
        if (mIsServer) {
            Log.d(TAG, "onPeersAvailable: wifiDirectApp.mServer is true (HOST)");
            for (WifiP2pDevice device : mPeers) {
                if (device.status == WifiP2pDevice.CONNECTED)
                    filteredPeers.add(device);
            }
        } else {
            Log.d(TAG, "onPeersAvailable: wifiDirectApp.mServer is false (CLIENT)");
            for (WifiP2pDevice device : mPeers) {
                //Order connected device to the top of the list
                if (device.isGroupOwner() && device.status == WifiP2pDevice.CONNECTED)
                    filteredPeers.add(0, device);
                else if (device.isGroupOwner())
                    filteredPeers.add(device);
            }
        }
        return filteredPeers;
    }

    public void disconnectFromGroup() {
        Log.d(TAG, "disconnectFromGroup");
        if (mP2pMan == null || mP2pChannel == null) {
            return;
        }
        mP2pMan.requestGroupInfo(mP2pChannel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup group) {
                if (group != null) {
                    mP2pMan.removeGroup(mP2pChannel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "disconnectFromGroup: removeGroup success");
                        }

                        @Override
                        public void onFailure(int reason) {
                            Log.d(TAG, "disconnectFromGroup: removeGroup fail: " + reason);
                        }
                    });
                }
            }
        });
    }

    public void setMyAddress(String addr) {
        Log.d(TAG, "setMyAddress");
        mMyAddress = addr;
    }

    public Intent getLaunchActivityIntent(Class<?> cls, String initializeMessage){
        Log.d(TAG, "getLaunchActivityIntent");
        //get the intent to launch any activity
        Intent i = new Intent(this, cls);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("FIRST_MSG", initializeMessage);
        return i;
    }

    /**
     * Properly resume the WifiDirectApp.
     */
    public void onResume(String tag,
                         HostGameActivity activity){
        Log.d(tag, "onResume called");
        mReceiver = new WiFiDirectBroadcastReceiver();
        registerReceiver(mReceiver, mIntentFilter);
        mHomeActivity = activity;
    }

    /**
     * Properly pause the WifiDirectApp.
     */
    public void onPause(String tag){
        Log.d(tag, "onPause called");
        unregisterReceiver(mReceiver);
    }

    /**
     * Properly destroy the WifiDirectApp.
     */
    public void onDestroy(String tag){
        Log.d(tag, "onDestroy called");
        disconnectFromGroup();

        if (mHomeActivity != null)
            mHomeActivity.finish();

        mP2pConnected = false;
        mHomeActivity = null;
        mGameplayActivity = null;
        mManageActivity = null;
        mIsServer = false;
    }
}
