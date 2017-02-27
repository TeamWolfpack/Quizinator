package com.seniordesign.wolfpack.quizinator.WifiDirect;

import java.util.ArrayList;
import java.util.List;

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

import com.google.gson.Gson;

import com.seniordesign.wolfpack.quizinator.Activities.MainMenuActivity;
import com.seniordesign.wolfpack.quizinator.Messages.QuizMessage;

public class ConnectionService extends Service implements ChannelListener {

    private static final String TAG = "ConnServ";

    private static ConnectionService mInstance = null;

    private WorkHandler mWorkHandler;
    private MessageHandler mHandler;

    boolean retryChannel = false;

    WifiDirectApp wifiDirectApp;
//    MainMenuActivity mActivity;
    ConnectionManager mConnMan;

    @Override
    public void onCreate() {
        super.onCreate();
        if (mInstance != null) {
            return;
        }
        mInstance = this;
        initializeConnectionService();
    }

    public static ConnectionService getInstance() {
        return mInstance;
    }

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

    @Override
    public IBinder onBind(Intent arg0) {
        return new Binder();
    }

    public Handler getHandler() {
        return mHandler;
    }

    public static boolean sendMessage(int code, String message) {
        Message result = getInstance().getHandler().obtainMessage();
            result.what = code;
            result.obj = message;
        return getInstance().getHandler().sendMessage(result);
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
