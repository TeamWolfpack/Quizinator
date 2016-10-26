package com.seniordesign.wolfpack.quizinator.WifiDirect;

import android.app.Application;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import com.seniordesign.wolfpack.quizinator.Activities.HostGameActivity;
import com.seniordesign.wolfpack.quizinator.Activities.MainMenuActivity;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static com.seniordesign.wolfpack.quizinator.WifiDirect.Constants.MSG_STARTSERVER;

/**
 * Created by leonardj on 10/24/2016.
 */
public class WifiDirectApp extends Application {

    private static final String TAG = "PTP_APP";

    public WifiP2pManager mP2pMan;
    public WifiP2pManager.Channel mP2pChannel;
    public boolean mP2pConnected;
    public String mMyAddr;
    public String mDeviceName; // the p2p name that is configurated from UI.

    public WifiP2pDevice mThisDevice;
    public WifiP2pInfo mP2pInfo;  // set when connection info available, reset when WIFI_P2P_CONNECTION_CHANGED_ACTION

    public boolean mIsServer;

    public HostGameActivity mHomeActivity;
    public List<WifiP2pDevice> mPeers = new ArrayList<>();  // update on every peers available
    JSONArray mMessageArray = new JSONArray();		// limit to the latest 50 messages

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * whether p2p is enabled in this device.
     * my bcast listener always gets enable/disable intent and persist to shared pref
     */
    public boolean isP2pEnabled() {
        String state = AppPreferences.getStringFromPref(this, AppPreferences.PREF_NAME, AppPreferences.P2P_ENABLED);
        return state != null && "1".equals(state.trim());
    }

    /**
     * upon p2p connection available, group owner start server socket channel
     * start socket server and select monitor the socket
     */
    public void startSocketServer() {
        //Message msg = ConnectionService.getInstance().getHandler().obtainMessage();
        //msg.what = MSG_STARTSERVER;
        //ConnectionService.getInstance().getHandler().sendMessage(msg);
    }

    /**
     * upon p2p connection available, non group owner start socket channel connect to group owner.
     */
    public void startSocketClient(String hostname) {
        //Log.d(TAG, "startSocketClient : client connect to group owner : " + hostname);
        //Message msg = ConnectionService.getInstance().getHandler().obtainMessage();
        //msg.what = MSG_STARTCLIENT;
        //msg.obj = hostname;
        //ConnectionService.getInstance().getHandler().sendMessage(msg);
    }

    /**
     * check whether there exists a connected peer.
     */
    public WifiP2pDevice getConnectedPeer(){
        WifiP2pDevice peer = null;
        for(WifiP2pDevice d : mPeers ){
            if( d.status == WifiP2pDevice.CONNECTED){
                peer = d;
            }
        }
        return peer;
    }

    public void clearMessages() {
        mMessageArray = new JSONArray();
    }

    public void setMyAddr(String addr){
        mMyAddr = addr;
    }
}
