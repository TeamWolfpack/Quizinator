package com.seniordesign.wolfpack.quizinator.wifiDirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.ArrayList;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver
        implements WifiP2pManager.PeerListListener,
        WifiP2pManager.ConnectionInfoListener {

    private static final String TAG = "BroRecv";

    public WiFiDirectBroadcastReceiver(){
        super();
    }

    /**
     * Processes the intent received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        if (intent == null)
            return;
        String action = intent.getAction();
        if (action == null)
            return;
        switch (action) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                deviceWifiStateChangedAction(intent.getIntExtra(
                        WifiP2pManager.EXTRA_WIFI_STATE, -1));
                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                deviceWifiPeersChangedAction();
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                if (WifiDirectApp.getInstance().mP2pMan == null)
                    return;
                deviceConnectionChangedAction(intent);
                break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                deviceDetailsHaveChanged(intent);
                break;
            default:
                break;
        }
    }

    /**
     * Processes device's wifi state change actions.
     * @param state device state
     * @return true if device has p2p enabled
     */
    private static boolean deviceWifiStateChangedAction(int state) {
        Log.d(TAG, "deviceWifiStateChangedAction");
        WifiDirectApp wifiDirectApp = WifiDirectApp.getInstance();
        if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            if (ConnectionService.getInstance().mWorkHandler == null) {
                return false;
            }
            wifiDirectApp.mP2pChannel =
                    wifiDirectApp.mP2pMan.initialize(
                            ConnectionService.getInstance(),
                            ConnectionService.getInstance().mWorkHandler.getLooper(),
                            null);
            return true;
        } else {
            wifiDirectApp.mThisDevice = null;
            wifiDirectApp.mP2pChannel = null;
            wifiDirectApp.mPeers.clear();
            if (wifiDirectApp.mHomeActivity != null) {
                wifiDirectApp.mHomeActivity.updateThisDevice(null);
                wifiDirectApp.mHomeActivity.resetData();
            }
            return false;
        }
    }

    /**
     * Requests to find more peers.
     * @return true if device peers request update
     */
    private boolean deviceWifiPeersChangedAction() {
        Log.d(TAG, "deviceWifiPeersChangedAction");
        WifiDirectApp wifiDirectApp = WifiDirectApp.getInstance();
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
        if(wifiDirectApp.mManageActivity != null)
            findDisconnectedPlayers(peerList);
        wifiDirectApp.mPeers.clear();
        wifiDirectApp.mPeers.addAll(peerList.getDeviceList());
        if (wifiDirectApp.mP2pInfo != null &&
                wifiDirectApp.getConnectedPeers().size() > 0 &&
                wifiDirectApp.mP2pInfo.groupFormed &&
                wifiDirectApp.mP2pInfo.isGroupOwner) {
            wifiDirectApp.startSocketServer();
        }
        if (wifiDirectApp.mHomeActivity != null)
            wifiDirectApp.mHomeActivity.onPeersAvailable();
    }

    private void findDisconnectedPlayers(WifiP2pDeviceList peerList){
        ArrayList<WifiP2pDevice> currentPeers = new ArrayList<>();
        WifiDirectApp wifiDirectApp = WifiDirectApp.getInstance();
        for (WifiP2pDevice d : wifiDirectApp.mPeers) {
            if (d.status == WifiP2pDevice.CONNECTED) {
                currentPeers.add(d);
            }
        }
        ArrayList<WifiP2pDevice> updatedPeers = new ArrayList<>();
        for (WifiP2pDevice d : peerList.getDeviceList()) {
            if (d.status == WifiP2pDevice.CONNECTED) {
                updatedPeers.add(d);
            }
        }
        if(currentPeers.size() > updatedPeers.size()){
            //handle disconnects
//                for(int i = 0; i < currentPeers.size(); ++i){
//                    for(int j = 0; j < updatedPeers.size(); ++j){
//                        if(currentPeers.get(i).deviceAddress.equals(updatedPeers.get(j).deviceAddress)){
//
//                        }
//                    }
//                }
            currentPeers.removeAll(updatedPeers);

            StringBuilder sb = new StringBuilder();
            for(WifiP2pDevice name : currentPeers){
                if(sb.toString().length() > 0)
                    sb.append("\n");
                sb.append(name.deviceName).append(" has left the game");
            }
            ConnectionService.sendMessage(MessageCodes.MSG_PEER_HAS_LEFT, sb.toString());
        }
    }

    /**
     * If P2P server mode is selected with create group, this device
     * will be group owner automatically.
     * @return true if p2p network is connected
     */
    private boolean deviceConnectionChangedAction(Intent intent) {
        Log.d(TAG, "deviceConnectionChangedAction");
        WifiDirectApp wifiDirectApp = WifiDirectApp.getInstance();
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
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
            ConnectionService.getInstance().mConnMan.closeClient();
            if (wifiDirectApp.mHomeActivity != null) {
                wifiDirectApp.mHomeActivity.resetData();
                if (!wifiDirectApp.mIsServer)
                    wifiDirectApp.mHomeActivity.discoverPeers();
            }
            if (wifiDirectApp.mGameplayActivity != null)
                wifiDirectApp.mGameplayActivity.endGamePlay();
            return false;
        }
    }

    /**
     * P2P connection connected.
     * For the client, this device changed to connected first.
     * @return true if your device has updated status
     */
    private boolean deviceDetailsHaveChanged(Intent intent) {
        Log.d(TAG, "deviceDetailsHaveChanged");
        WifiDirectApp wifiDirectApp = WifiDirectApp.getInstance();
        if(intent.hasExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)){
            wifiDirectApp.mThisDevice =
                    intent.getParcelableExtra(
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
        Log.d(TAG, "onConnectionInfoAvailable");
        WifiDirectApp wifiDirectApp = WifiDirectApp.getInstance();
        if (info.groupFormed && !info.isGroupOwner && info.groupOwnerAddress != null)
            wifiDirectApp.startSocketClient(info.groupOwnerAddress.getHostAddress());
        wifiDirectApp.mP2pConnected = true;
        wifiDirectApp.mP2pInfo = info;
    }
}
