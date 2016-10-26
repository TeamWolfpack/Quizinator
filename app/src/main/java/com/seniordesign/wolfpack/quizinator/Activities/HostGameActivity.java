package com.seniordesign.wolfpack.quizinator.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.seniordesign.wolfpack.quizinator.R;
import com.seniordesign.wolfpack.quizinator.WifiDirect.ConnectionService;
import com.seniordesign.wolfpack.quizinator.WifiDirect.DeviceDetailFragment;
import com.seniordesign.wolfpack.quizinator.WifiDirect.DeviceListFragment;
import com.seniordesign.wolfpack.quizinator.WifiDirect.WifiDirectApp;

import static com.seniordesign.wolfpack.quizinator.WifiDirect.Constants.ACT_CREATE;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.Constants.CAT_LOCATION;
import static com.seniordesign.wolfpack.quizinator.WifiDirect.Constants.LAB_HOME;

public class HostGameActivity extends AppCompatActivity implements DeviceListFragment.DeviceActionListener {

    private WifiDirectApp mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_game);
        setTitle("Host Game");

        mApp = (WifiDirectApp)getApplication();
        mApp.mHomeActivity = this;

        // If service not started yet, start it.
        Intent serviceIntent = new Intent(this, ConnectionService.class);
        startService(serviceIntent);  // start the connection service
    }

    /*
     * @author leonardj (10/24/16)
     */
    @Override
    protected void onStart(){
        super.onStart();
        //Initiate Discovery

//        //leonardj - isP2pEnabled() is always false for some reason
//        if( !mApp.isP2pEnabled() ){
//            Toast.makeText(this, R.string.p2p_off_warning, Toast.LENGTH_LONG).show();
//            return;
//        }

        final DeviceListFragment fragment = (DeviceListFragment)getFragmentManager().findFragmentById(R.id.frag_list);
        fragment.onInitiateDiscovery();

        if (mApp.mP2pMan == null) {
            Toast.makeText(this, "mP2pMan is null", Toast.LENGTH_SHORT).show();
        } else if (mApp.mP2pChannel == null) {
            Toast.makeText(this, "mP2pChannel is null", Toast.LENGTH_SHORT).show();
        }

        mApp.mP2pMan.discoverPeers(mApp.mP2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(HostGameActivity.this, "Discovery Initiated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                fragment.clearPeers();
                Toast.makeText(HostGameActivity.this, "Discovery Failed, try again... ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * process WIFI_P2P_THIS_DEVICE_CHANGED_ACTION intent, refresh this device.
     */
    public void updateThisDevice(final WifiP2pDevice device){
        runOnUiThread(new Runnable() {
            @Override public void run() {
                DeviceListFragment fragment = (DeviceListFragment)getFragmentManager().findFragmentById(R.id.frag_list);
                fragment.updateThisDevice(device);
            }
        });
    }

    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    public void resetData() {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list);
                DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
                if (fragmentList != null) {
                    fragmentList.clearPeers();
                }
                if (fragmentDetails != null) {
                    fragmentDetails.resetViews();
                }
            }
        });
    }

    /**
     * handle p2p connection available, update UI.
     */
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
                fragmentDetails.onConnectionInfoAvailable(info);
            }
        });
    }

    /**
     * update the device list fragment.
     */
    public void onPeersAvailable(final WifiP2pDeviceList peerList){
        runOnUiThread(new Runnable() {
            @Override public void run() {
                DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list);
                fragmentList.onPeersAvailable(mApp.mPeers);  // use application cached list.
                DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);

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
                .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        AlertDialog info = builder.create();
        info.show();
    }

    /**
     * user taps on peer from discovered list of peers, show this peer's detail.
     */
    @Override
    public void showDetails(WifiP2pDevice device) {
        DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
        fragment.showDetails(device);
    }

    @Override
    public void cancelDisconnect() {
        /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (mApp.mP2pMan != null) {
            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager().findFragmentById(R.id.frag_list);
            if (fragment.getDevice() == null || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                disconnect();
            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE || fragment.getDevice().status == WifiP2pDevice.INVITED) {
                mApp.mP2pMan.cancelConnect(mApp.mP2pChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(HostGameActivity.this, "Aborting connection", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(HostGameActivity.this, "cancelConnect: request failed. Please try again.. ", Toast.LENGTH_SHORT).show();
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
        // perform p2p connect upon users click the connect button. after connection, manager request connection info.
        mApp.mP2pMan.connect(mApp.mP2pChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                Toast.makeText(HostGameActivity.this, "Connect success..", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(HostGameActivity.this, "Connect failed. Retry.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void disconnect() {
        final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);
        fragment.resetViews();
        mApp.mP2pMan.removeGroup(mApp.mP2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(HostGameActivity.this, "disconnect failed.." + reasonCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                fragment.getView().setVisibility(View.GONE);
            }
        });
    }
}
