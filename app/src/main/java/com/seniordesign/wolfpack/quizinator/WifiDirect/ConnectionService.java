package com.seniordesign.wolfpack.quizinator.WifiDirect;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 *
 * @url -> https://developer.android.com/reference/android/app/Service.html
 */
public class ConnectionService extends Service implements ChannelListener {

    private static final String TAG = "ConnServ";

    private static ConnectionService mInstance = null;

    private WorkHandler mWorkHandler;
    private MessageHandler mHandler;

    boolean retryChannel = false;

    WifiDirectApp wifiDirectApp;
    ConnectionManager mConnMan;

    /**
     * Simple singleton.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        if (mInstance != null) {
            return;
        }
        mInstance = this;
        initializeConnectionService();
    }

    /**
     * Returns an instance of this service.
     * @return the connection service
     */
    public static ConnectionService getInstance() {
        return mInstance;
    }

    /**
     * Initializes a new Connection Service.
     */
    private void initializeConnectionService() {
        Log.d(TAG, "initializeConnectionService");
        mWorkHandler = new WorkHandler(TAG);
        mHandler = new MessageHandler(mWorkHandler.getLooper(), mConnMan);
        mConnMan = new ConnectionManager(this);
        wifiDirectApp = (WifiDirectApp) getApplication();
        wifiDirectApp.mP2pMan = (WifiP2pManager)
                getSystemService(Context.WIFI_P2P_SERVICE);
        wifiDirectApp.mP2pChannel =
                wifiDirectApp.mP2pMan.initialize(this,
                        mWorkHandler.getLooper(), null);
    }

    /**
     * Creates new Connection Service and handles the intent.
     * @param intent to be processed
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initializeConnectionService();
        new ServiceIntentHandler(this, mConnMan, mWorkHandler, intent);
        return START_STICKY;
    }

    /**
     * The channel to the framework Wifi P2p has been disconnected.
     * Could try re-initializing.
     */
    @Override
    public void onChannelDisconnected() {
        if (!retryChannel) {
            wifiDirectApp.mP2pChannel =
                    wifiDirectApp.mP2pMan.initialize(this,
                            mWorkHandler.getLooper(), null);
            if (wifiDirectApp.mHomeActivity != null)
                wifiDirectApp.mHomeActivity.resetData();
            retryChannel = true;
        } else {
            if (wifiDirectApp.mHomeActivity != null)
                wifiDirectApp.mHomeActivity.onChannelDisconnected();
            stopSelf();
        }
    }

    /**
     * Creates a new Binder.
     * NEEDED FOR Service to be inherited
     * @param intent *unimplemented* update intent of service
     * @return new Binder object
     */
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    /**
     * Returns the MessageHandler so that other classes can read messages.
     */
    public Handler getHandler() {
        //TODO -> external classes call this, they should not be able to
        return mHandler;
    }

    /**
     * Sends message(s) to peer(s).
     */
    public static boolean sendMessage(int code, String message) {
        //TODO -> external classes call this, they should not be able to
        Message result = getInstance().getHandler().obtainMessage();
            result.what = code;
            result.obj = message;
        return getInstance().getHandler().sendMessage(result);
    }


    /**
     * Returns status of device.
     * Used by Peer List label's.
     * @param deviceStatus numerical value of device status
     * @return string device status label
     */
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
