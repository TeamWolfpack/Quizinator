package com.seniordesign.wolfpack.quizinator.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.Card.CardDataSource;
import com.seniordesign.wolfpack.quizinator.Database.Rules.Rules;
import com.seniordesign.wolfpack.quizinator.Database.Rules.RulesDataSource;
import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.WifiDirect.ConnectionService;
import com.seniordesign.wolfpack.quizinator.WifiDirect.DeviceDetailFragment;
import com.seniordesign.wolfpack.quizinator.WifiDirect.DeviceListFragment;
import com.seniordesign.wolfpack.quizinator.WifiDirect.WiFiDirectBroadcastReceiver;
import com.seniordesign.wolfpack.quizinator.WifiDirect.WifiDirectApp;

public class HostGameActivity
        extends AppCompatActivity
        implements DeviceListFragment.DeviceActionListener {

    private static final String TAG = "ACT_HG";

    private Rules rulesForGame;
    private Card currentCardForGame;

    private WifiDirectApp wifiDirectApp;
    private IntentFilter mIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_game);

        wifiDirectApp = (WifiDirectApp)getApplication();
        wifiDirectApp.mHomeActivity = this;
        wifiDirectApp.mIsServer = getIntent().getExtras().getBoolean("isServer");

        Log.d(TAG, "onCreate: Is Server " + wifiDirectApp.mIsServer); //TODO remove later

        if (wifiDirectApp.mIsServer) {
            setTitle("Host Game");
        } else {
            setTitle("Join Game");
            findViewById(R.id.start_game_settings).setVisibility(View.GONE);
        }

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

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    /*
     * @author leonardj (10/24/16)
     */
    @Override
    protected void onStart(){
        super.onStart();
        //Initiate Discovery
        if( !wifiDirectApp.isP2pEnabled() ){
            Toast.makeText(this, R.string.p2p_off_warning, Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(TAG, "onStart: P2P enabled - " + wifiDirectApp.isP2pEnabled()); //TODO remove later

        final DeviceListFragment fragment =
                (DeviceListFragment)getFragmentManager().findFragmentById(R.id.frag_list);
        fragment.onInitiateDiscovery();

        if (wifiDirectApp.mP2pMan == null) {
            Toast.makeText(this, "mP2p manager is null",
                    Toast.LENGTH_SHORT).show();
        } else if (wifiDirectApp.mP2pChannel == null) {
            Toast.makeText(this, "mP2p channel is null",
                    Toast.LENGTH_SHORT).show();
        }




        wifiDirectApp.mP2pMan.discoverPeers(wifiDirectApp.mP2pChannel,
                new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
//                Toast.makeText(HostGameActivity.this,
//                        "Discovery Initiated", Toast.LENGTH_SHORT).show();
                if (wifiDirectApp.mIsServer) {
                    wifiDirectApp.mP2pMan.requestGroupInfo(wifiDirectApp.mP2pChannel, new WifiP2pManager.GroupInfoListener() {
                        @Override
                        public void onGroupInfoAvailable(WifiP2pGroup group) {
                        if (group != null) {
                            Log.d(TAG, "onStart: group != null");
                            wifiDirectApp.mP2pMan.removeGroup(wifiDirectApp.mP2pChannel, new WifiP2pManager.ActionListener() {
                                @Override
                                public void onSuccess() {
                                    Log.d(TAG, "onStart: removeGroup Success");

                                    wifiDirectApp.mP2pMan.createGroup(wifiDirectApp.mP2pChannel, new WifiP2pManager.ActionListener() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d(TAG, "onStart: createGroup Success");
                                        }

                                        @Override
                                        public void onFailure(int reason) {
                                            Log.d(TAG, "onStart: createGroup Fail: " + reason);
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(int reason) {
                                    Log.d(TAG, "onStart: removeGroup Fail: " + reason);
                                }
                            });
                        } else {
                            Log.d(TAG, "onStart: group == null");
                            wifiDirectApp.mP2pMan.createGroup(wifiDirectApp.mP2pChannel, new WifiP2pManager.ActionListener() {
                                @Override
                                public void onSuccess() {
                                    Log.d(TAG, "onStart: createGroup Success");
                                }

                                @Override
                                public void onFailure(int reason) {
                                    Log.d(TAG, "onStart: createGroup Fail: " + reason);
                                }
                            });
                        }
                        }
                    });
                } else {
                    Log.d(TAG, "onStart: you are a client"); //TODO remove later
                }

            }

            @Override
            public void onFailure(int reasonCode) {
                fragment.clearPeers();
                Toast.makeText(HostGameActivity.this,
                    "Connect failed. Retry.", Toast.LENGTH_SHORT).show();
//                Toast.makeText(HostGameActivity.this,
//                        "Discovery Failed, try again... ", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onStart: discoverPeers failed, reason: " + reasonCode); //TODO remove later
            }
        });
    }

    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        //receiver = new WiFiDirectBroadcastReceiver(wifiDirectApp.mP2pMan, wifiDirectApp.mP2pChannel, this);
        //receiver = new WiFiDirectBroadcastReceiver();

        Log.d(TAG, "onResume Called"); //TODO remove later

        wifiDirectApp.mReceiver = new WiFiDirectBroadcastReceiver(
                wifiDirectApp, this);

        registerReceiver(wifiDirectApp.mReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause Called"); //TODO remove later
        unregisterReceiver(wifiDirectApp.mReceiver);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        Log.d(TAG, "onDestroy Called"); //TODO remove later

        wifiDirectApp.mP2pMan.requestGroupInfo(wifiDirectApp.mP2pChannel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup group) {
                if (group != null) {
                    Log.d(TAG, "group != null");
                    wifiDirectApp.mP2pMan.removeGroup(wifiDirectApp.mP2pChannel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "removeGroup Success");
                        }

                        @Override
                        public void onFailure(int reason) {
                            Log.d(TAG, "removeGroup Fail: " + reason);
                        }
                    });
                }
            }
        });



//        wifiDirectApp.mP2pInfo.isGroupOwner = false;
        //unregisterReceiver(wifiDirectApp.mReceiver);
        //wifiDirectApp.mP2pMan.removeGroup(wifiDirectApp.mP2pChannel, null);
    }

    /**
     * process WIFI_P2P_THIS_DEVICE_CHANGED_ACTION intent, refresh this device.
     */
    public void updateThisDevice(final WifiP2pDevice device){
        Log.d(TAG, "updateThisDevice: device name " + device.deviceName); //TODO remove later
        Log.d(TAG, "updateThisDevice: device address " + device.deviceAddress); //TODO remove later
        Log.d(TAG, "updateThisDevice: is group owner: " + device.isGroupOwner()); //TODO remove later

        runOnUiThread(new Runnable() {
            @Override public void run() {
                Log.d(TAG, "updateThisDevice: Runnable: update device in list fragment"); //TODO remove later

                DeviceListFragment fragment = (DeviceListFragment)getFragmentManager().findFragmentById(R.id.frag_list);
                if (fragment != null) {
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
        Log.d(TAG, "resetData: reseting the data"); //TODO remove later
        runOnUiThread(new Runnable() {
            @Override public void run() {
                DeviceListFragment fragmentList =
                        (DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list);
                DeviceDetailFragment fragmentDetails =
                        (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
                if (fragmentList != null) {
                    Log.d(TAG, "resetData: clearing peer list in fragment list"); //TODO remove later
                    fragmentList.clearPeers();
                }
                if (fragmentDetails != null) {
                    Log.d(TAG, "resetData: reseting view in fragment detail"); //TODO remove later
                    fragmentDetails.resetViews();
                }
            }
        });
    }

    /**
     * handle p2p connection available, update UI.
     */
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        Log.d(TAG, "onConnectionInfoAvailable: sending the info to the fragment detail"); //TODO remove later
        Log.d(TAG, "onConnectionInfoAvailable: info - " + info.toString()); //TODO remove later
        Log.d(TAG, "onConnectionInfoAvailable: group owner - " + info.isGroupOwner); //TODO remove later
        runOnUiThread(new Runnable() {
            @Override public void run() {
                DeviceDetailFragment fragmentDetails =
                        (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
                fragmentDetails.onConnectionInfoAvailable(info);
            }
        });
    }

    /**
     * update the device list fragment.
     */
    public void onPeersAvailable(final WifiP2pDeviceList peerList){
        Log.d(TAG, "onPeersAvailable: peer list available"); //TODO remove later

        for (WifiP2pDevice device : peerList.getDeviceList()) {
            Log.d(TAG, "onPeersAvailable: device in list - " + device.toString()); //TODO remove later
            Log.d(TAG, "onPeersAvailable: device is group owner - " + device.isGroupOwner()); //TODO remove later
        }

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
        Toast.makeText(this, "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",Toast.LENGTH_LONG).show();
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
        if(!wifiDirectApp.mP2pConnected ){
            Toast.makeText(this, "You are not connected to anyone",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        runOnUiThread(new Runnable() {
            @Override public void run() {
                Intent i = wifiDirectApp.
                        getLaunchActivityIntent(
                                MultiplayerGameplayActivity.class, null);
                i.getExtras().putString("Rules", new Gson().toJson(rules));
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
            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list);
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
     * user clicked connect button after discover peers.
     */
    @Override
    public void connect(WifiP2pConfig config) {
        // perform p2p connect upon users click the connect button.
        // after connection, manager request connection info.

        if (wifiDirectApp.mIsServer) {
            wifiDirectApp.mP2pMan.requestGroupInfo(wifiDirectApp.mP2pChannel, new WifiP2pManager.GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    if (group != null) {
                        Log.d(TAG, "group != null");
//                        wifiDirectApp.mP2pMan.removeGroup(wifiDirectApp.mP2pChannel, new WifiP2pManager.ActionListener() {
//                            @Override
//                            public void onSuccess() {
//                                Log.d(TAG, "removeGroup Success");
//
//                                wifiDirectApp.mP2pMan.createGroup(wifiDirectApp.mP2pChannel, new WifiP2pManager.ActionListener() {
//                                    @Override
//                                    public void onSuccess() {
//                                        Log.d(TAG, "createGroup Success");
//                                    }
//
//                                    @Override
//                                    public void onFailure(int reason) {
//                                        Log.d(TAG, "createGroup Fail: " + reason);
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onFailure(int reason) {
//                                Log.d(TAG, "removeGroup Fail: " + reason);
//                            }
//                        });
                    } else {
                        Log.d(TAG, "group == null");
                        wifiDirectApp.mP2pMan.createGroup(wifiDirectApp.mP2pChannel, new WifiP2pManager.ActionListener() {
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
        } else {
            wifiDirectApp.mP2pMan.connect(wifiDirectApp.mP2pChannel, config,
                    new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                            Toast.makeText(HostGameActivity.this, "Client: Connect success..", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int reason) {
                            Toast.makeText(HostGameActivity.this, "Client: Connect failed. Retry.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void disconnect() {
        Log.d(TAG, "disconnect: disconnect was called"); //TODO remove later

        final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
        if (fragment != null) {
            Log.d(TAG, "disconnect: resetting the detail fragment"); //TODO remove later
            fragment.resetViews();
        }

        wifiDirectApp.mP2pMan.removeGroup(wifiDirectApp.mP2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(HostGameActivity.this, "disconnect failed.." + reasonCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "disconnect: resetting the detail fragment view to GONE"); //TODO remove later
                fragment.getView().setVisibility(View.GONE);
            }
        });
    }

    public boolean startGameSettingsActivity(View v){
        if(!wifiDirectApp.mP2pConnected ){
            Toast.makeText(this, "You are not connected to anyone",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        //PTPLog.d(TAG, "startChatActivity : start chat activity fragment..." + initMsg);
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
