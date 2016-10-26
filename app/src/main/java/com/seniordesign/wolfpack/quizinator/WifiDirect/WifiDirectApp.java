package com.seniordesign.wolfpack.quizinator.WifiDirect;

import android.app.Application;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

import com.seniordesign.wolfpack.quizinator.Activities.HostGameActivity;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates an instance of a WifiDirect Application because it is a
 * global singleton.
 * @creation 10/24/2016.
 */
public class WifiDirectApp extends Application {

    private static final String TAG = "PTP_APP";

    public WifiP2pManager mP2pMan;
    public WifiP2pManager.Channel mP2pChannel;
    public boolean mP2pConnected;
    public String mMyAddress;
    // the p2p name that is configurated from UI.
    public String mDeviceName;

    public WifiP2pDevice mThisDevice;
    // set when connection info available, reset
    //  when WIFI_P2P_CONNECTION_CHANGED_ACTION
    public WifiP2pInfo mP2pInfo;

    public boolean mIsServer;

    public HostGameActivity mHomeActivity;
    // update on every peers available
    public List<WifiP2pDevice> mPeers = new ArrayList<>();
    // limit to the latest 50 messages
    JSONArray mMessageArray = new JSONArray();

    /*
     * @author kuczynskij (10/26/2016)
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * bcast listener always gets enable/disable intent
     * and persists to shared pref.
     * @return true if P2P is enabled on device
     */
    /*
     * @author kuczynskij (10/26/2016)
     */
    public boolean isP2pEnabled() {
        String state =
                AppPreferences.getStringFromPref(
                        this,
                        AppPreferences.PREF_NAME,
                        AppPreferences.P2P_ENABLED);
        return state != null && "1".equals(state.trim());
    }

    /**
     * Upon p2p connection available, group owner start server socket
     * channel start socket server and select monitor the socket.
     */
    /*
     * @author kuczynskij (10/26/2016)
     */
    public void startSocketServer() {
        //Message msg = ConnectionService.getInstance().getHandler().obtainMessage();
        //msg.what = MSG_STARTSERVER;
        //ConnectionService.getInstance().getHandler().sendMessage(msg);
    }

    /**
     * Upon p2p connection available, non group owner start
     * socket channel connect to group owner.
     */
    /*
     * @author kuczynskij (10/26/2016)
     */
    public void startSocketClient(String hostname) {
        //Log.d(TAG, "startSocketClient : client connect to group owner : " + hostname);
        //Message msg = ConnectionService.getInstance().getHandler().obtainMessage();
        //msg.what = MSG_STARTCLIENT;
        //msg.obj = hostname;
        //ConnectionService.getInstance().getHandler().sendMessage(msg);
    }

    /*
     * @author kuczynskij (10/26/2016)
     */
    public WifiP2pDevice getConnectedPeer(){
        WifiP2pDevice peer = null;
        for(WifiP2pDevice d : mPeers ){
            if( d.status == WifiP2pDevice.CONNECTED)
                peer = d;
        }
        return peer;
    }

    /*
     * @author kuczynskij (10/26/2016)
     */
    public void clearMessages() {
        mMessageArray = new JSONArray();
    }

    /*
     * @author kuczynskij (10/26/2016)
     */
    public void setMyAddress(String addr){
        mMyAddress = addr;
    }
}
