package com.seniordesign.wolfpack.quizinator.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.seniordesign.wolfpack.quizinator.Fragments.DeviceDetailFragment;
import com.seniordesign.wolfpack.quizinator.Fragments.DeviceListFragment;
import com.seniordesign.wolfpack.quizinator.WifiDirect.WiFiDirectBroadcastReceiver;
import com.seniordesign.wolfpack.quizinator.WifiDirect.WifiDirectApp;

public class HostGameActivity
        extends AppCompatActivity
        implements DeviceListFragment.DeviceActionListener {

    private static final String TAG = "ACT_HG";

    //private Rules rulesForGame;

    private WifiDirectApp wifiDirectApp;

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
            findViewById(R.id.start_game_settings).setVisibility(View.GONE);
        }

        //TODO -> remove the instantiation of serviceIntent if left unused, still create object
        // If service not started yet, start it.
        Intent serviceIntent =
                new Intent(this, ConnectionService.class);
        // start the connection service

        wifiDirectApp.mP2pMan = (WifiP2pManager) getSystemService(
                Context.WIFI_P2P_SERVICE);
        wifiDirectApp.mP2pChannel = wifiDirectApp.mP2pMan.initialize(
                this, getMainLooper(), null);
        wifiDirectApp.mReceiver = new WiFiDirectBroadcastReceiver(
                wifiDirectApp, this);

        initiateDiscovery();
    }

    /*
     *  onStart is called before onResume when resuming the activity.
     *  So by making this a different method, it is only called onCreate.
     */
    private void initiateDiscovery(){
        if( !wifiDirectApp.isP2pEnabled() ){
            Toast.makeText(this, R.string.p2p_off_warning, Toast.LENGTH_LONG).show();
            Log.d(TAG, "onStart: P2P is off - " + R.string.p2p_off_warning);
            return;
        }
        Log.d(TAG, "onStart: P2P enabled - " + wifiDirectApp.isP2pEnabled());

        final DeviceListFragment fragment =
                (DeviceListFragment)getFragmentManager().findFragmentById(R.id.frag_list);
        fragment.onInitiateDiscovery();

        if (wifiDirectApp.mP2pMan == null) {
            Log.d(TAG, "mP2p manager is null");
        } else if (wifiDirectApp.mP2pChannel == null) {
            Log.d(TAG, "mP2p channel is null");
        }

        discoverPeers(fragment);
    }

    private void discoverPeers(final DeviceListFragment fragment){
        wifiDirectApp.mP2pMan.discoverPeers(wifiDirectApp.mP2pChannel,
                new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                if (wifiDirectApp.mIsServer) {
                    wifiDirectApp.mP2pMan.requestGroupInfo(
                            wifiDirectApp.mP2pChannel,
                            new WifiP2pManager.GroupInfoListener() {
                        @Override
                        public void onGroupInfoAvailable(WifiP2pGroup group) {
                            if (group != null)
                                removeExistingGroup();
                            else
                                createNewGroup();
                        }
                    });
                } else {
                    Log.d(TAG, "onStart: you are a client");
                }
            }

            @Override
            public void onFailure(int reasonCode) {
                fragment.clearPeers();
                Log.d(TAG, "onStart: connection failed due to discoverPeers failed, reason: " + reasonCode);
            }
        });
    }

    private void createNewGroup(){
        wifiDirectApp.mP2pMan.createGroup(wifiDirectApp.mP2pChannel,
                new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "createNewGroup: createGroup Success");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "createNewGroup: createGroup Fail: " + reason);
            }
        });
    }

    private void removeExistingGroup(){
        wifiDirectApp.mP2pMan.removeGroup(wifiDirectApp.mP2pChannel,
                new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "removeExistingGroup: removeGroup Success");

                wifiDirectApp.mP2pMan.createGroup(wifiDirectApp.mP2pChannel,
                        new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "removeExistingGroup: createGroup Success");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.d(TAG, "removeExistingGroup: createGroup failed: " + reason);
                    }
                });
            }
            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "removeExistingGroup: removeGroup failed: " + reason);
            }
        });
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

    /**
     * Process WIFI_P2P_THIS_DEVICE_CHANGED_ACTION intent, refresh this device.
     */
    public void updateThisDevice(final WifiP2pDevice device){
//        Log.d(TAG, "updateThisDevice: \n" +
//                "     device name: " + device.deviceName + "\n" +
//                "     device address: " + device.deviceAddress);

        runOnUiThread(new Runnable() {
            @Override public void run() {
                DeviceListFragment fragment =
                        (DeviceListFragment)getFragmentManager().
                                findFragmentById(R.id.frag_list);
                if (fragment != null && device != null) {
                    fragment.updateThisDevice(device);
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
                DeviceListFragment fragmentList =
                        (DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list);
                DeviceDetailFragment fragmentDetails =
                        (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
                if (fragmentList != null) {
                    Log.d(TAG, "resetData: clearing peer list in fragment list");
                    fragmentList.clearPeers();
                }
                if (fragmentDetails != null) {
                    Log.d(TAG, "resetData: resetting view in fragment detail");
                    fragmentDetails.resetViews();
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
                DeviceDetailFragment fragmentDetails =
                        (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
                fragmentDetails.onConnectionInfoAvailable(info);
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
                DeviceListFragment fragmentList =
                        (DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list);
                    fragmentList.onPeersAvailable(wifiDirectApp.mPeers);  // use application cached list.
                DeviceDetailFragment fragmentDetails =
                        (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);

                for(WifiP2pDevice d : peerList.getDeviceList()){
                    if( d.status == WifiP2pDevice.FAILED ){
                        fragmentDetails.resetViews();
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

    /*
     * @author leonardj (11/4/16)
     */
    public boolean startMultiplayerGamePlay(final Rules rules) {
        Log.d(TAG, "startMultiplayerGamePlay");
        if(!wifiDirectApp.mP2pConnected ){
            Toast.makeText(this, "You are not connected to anyone",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        runOnUiThread(new Runnable() {
            @Override public void run() {
                Intent i = wifiDirectApp.
                        getLaunchActivityIntent(
                                GamePlayActivity.class, null);
                    i.putExtra(Constants.RULES, new Gson().toJson(rules));
                    i.putExtra(Constants.GAME_MODE,false);
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
        Log.d(TAG, "showDetails: device - " + device.toString());
        DeviceDetailFragment fragment = (DeviceDetailFragment)
                getFragmentManager().findFragmentById(R.id.frag_detail);
        fragment.showDetails(device);
    }

    @Override
    public void cancelDisconnect() {
        /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (wifiDirectApp.mP2pMan != null) {
            final DeviceListFragment fragment = (DeviceListFragment)
                    getFragmentManager().findFragmentById(R.id.frag_list);
            if (fragment.getDevice() == null || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                disconnect();
            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE || fragment.getDevice().status == WifiP2pDevice.INVITED) {
                wifiDirectApp.mP2pMan.cancelConnect(wifiDirectApp.mP2pChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(HostGameActivity.this,
                                "Aborting connection", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(HostGameActivity.this,
                                "cancelConnect: request failed. " +
                                        "Please try again.. ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
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
            connectIsNotServerActionListener(config);
        }
    }

    private void connectIsServerCreateGroup(){
        wifiDirectApp.mP2pMan.requestGroupInfo(wifiDirectApp.mP2pChannel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup group) {
                if (group != null) {
                    Log.d(TAG, "group != null");
                } else {
                    Log.d(TAG, "group == null");
                    wifiDirectApp.mP2pMan.createGroup(wifiDirectApp.mP2pChannel,
                        new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "createGroup Success");
                            }
                            @Override
                            public void onFailure(int reason) {
                                Log.d(TAG, "createGroup Fail: " + reason);
                            }
                        });
                }
            }
        });
    }

    private void connectIsNotServerActionListener(WifiP2pConfig config){
        wifiDirectApp.mP2pMan.connect(wifiDirectApp.mP2pChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                Log.d(TAG, "Client: Connect success...");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Client: Connect failed. Retry.");
            }
        });
    }

    @Override
    public void disconnect() {
        Log.d(TAG, "disconnect()");
        final DeviceDetailFragment fragment = (DeviceDetailFragment)
                getFragmentManager().findFragmentById(R.id.frag_detail);
        if (fragment != null) {
            Log.d(TAG, "disconnect: resetting the detail fragment");
            fragment.resetViews();
        }
        wifiDirectApp.mP2pMan.removeGroup(wifiDirectApp.mP2pChannel,
                new WifiP2pManager.ActionListener() {
            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "disconnect failed..." + reasonCode);
            }
            @Override
            public void onSuccess() {
                Log.d(TAG, "disconnect: resetting the detail fragment view to GONE");
                fragment.getView().setVisibility(View.GONE);
            }
        });


    }

    public boolean startGameSettingsActivity(View v){
        Log.d(TAG, "startGameSettingsActivity: view(" + v.toString() + ")");
        if(!wifiDirectApp.mP2pConnected ){
            Toast.makeText(this, "You are not connected to anyone",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        runOnUiThread(new Runnable() {
            @Override public void run() {
                Intent i = wifiDirectApp.
                        getLaunchActivityIntent(
                                NewGameSettingsActivity.class, null);
                startActivity(i);
            }
        });
        return true;
    }
}
