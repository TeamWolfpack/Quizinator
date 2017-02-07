package com.seniordesign.wolfpack.quizinator.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Constants;
import com.seniordesign.wolfpack.quizinator.Database.Rules.Rules;
import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.WifiDirect.ConnectionService;
import com.seniordesign.wolfpack.quizinator.Fragments.PeerListFragment;
import com.seniordesign.wolfpack.quizinator.WifiDirect.WiFiDirectBroadcastReceiver;
import com.seniordesign.wolfpack.quizinator.WifiDirect.WifiDirectApp;

import static com.seniordesign.wolfpack.quizinator.WifiDirect.MessageCodes.MSG_DISCONNECT_FROM_ALL_PEERS;

public class HostGameActivity
        extends AppCompatActivity
        implements PeerListFragment.DeviceActionListener {

    private static final String TAG = "ACT_HG";
    private WifiDirectApp wifiDirectApp;

    private WifiP2pManager.ActionListener p2pActionListener =
            new WifiP2pManager.ActionListener() {
        @Override
        public void onSuccess() {
        }

        @Override
        public void onFailure(int reason) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_game);
        wifiDirectApp = (WifiDirectApp)getApplication();
        wifiDirectApp.mHomeActivity = this;
        wifiDirectApp.mIsServer = getIntent().getExtras().getBoolean("isServer");
        Log.d(TAG, "onCreate: is server - " + wifiDirectApp.mIsServer);
        if (wifiDirectApp.mIsServer) {
            setTitle(Constants.HOST_GAME);
        } else {
            setTitle(Constants.JOIN_GAME);
            findViewById(R.id.buttonsPanel).setVisibility(View.GONE);
        }
        // If service not started yet, start it.
        new Intent(this, ConnectionService.class);
        // start the connection service
        wifiDirectApp.mP2pMan = (WifiP2pManager) getSystemService(
                Context.WIFI_P2P_SERVICE);
        wifiDirectApp.mP2pChannel = wifiDirectApp.mP2pMan.initialize(
                this, getMainLooper(), null);
        wifiDirectApp.mReceiver = new WiFiDirectBroadcastReceiver();
        initiateDiscovery();
    }

    @Override
    public void onResume() {
        super.onResume();
        wifiDirectApp.onResume(TAG, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        wifiDirectApp.onPause(TAG);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        wifiDirectApp.onDestroy(TAG);
    }

    public void initiateDiscovery(){
        Log.d(TAG, "initiateDiscovery");
        if( !wifiDirectApp.isP2pEnabled() ){
            Toast.makeText(this, R.string.p2p_off_warning,
                    Toast.LENGTH_LONG).show();
            return;
        }
        final PeerListFragment peerListFragment =
                (PeerListFragment) getFragmentManager().
                        findFragmentById(R.id.frag_peer_list);
        peerListFragment.onInitiateDiscovery();
        discoverPeers(peerListFragment);
    }

    private void discoverPeers(final PeerListFragment fragment){
        Log.d(TAG, "discoverPeers");
        wifiDirectApp.mP2pMan.discoverPeers(wifiDirectApp.mP2pChannel,
                new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                discoverPeersGroupInfoOnSuccess();
            }
            @Override
            public void onFailure(int reasonCode) {
                fragment.clearPeers();
            }
        });
    }

    private void discoverPeersGroupInfoOnSuccess(){
        Log.d(TAG, "discoverPeersGroupInfoOnSuccess");
        if (wifiDirectApp.mIsServer) {
            wifiDirectApp.mP2pMan.requestGroupInfo(
                wifiDirectApp.mP2pChannel, p2pGroupInfoListener);
        }
    }

    private WifiP2pManager.GroupInfoListener p2pGroupInfoListener =
            new WifiP2pManager.GroupInfoListener() {
        @Override
        public void onGroupInfoAvailable(WifiP2pGroup group) {
            if (group != null)
                removeExistingGroup();
            else
                wifiDirectApp.mP2pMan.createGroup(
                        wifiDirectApp.mP2pChannel, p2pActionListener);
        }
    };

    public void removeExistingGroup(){
        Log.d(TAG, "removeExistingGroup: remove group and creates a new one");
        wifiDirectApp.mP2pMan.removeGroup(wifiDirectApp.mP2pChannel,
                removeExistingGroupActionListener);
    }

    private WifiP2pManager.ActionListener removeExistingGroupActionListener =
        new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                wifiDirectApp.mP2pMan.createGroup(
                        wifiDirectApp.mP2pChannel, p2pActionListener);
            }
            @Override
            public void onFailure(int reason) {
            }
        };

    /**
     * Process WIFI_P2P_THIS_DEVICE_CHANGED_ACTION intent, refresh
     * this device.
     */
    public void updateThisDevice(final WifiP2pDevice device){
        if(device != null){
            Log.d(TAG, "updateThisDevice: \n" +
                    "     device name: " + device.deviceName + "\n" +
                    "     device address: " + device.deviceAddress);
        }

        runOnUiThread(new Runnable() {
            @Override public void run() {
                PeerListFragment peerListFragment =
                        (PeerListFragment)getFragmentManager().
                                findFragmentById(R.id.frag_peer_list);
                if (peerListFragment != null) {
                    peerListFragment.updateThisDevice(device);
                }
            }
        });
    }

    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    public void resetData() {
        Log.d(TAG, "resetData: resetting the data");
        runOnUiThread(new Runnable() {
            @Override public void run() {
                PeerListFragment peerListFragment =
                        (PeerListFragment) getFragmentManager().
                                findFragmentById(R.id.frag_peer_list);
                if (peerListFragment != null) {
                    peerListFragment.clearPeers();
                    peerListFragment.dismissProgressDialog();
                }
            }
        });
    }

    /**
     * handle p2p connection available, update UI.
     */
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        Log.d(TAG, "onConnectionInfoAvailable: sending the info to " +
                "the fragment detail\n" +
                "info - " + info.toString() + "\n" +
                "group owner - " + info.isGroupOwner);
        runOnUiThread(new Runnable() {
            @Override public void run() {
                PeerListFragment peerListFragment =
                        (PeerListFragment) getFragmentManager().
                                findFragmentById(R.id.frag_peer_list);
                peerListFragment.onConnectionInfoAvailable(info);
            }
        });
    }

    /**
     * Update the device list fragment.
     */
    public void onPeersAvailable(final WifiP2pDeviceList peerList){
        Log.d(TAG, "onPeersAvailable: peer list available");
        runOnUiThread(new Runnable() {
            @Override public void run() {
                PeerListFragment peerListFragment =
                        (PeerListFragment) getFragmentManager().findFragmentById(R.id.frag_peer_list);
                peerListFragment.onPeersAvailable(wifiDirectApp.mPeers);  // use application cached list.

                for(WifiP2pDevice d : peerList.getDeviceList()){
                    if( d.status == WifiP2pDevice.FAILED ){
                        peerListFragment.dismissProgressDialog();
                    }
                }
            }
        });
    }

    /**
     * The channel to the framework(WiFi direct) has been disconnected.
     * This is diff than the p2p connection to group owner.
     */
    public void onChannelDisconnected() {
        Toast.makeText(this, "Severe! Channel is probably lost permanently. " +
                "Try Disable/Re-Enable P2P.", Toast.LENGTH_LONG).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("WiFi Direct down, please re-enable WiFi Direct")
                .setCancelable(true)
                .setPositiveButton("Re-enable WiFi Direct", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog info = builder.create();
        info.show();
    }

    public boolean startMultiplayerGamePlay(final Rules rules) {
        Log.d(TAG, "startMultiplayerGamePlay");
        if(!wifiDirectApp.mP2pConnected ){
            Toast.makeText(this, "You are not connected to anyone",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        runOnUiThread(new Runnable() {
            @Override public void run() {
                Intent i = wifiDirectApp.getLaunchActivityIntent(GamePlayActivity.class, null);
                    i.putExtra(Constants.RULES, new Gson().toJson(rules));
                    i.putExtra(Constants.GAME_MODE, false);
                startActivity(i);
            }
        });
        return true;
    }

    /**
     * user taps on peer from discovered list of peers, show this peer's detail.
     */
    @Override
    public void showDetails(WifiP2pDevice device) {
        Log.d(TAG, "updateSelectedDevice: device - " + device.toString());
        PeerListFragment peerListFragment = (PeerListFragment)
                getFragmentManager().findFragmentById(R.id.frag_peer_list);
        peerListFragment.updateSelectedDevice(device);
    }

    /**
     * User clicked connect button after discover peers.
     */
    @Override
    public void connect(WifiP2pConfig config) {
        Log.d(TAG, "connect: config(" + config.toString() + ")");
        // perform p2p connect upon users click the connect button.
        // after connection, manager request connection info
        if (wifiDirectApp.mIsServer) {
            connectIsServerCreateGroup();
        } else {
            wifiDirectApp.mP2pMan.connect(wifiDirectApp.mP2pChannel,
                    config, p2pActionListener);
        }
    }

    private void connectIsServerCreateGroup(){
        Log.d(TAG, "connectIsServerCreateGroup");
        wifiDirectApp.mP2pMan.requestGroupInfo(wifiDirectApp.mP2pChannel,
                new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup group) {
                if (group != null) {
                    Log.d(TAG, "group != null");
                } else {
                    Log.d(TAG, "group == null");
                    wifiDirectApp.mP2pMan.createGroup(
                            wifiDirectApp.mP2pChannel, p2pActionListener);
                }
            }
        });
    }

    @Override
    public void disconnect() {
        Log.d(TAG, "disconnect: disconnects from all peers and reset " +
                "the peer list fragment");
        final PeerListFragment peerListFragment = (PeerListFragment)
                getFragmentManager().findFragmentById(R.id.frag_peer_list);
        if (peerListFragment != null) {
            peerListFragment.clearPeers();
            peerListFragment.dismissProgressDialog();
        }
        wifiDirectApp.mP2pMan.removeGroup(wifiDirectApp.mP2pChannel, null);
    }

    /**
     * Button handler for the layout.
     */
    public boolean onGameSettingsButtonClicked(View v){
        Log.d(TAG, "startGameSettingsActivity: view(" + v.toString() + ")");
        if(!wifiDirectApp.mP2pConnected ){
            Toast.makeText(this, "You are not connected to anyone",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        runOnUiThread(new Runnable() {
            @Override public void run() {
                startActivity(wifiDirectApp.getLaunchActivityIntent(NewGameSettingsActivity.class, null));
            }
        });
        return true;
    }

    public void onConnectButtonClicked(View v){
        PeerListFragment peerListFragment =
                (PeerListFragment)getFragmentManager().
                        findFragmentById(R.id.frag_peer_list);
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = peerListFragment.getSelectedDevice().deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        // least inclination to be group owner.
            // 15 is highest group owner (host)
            // 0 is lowest (player)
        config.groupOwnerIntent = wifiDirectApp.isHost();
        peerListFragment.dismissProgressDialog();
        // perform p2p connect upon user click the connect button,
        // connect available handle when connection done.
        this.connect(config);
    }

    public void onDisconnectButtonClicked(View v){
        this.disconnect();
    }

    public boolean onDisconnectAllButtonClicked(View v){
        ConnectionService.sendMessage(MSG_DISCONNECT_FROM_ALL_PEERS, "");
        this.disconnect();
        finish();
        return true;
    }
}
