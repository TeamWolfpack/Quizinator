package com.seniordesign.wolfpack.quizinator.WifiDirect;

import static com.seniordesign.wolfpack.quizinator.WifiDirect.Constants.*;

import java.nio.channels.SocketChannel;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Activities.MainMenuActivity;
import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.Rules.Rules;

/**
 * @creation 10/26/2016
 */
public class ConnectionService
        extends Service implements
        ChannelListener,
        PeerListListener,
        ConnectionInfoListener {  // callback of requestPeers{

    private static final String TAG = "ConnServ";

    private static ConnectionService _sinstance = null;

    private WorkHandler mWorkHandler;
    private MessageHandler mHandler;

    boolean retryChannel = false;

    WifiDirectApp mApp;
    MainMenuActivity mActivity; // shall I use weak reference here ?
    ConnectionManager mConnMan;

    private void _initialize() {
        if (_sinstance != null) {
            return;
        }

        _sinstance = this;
        mWorkHandler = new WorkHandler(TAG);
        mHandler = new MessageHandler(mWorkHandler.getLooper());

        mApp = (WifiDirectApp) getApplication();
        mApp.mP2pMan = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mApp.mP2pChannel = mApp.mP2pMan.initialize(this, mWorkHandler.getLooper(), null);

        mConnMan = new ConnectionManager(this);
    }

    public static ConnectionService getInstance() {
        return _sinstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _initialize();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        _initialize();
        processIntent(intent);
        return START_STICKY;
    }

    /*
     * @author kuczynskij (10/26/2016)
     */
    private void processIntent(Intent intent) {
        if (intent == null)
            return;
        String action = intent.getAction();
        if (action == null)
            return;
        switch (action) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                deviceWifiStateChangedAction(state);
                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                //find all peers
                deviceWifiPeersChangedAction();
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                //if select p2p server mode with create group, this
                // device will be group owner automatically
                if (mApp.mP2pMan == null)
                    return;
                deviceConnectionChangedAction(intent);
                break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                //p2p connected, for client, this device
                // changed to connected first
                deviceDetailsHaveChanged(intent);
                break;
            default:
                break;
        }
    }

    /*
     * @author kuczynskij (10/26/2016)
     */
    private boolean deviceWifiStateChangedAction(int state) {
        if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            // Wifi Direct mode is enabled
            mApp.mP2pChannel = mApp.mP2pMan.initialize(this,
                    mWorkHandler.getLooper(), null);
            AppPreferences.setStringToPref(mApp,
                    AppPreferences.PREF_NAME, AppPreferences.P2P_ENABLED, "1");
            return true;
        } else {
            mApp.mThisDevice = null;    // reset this device status
            mApp.mP2pChannel = null;
            mApp.mPeers.clear();
            if (mApp.mHomeActivity != null) {
                mApp.mHomeActivity.updateThisDevice(null);
                mApp.mHomeActivity.resetData();
            }
            AppPreferences.setStringToPref(mApp,
                    AppPreferences.PREF_NAME, AppPreferences.P2P_ENABLED, "0");
            return false;
        }
    }

    /*
     * @author kuczynskij (10/26/2016)
     */
    private boolean deviceWifiPeersChangedAction() {
        // a list of peers are available after discovery,
        // use PeerListListener to collect request available
        // peers from the wifi p2p manager. This is an
        // asynchronous call and the calling activity is
        // notified with callback on
        // PeerListListener.onPeersAvailable()
        if (mApp.mP2pMan != null) {
            mApp.mP2pMan.requestPeers(mApp.mP2pChannel, this);
            return true;
        }
        return false;
    }

    /*
     * @author kuczynskij (10/26/2016)
     */
    private boolean deviceConnectionChangedAction(Intent intent) {
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
        if (networkInfo.isConnected()) {
            Log.d(TAG, "processIntent: WIFI_P2P_CONNECTION_CHANGED_ACTION: p2p connected ");
            // Connected with the other device, request connection
            // info for group owner IP. Callback inside details fragment.
            mApp.mP2pMan.requestConnectionInfo(mApp.mP2pChannel, this);
            return true;
        } else {
            // It's a disconnect
            Log.d(TAG, "processIntent: WIFI_P2P_CONNECTION_CHANGED_ACTION: p2p " +
                    "disconnected, mP2pConnected = false..closeClient..");
            mApp.mP2pConnected = false;
            mApp.mP2pInfo = null;   // reset connection info
            // after connection done.
            mConnMan.closeClient();
            if (mApp.mHomeActivity != null)
                mApp.mHomeActivity.resetData();
            return false;
        }
    }

    /*
     * @author kuczynskij (10/26/2016)
     */
    private boolean deviceDetailsHaveChanged(Intent intent) {
        mApp.mThisDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
        mApp.mDeviceName = mApp.mThisDevice.deviceName;
        if (mApp.mHomeActivity != null) {
            mApp.mHomeActivity.updateThisDevice(mApp.mThisDevice);
            return true;
        }
        return false;
    }

    /**
     * The channel to the framework Wifi P2p has been disconnected.
     * Could try re-initializing.
     */
    @Override
    public void onChannelDisconnected() {
        if (!retryChannel) {
            mApp.mP2pChannel = mApp.mP2pMan.initialize(this, mWorkHandler.getLooper(), null);
            if (mApp.mHomeActivity != null) {
                mApp.mHomeActivity.resetData();
            }
            retryChannel = true;
        } else {
            if (mApp.mHomeActivity != null) {
                mApp.mHomeActivity.onChannelDisconnected();
            }
            stopSelf();
        }
    }

    /**
     * The callback of requestPeers upon
     * WIFI_P2P_PEERS_CHANGED_ACTION intent.
     */
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        mApp.mPeers.clear();
        mApp.mPeers.addAll(peerList.getDeviceList());
        WifiP2pDevice connectedPeer = mApp.getConnectedPeer();
        if (connectedPeer != null) {
            //PTPLog.d(TAG, "onPeersAvailable : exist connected peer : " + connectedPeer.deviceName);
        }
        if (mApp.mP2pInfo != null && connectedPeer != null) {
            if (mApp.mP2pInfo.groupFormed && mApp.mP2pInfo.isGroupOwner) {
                mApp.startSocketServer();
            } else if (mApp.mP2pInfo.groupFormed && connectedPeer != null) {
                // XXX client path goes to connection info available after connection established.
                // PTPLog.d(TAG, "onConnectionInfoAvailable: device is client, connect to group owner: startSocketClient ");
                // mApp.startSocketClient(mApp.mP2pInfo.groupOwnerAddress.getHostAddress());
            }
        }
        if (mApp.mHomeActivity != null) {
            mApp.mHomeActivity.onPeersAvailable(peerList);
        }
    }

    /**
     * the callback of when the _Requested_ connectino info is available.
     * WIFI_P2P_CONNECTION_CHANGED_ACTION intent, requestConnectionInfo()
     */
    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        Log.d(TAG, "onConnectionInfoAvailable: " + info.groupOwnerAddress.getHostAddress());
        if (info.groupFormed && info.isGroupOwner) {
            // XXX server path goes to peer connected.
            //new FileServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text)).execute();
            //PTPLog.d(TAG, "onConnectionInfoAvailable: device is groupOwner: startSocketServer ");
            // mApp.startSocketServer();
        } else if (info.groupFormed) {
            mApp.startSocketClient(info.groupOwnerAddress.getHostAddress());
        }
        mApp.mP2pConnected = true;
        mApp.mP2pInfo = info;   // connection info available
    }

    private void enableStartChatActivity() {
        if (mApp.mHomeActivity != null) {
            mApp.mHomeActivity.onConnectionInfoAvailable(mApp.mP2pInfo);
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public Handler getHandler() {
        return mHandler;
    }

    /**
     * message handler looper to handle all the msg sent to location manager.
     */
    final class MessageHandler extends Handler {
        public MessageHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            processMessage(msg);
        }
    }

    /**
     * The main message process loop.
     */
    private void processMessage(Message msg) {

        switch (msg.what) {
            case MSG_NULL:
                break;
            case MSG_REGISTER_ACTIVITY:
                onActivityRegister((MainMenuActivity) msg.obj, msg.arg1);
                break;
            case MSG_STARTSERVER:
                if (mConnMan.startServerSelector() >= 0) {
                    enableStartChatActivity();
                }
                break;
            case MSG_STARTCLIENT:
                if (mConnMan.startClientSelector((String) msg.obj) >= 0) {
                    enableStartChatActivity();
                }
                break;
            case MSG_NEW_CLIENT:
                mConnMan.onNewClient((SocketChannel) msg.obj);
                break;
            case MSG_FINISH_CONNECT:
                mConnMan.onFinishConnect((SocketChannel) msg.obj);
                break;
            case MSG_PULLIN_DATA:
                onPullInData((SocketChannel)msg.obj, msg.getData());
                break;
            case MSG_PUSHOUT_DATA:
                onPushOutData((String) msg.obj);
                break;
            case MSG_SEND_RULES_ACTIVITY:
                pushAllRulesOut((String) msg.obj);
                break;
            case MSG_SEND_CARD_ACTIVITY:
                pushCardOut((String) msg.obj);
                break;
            case MSG_SELECT_ERROR:
                mConnMan.onSelectorError();
                break;
            case MSG_BROKEN_CONN:
                mConnMan.onBrokenConnection((SocketChannel) msg.obj);
                break;
            default:
                break;
        }
    }

    /**
     * Register the activity that uses this service.
     */
    private void onActivityRegister(MainMenuActivity activity,
                                    int register) {
        Log.d(TAG, "onActivityRegister : activity register " +
                "itself to service : " + register);
        if (register == 1) {
            mActivity = activity;
        } else {
            // set to null explicitly to avoid mem leak.
            mActivity = null;
        }
    }

    /**
     * Handle data push out request.
     * If the sender is the server, pub to all client.
     * If the sender is client, only can send to the server.
     */
    private void onPushOutData(String data) {
        Log.d(TAG, "onPushOutData : " + data);
        mConnMan.pushOutData(data);
    }

    /*
     * @author leonardj (10/31/2016)
     */
    private void pushCardOut(String data){
        Log.d(TAG, "pushCardOut: " + data);
        mConnMan.pushOutData(MSG_SEND_CARD_ACTIVITY + data);
    }

    /*
     * @author kuczynskij (11/01/2016)
     * @author leonardj (10/31/2016)
     */
    private void pushAllRulesOut(String data){
        Log.d(TAG, "pushAllRulesOut: " + data);
        //anything we may need to the rules string
        mConnMan.pushOutData(MSG_SEND_RULES_ACTIVITY + data);
    }

    /**
     * service handle data in come from socket channel
     */
    private String onPullInData(SocketChannel schannel, Bundle b){
        String data = b.getString("DATA");
        Log.d(TAG, "onDataIn : recvd msg : " + data);
        mConnMan.onDataIn(schannel, data);  // pub to all client if this device is server.
        int code;
        try {
            code = Integer.parseInt(data.substring(0, 4));
            data = data.substring(4);
        }catch(NumberFormatException nfe){
            code = -1;
        }
        Gson g = new Gson();
        switch(code){
            case MSG_SEND_RULES_ACTIVITY:
                Rules r = g.fromJson(data, Rules.class);
                mApp.mHomeActivity.loadRuleInActivity(r);
                break;
            case MSG_TEST_HI_JIMMY:
                data += " this works yay";
                break;
            case MSG_SEND_CARD_ACTIVITY:
                Card card = g.fromJson(data, Card.class);
                mApp.mHomeActivity.loadCardInActivity(card);
                break;
        }
//        MessageRow row = MessageRow.parseMessageRow(data);
//        // now first add to app json array
//        mApp.shiftInsertMessage(row);
//        // add to activity if it is on focus.
//        showInActivity(row);
        return data;
    }

    /**
     * Sync call to send data using conn man's channel,
     * as conn man now is blocking on select.
     */
    public int connectionSendData(String jsonstring) {
        Log.d(TAG, "connectionSendData : " + jsonstring);
        new SendDataAsyncTask(mConnMan, jsonstring).execute();
        return 0;
    }

    /**
     * Write data in an async task to avoid
     * NetworkOnMainThreadException.
     */
    public class SendDataAsyncTask
            extends AsyncTask<Void, Void, Integer> {
        private String data;
        private ConnectionManager connman;

        public SendDataAsyncTask(ConnectionManager conn,
                                 String jsonstring) {
            connman = conn;
            data = jsonstring;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            return connman.pushOutData(data);
        }

        @Override
        protected void onPostExecute(Integer result) {
            Log.d(TAG, "SendDataAsyncTask : onPostExecute:  " +
                    data + " len: " + result);
        }
    }

    public static String getDeviceStatus(int deviceStatus) {
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown = " + deviceStatus;
        }
    }
}
