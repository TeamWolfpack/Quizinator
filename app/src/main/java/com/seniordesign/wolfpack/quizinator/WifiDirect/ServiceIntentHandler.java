package com.seniordesign.wolfpack.quizinator.WifiDirect;

import android.app.Service;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

class ServiceIntentHandler implements
        WifiP2pManager.PeerListListener,
        WifiP2pManager.ConnectionInfoListener {

    private static final String TAG = "SerInHnd";

    private WifiDirectApp wifiDirectApp;
    private Service mService;
    private static ConnectionManager mConnMan;
    private static WorkHandler mWorkHandler;
    private static Intent mIntent;

    ServiceIntentHandler(Service service,
                         ConnectionManager connMan,
                         WorkHandler workHandler,
                         Intent intent){
        mService = service;
        mWorkHandler = workHandler;
        mIntent = intent;
        mConnMan = connMan;
        wifiDirectApp = WifiDirectApp.getInstance();
        processIntent();
    }

    private void processIntent(){
        if (mIntent == null)
            return;
        String action = mIntent.getAction();
        if (action == null)
            return;
        switch (action) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                deviceWifiStateChangedAction(mIntent.getIntExtra(
                        WifiP2pManager.EXTRA_WIFI_STATE, -1),
                        mService, mWorkHandler);
                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                //find all peers
                deviceWifiPeersChangedAction();
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                //if select p2p server mode with create group, this
                // device will be group owner automatically
                if (WifiDirectApp.getInstance().mP2pMan == null)
                    return;
                deviceConnectionChangedAction();
                break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                //p2p connected, for client, this device changed to
                // connected first
                deviceDetailsHaveChanged();
                break;
            default:
                break;
        }
    }

    private static boolean deviceWifiStateChangedAction(int state,
                                            Service service,
                                            WorkHandler workHandler) {
        WifiDirectApp wifiDirectApp = WifiDirectApp.getInstance();
        if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            wifiDirectApp.mP2pChannel =
                    wifiDirectApp.mP2pMan.initialize(service,
                            workHandler.getLooper(), null);
            AppPreferences.setStringToPref(wifiDirectApp,
                    AppPreferences.PREF_NAME,
                    AppPreferences.P2P_ENABLED, "1");
            return true;
        } else {
            wifiDirectApp.mThisDevice = null;
            wifiDirectApp.mP2pChannel = null;
            wifiDirectApp.mPeers.clear();
            if (wifiDirectApp.mHomeActivity != null) {
                wifiDirectApp.mHomeActivity.updateThisDevice(null);
                wifiDirectApp.mHomeActivity.resetData();
            }
            AppPreferences.setStringToPref(wifiDirectApp,
                    AppPreferences.PREF_NAME,
                    AppPreferences.P2P_ENABLED,
                    "0");
            return false;
        }
    }

    private boolean deviceWifiPeersChangedAction() {
        if(wifiDirectApp.mManageActivity != null)
            wifiDirectApp.mManageActivity.validateAnswer(null);
        if (wifiDirectApp.mP2pMan != null && wifiDirectApp.mP2pChannel != null) {
            wifiDirectApp.mP2pMan.requestPeers(wifiDirectApp.mP2pChannel, this);
            return true;
        }
        return false;
    }

    /**
     * The callback of requestPeers upon
     * WIFI_P2P_PEERS_CHANGED_ACTION intent.
     *
     * NEEDED FOR PeerListListener
     */
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        Log.d(TAG, "onPeersAvailable");
        WifiDirectApp wifiDirectApp = WifiDirectApp.getInstance();
        wifiDirectApp.mPeers.clear();
        wifiDirectApp.mPeers.addAll(peerList.getDeviceList());
        if (wifiDirectApp.mP2pInfo != null &&
                wifiDirectApp.getConnectedPeer() != null &&
                wifiDirectApp.mP2pInfo.groupFormed &&
                wifiDirectApp.mP2pInfo.isGroupOwner) {
            wifiDirectApp.startSocketServer();
        }
        if (wifiDirectApp.mHomeActivity != null)
            wifiDirectApp.mHomeActivity.onPeersAvailable(peerList);
    }

    private boolean deviceConnectionChangedAction() {
        NetworkInfo networkInfo = mIntent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
        if (networkInfo.isConnected()) {
            Log.d(TAG, "deviceConnectionChangedAction: WIFI_P2P_CONNECTION_CHANGED_ACTION: p2p connected ");
            // Connected with the other device, request connection
            // info for group owner IP. Callback inside details fragment.
            wifiDirectApp.mP2pMan.requestConnectionInfo(wifiDirectApp.mP2pChannel, this);
            return true;
        } else {
            // It's a disconnect
            Log.d(TAG, "deviceConnectionChangedAction: WIFI_P2P_CONNECTION_CHANGED_ACTION: p2p " +
                    "disconnected, mP2pConnected = false..closeClient..");
            wifiDirectApp.mP2pConnected = false;
            wifiDirectApp.mP2pInfo = null;
            mConnMan.closeClient();
            if (wifiDirectApp.mHomeActivity != null)
                wifiDirectApp.mHomeActivity.resetData();
            if (wifiDirectApp.mGameplayActivity != null)
                wifiDirectApp.mGameplayActivity.endGamePlay();
            return false;
        }
    }

    private boolean deviceDetailsHaveChanged() {
        if(mIntent.hasExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)){
            wifiDirectApp.mThisDevice =
                    mIntent.getParcelableExtra(
                            WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            wifiDirectApp.mDeviceName =
                    wifiDirectApp.mThisDevice.deviceName;
            if (wifiDirectApp.mHomeActivity != null) {
                wifiDirectApp.mHomeActivity.updateThisDevice(
                        wifiDirectApp.mThisDevice);
                return true;
            }
        }
        return false;
    }

    /**
     * the callback of when the _Requested_ connectino info is available.
     * WIFI_P2P_CONNECTION_CHANGED_ACTION intent, requestConnectionInfo()
     *
     * NEEDED FOR ConnectionInfoListener
     */
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        WifiDirectApp wifiDirectApp = WifiDirectApp.getInstance();
        if (info.groupFormed && !info.isGroupOwner && info.groupOwnerAddress != null)
            wifiDirectApp.startSocketClient(info.groupOwnerAddress.getHostAddress());
        wifiDirectApp.mP2pConnected = true;
        wifiDirectApp.mP2pInfo = info;
    }
}